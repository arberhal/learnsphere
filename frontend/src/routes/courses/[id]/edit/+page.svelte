<script>
	import { enhance } from '$app/forms';
	import { goto, invalidateAll } from '$app/navigation';
	import { writable } from 'svelte/store';

	let { data, form } = $props();

	// State
	const showDeleteCourseConfirm = writable(false);
	const isDeleting = writable(false);
	const editingLessonId = writable(null);
	const showAddLesson = writable(false);

	// New lesson form
	const newLesson = writable({
		title: '',
		content: '',
		videoUrl: '',
		order: data.lessons.length + 1
	});

	function handleCourseUpdateEnhance() {
		return async ({ result, update }) => {
			if (result.type === 'success') {
				await invalidateAll();
			}
			await update();
		};
	}

	function handleCourseDeleteEnhance() {
		isDeleting.set(true);

		return async ({ result }) => {
			if (result.type === 'redirect') {
				goto(result.location);
			} else {
				isDeleting.set(false);
				showDeleteCourseConfirm.set(false);
			}
		};
	}

	function handleLessonActionEnhance() {
		return async ({ result, update }) => {
			if (result.type === 'success') {
				editingLessonId.set(null);
				showAddLesson.set(false);
				newLesson.set({
					title: '',
					content: '',
					videoUrl: '',
					order: data.lessons.length + 1
				});
				await invalidateAll();
			}
			await update();
		};
	}

	function startEditLesson(lessonId) {
		editingLessonId.set(lessonId);
	}

	function cancelEditLesson() {
		editingLessonId.set(null);
	}

	function startAddLesson() {
		showAddLesson.set(true);
		newLesson.update(lesson => ({
			...lesson,
			order: data.lessons.length + 1
		}));
	}

	function cancelAddLesson() {
		showAddLesson.set(false);
		newLesson.set({
			title: '',
			content: '',
			videoUrl: '',
			order: data.lessons.length + 1
		});
	}
</script>

<!-- Converted all styles to Tailwind CSS -->
<div class="max-w-5xl mx-auto px-4 py-8">
	<!-- Header -->
	<nav aria-label="breadcrumb" class="mb-6">
		<ol class="flex items-center gap-2 text-sm text-gray-600">
			<li><a href="/courses" class="text-blue-600 hover:underline">Courses</a></li>
			<li>/</li>
			<li><a href="/courses/{data.course.id}" class="text-blue-600 hover:underline">{data.course.title}</a></li>
			<li>/</li>
			<li class="text-gray-900 font-medium" aria-current="page">Edit</li>
		</ol>
	</nav>

	<div class="flex justify-between items-center mb-6">
		<h1 class="text-3xl font-bold text-gray-900">Edit Course</h1>
		<button 
			type="button" 
			class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
			onclick={() => showDeleteCourseConfirm.set(true)}
			onkeydown={(e) => e.key === 'Enter' && showDeleteCourseConfirm.set(true)}
		>
			Delete Course
		</button>
	</div>

	<!-- Success/Error Messages -->
	{#if form?.success || form?.lessonSuccess}
		<div class="mb-6 p-4 bg-green-50 border border-green-200 text-green-800 rounded-lg">
			{form?.message || 'Changes saved successfully!'}
		</div>
	{/if}

	{#if form?.error || form?.lessonError}
		<div class="mb-6 p-4 bg-red-50 border border-red-200 text-red-800 rounded-lg">
			{form?.error || form?.lessonError}
		</div>
	{/if}

	<!-- Course Info Section -->
	<div class="mb-6 bg-white rounded-lg border border-gray-200 shadow-sm">
		<div class="px-6 py-4 border-b border-gray-200">
			<h2 class="text-lg font-semibold text-gray-900">Course Information</h2>
		</div>
		<div class="p-6">
			<form method="POST" action="?/updateCourse" use:enhance={handleCourseUpdateEnhance}>
				<div class="mb-4">
					<label for="title" class="block text-sm font-medium text-gray-700 mb-2">
						Course Title *
					</label>
					<input
						type="text"
						id="title"
						name="title"
						class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 {form?.errors?.title ? 'border-red-500' : ''}"
						value={data.course.title}
						required
					/>
					{#if form?.errors?.title}
						<p class="mt-1 text-sm text-red-600">{form.errors.title}</p>
					{/if}
				</div>

				<div class="mb-4">
					<label for="description" class="block text-sm font-medium text-gray-700 mb-2">
						Description *
					</label>
					<textarea
						id="description"
						name="description"
						class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 {form?.errors?.description ? 'border-red-500' : ''}"
						rows="4"
						required>{data.course.description}</textarea
					>
					{#if form?.errors?.description}
						<p class="mt-1 text-sm text-red-600">{form.errors.description}</p>
					{/if}
				</div>

				<button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
					Save Course Info
				</button>
			</form>
		</div>
	</div>

	<!-- Lessons Section -->
	<div class="bg-white rounded-lg border border-gray-200 shadow-sm">
		<div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
			<h2 class="text-lg font-semibold text-gray-900">Lessons</h2>
			{#if !$showAddLesson}
				<button 
					type="button" 
					class="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
					onclick={startAddLesson}
					onkeydown={(e) => e.key === 'Enter' && startAddLesson()}
				>
					+ Add Lesson
				</button>
			{/if}
		</div>
		<div class="p-6">
			<!-- Add New Lesson Form -->
			{#if $showAddLesson}
				<div class="mb-6 p-6 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
					<h3 class="text-base font-semibold text-gray-900 mb-4">New Lesson</h3>
					<form method="POST" action="?/createLesson" use:enhance={handleLessonActionEnhance}>
						<div class="grid grid-cols-1 md:grid-cols-12 gap-4 mb-4">
							<div class="md:col-span-8">
								<label for="newLessonTitle" class="block text-sm font-medium text-gray-700 mb-2">
									Title *
								</label>
								<input
									type="text"
									id="newLessonTitle"
									name="lessonTitle"
									class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 {$form?.lessonErrors?.lessonTitle ? 'border-red-500' : ''}"
									bind:value={$newLesson.title}
									required
								/>
								{#if $form?.lessonErrors?.lessonTitle}
									<p class="mt-1 text-sm text-red-600">{$form.lessonErrors.lessonTitle}</p>
								{/if}
							</div>

							<div class="md:col-span-4">
								<label for="newLessonOrder" class="block text-sm font-medium text-gray-700 mb-2">
									Order *
								</label>
								<input
									type="number"
									id="newLessonOrder"
									name="lessonOrder"
									class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
									bind:value={$newLesson.order}
									required
									min="1"
								/>
							</div>
						</div>

						<div class="mb-4">
							<label for="newLessonVideoUrl" class="block text-sm font-medium text-gray-700 mb-2">
								Video URL (Optional)
							</label>
							<input
								type="url"
								id="newLessonVideoUrl"
								name="lessonVideoUrl"
								class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
								bind:value={$newLesson.videoUrl}
								placeholder="https://youtube.com/embed/..."
							/>
						</div>

						<div class="mb-4">
							<label for="newLessonContent" class="block text-sm font-medium text-gray-700 mb-2">
								Content *
							</label>
							<textarea
								id="newLessonContent"
								name="lessonContent"
								class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 {$form?.lessonErrors?.lessonContent ? 'border-red-500' : ''}"
								bind:value={$newLesson.content}
								rows="6"
								required
							></textarea>
							{#if $form?.lessonErrors?.lessonContent}
								<p class="mt-1 text-sm text-red-600">{$form.lessonErrors.lessonContent}</p>
							{/if}
						</div>

						<div class="flex gap-2">
							<button type="submit" class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors">
								Add Lesson
							</button>
							<button 
								type="button" 
								class="px-3 py-1.5 text-sm bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
								onclick={cancelAddLesson}
								onkeydown={(e) => e.key === 'Enter' && cancelAddLesson()}
							>
								Cancel
							</button>
						</div>
					</form>
				</div>
			{/if}

			<!-- Existing Lessons List -->
			{#if data.lessons.length === 0}
				<p class="text-gray-500 text-center py-8">No lessons yet. Add your first lesson above!</p>
			{:else}
				<div class="flex flex-col gap-4">
					{#each data.lessons as lesson (lesson.id)}
						<div class="p-4 border border-gray-200 rounded-lg bg-white hover:shadow-md transition-shadow">
							{#if $editingLessonId === lesson.id}
								<!-- Edit Mode -->
								<form method="POST" action="?/updateLesson" use:enhance={handleLessonActionEnhance}>
									<input type="hidden" name="lessonId" value={lesson.id} />

									<div class="grid grid-cols-1 md:grid-cols-12 gap-3 mb-3">
										<div class="md:col-span-8">
											<input
												type="text"
												name="lessonTitle"
												class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
												value={lesson.title}
												required
											/>
										</div>

										<div class="md:col-span-4">
											<input
												type="number"
												name="lessonOrder"
												class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
												value={lesson.order}
												required
												min="1"
											/>
										</div>
									</div>

									<div class="mb-3">
										<input
											type="url"
											name="lessonVideoUrl"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
											value={lesson.videoUrl || ''}
											placeholder="Video URL (optional)"
										/>
									</div>

									<div class="mb-3">
										<textarea
											name="lessonContent"
											class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
											rows="4"
											required>{lesson.content}</textarea
										>
									</div>

									<div class="flex gap-2">
										<button type="submit" class="px-3 py-1.5 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors">
											Save
										</button>
										<button
											type="button"
											class="px-3 py-1.5 text-sm bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
											onclick={cancelEditLesson}
											onkeydown={(e) => e.key === 'Enter' && cancelEditLesson()}
										>
											Cancel
										</button>
									</div>
								</form>
							{:else}
								<!-- View Mode -->
								<div class="flex justify-between items-start gap-4">
									<div class="flex-1">
										<div class="flex items-center gap-2 mb-2">
											<span class="px-2 py-1 text-xs font-semibold bg-blue-600 text-white rounded">
												Lesson {lesson.order}
											</span>
											<h3 class="text-base font-semibold text-gray-900">{lesson.title}</h3>
										</div>
										{#if lesson.videoUrl}
											<p class="text-sm text-gray-500 mb-1">📹 Video included</p>
										{/if}
										<p class="text-sm text-gray-600">
											{lesson.content.substring(0, 100)}{lesson.content.length > 100 ? '...' : ''}
										</p>
									</div>

									<div class="flex gap-2 flex-shrink-0">
										<a
											href="/courses/{data.course.id}/lessons/{lesson.id}"
											class="px-3 py-1.5 text-sm border border-blue-600 text-blue-600 rounded-lg hover:bg-blue-50 transition-colors"
										>
											View
										</a>
										<button
											type="button"
											class="px-3 py-1.5 text-sm border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
											onclick={() => startEditLesson(lesson.id)}
											onkeydown={(e) => e.key === 'Enter' && startEditLesson(lesson.id)}
										>
											Edit
										</button>
										<form
											method="POST"
											action="?/deleteLesson"
											use:enhance={handleLessonActionEnhance}
										>
											<input type="hidden" name="lessonId" value={lesson.id} />
											<button
												type="submit"
												class="px-3 py-1.5 text-sm border border-red-600 text-red-600 rounded-lg hover:bg-red-50 transition-colors"
												onkeydown={(e) => e.key === 'Enter' && e.target.click()}
											>
												Delete
											</button>
										</form>
									</div>
								</div>
							{/if}
						</div>
					{/each}
				</div>
			{/if}
		</div>
	</div>

	<!-- Delete Course Confirmation Modal -->
	{#if $showDeleteCourseConfirm}
		<div 
			class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
			onclick={() => showDeleteCourseConfirm.set(false)}
		>
			<div 
				class="max-w-lg w-11/12 bg-white rounded-lg shadow-xl"
				onclick={(e) => e.stopPropagation()}
			>
				<div class="px-6 py-4 border-b border-gray-200">
					<h5 class="text-xl font-semibold text-gray-900">Delete Course</h5>
				</div>
				<div class="p-6">
					<p class="text-gray-700 mb-2">
						Are you sure you want to delete "<strong>{data.course.title}</strong>"?
					</p>
					<p class="text-red-600 text-sm">
						This action cannot be undone. All lessons will also be deleted.
					</p>

					{#if form?.deleteError}
						<div class="mt-4 p-4 bg-red-50 border border-red-200 text-red-800 rounded-lg">
							{form.deleteError}
						</div>
					{/if}
				</div>
				<div class="px-6 py-4 border-t border-gray-200">
					<form method="POST" action="?/deleteCourse" use:enhance={handleCourseDeleteEnhance}>
						<div class="flex gap-2">
							<button 
								type="submit" 
								class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
								disabled={$isDeleting}
							>
								{$isDeleting ? 'Deleting...' : 'Yes, Delete Course'}
							</button>
							<button
								type="button"
								class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors disabled:opacity-50"
								onclick={() => showDeleteCourseConfirm.set(false)}
								onkeydown={(e) => e.key === 'Enter' && showDeleteCourseConfirm.set(false)}
								disabled={$isDeleting}
							>
								Cancel
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	{/if}
</div>
