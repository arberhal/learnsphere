<script>
	import { enhance } from '$app/forms';
	import { goto, invalidateAll } from '$app/navigation';

	let { data, form } = $props();

	// State
	let showDeleteCourseConfirm = $state(false);
	let isDeleting = $state(false);
	let editingLessonId = $state(null);
	let showAddLesson = $state(false);

	// New lesson form
	let newLesson = $state({
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
		isDeleting = true;

		return async ({ result }) => {
			if (result.type === 'redirect') {
				goto(result.location);
			} else {
				isDeleting = false;
				showDeleteCourseConfirm = false;
			}
		};
	}

	function handleLessonActionEnhance() {
		return async ({ result, update }) => {
			if (result.type === 'success') {
				editingLessonId = null;
				showAddLesson = false;
				newLesson = {
					title: '',
					content: '',
					videoUrl: '',
					order: data.lessons.length + 1
				};
				await invalidateAll();
			}
			await update();
		};
	}

	function startEditLesson(lessonId) {
		editingLessonId = lessonId;
	}

	function cancelEditLesson() {
		editingLessonId = null;
	}

	function startAddLesson() {
		showAddLesson = true;
		newLesson.order = data.lessons.length + 1;
	}

	function cancelAddLesson() {
		showAddLesson = false;
		newLesson = {
			title: '',
			content: '',
			videoUrl: '',
			order: data.lessons.length + 1
		};
	}
</script>

<div class="container">
	<!-- Header -->
	<nav aria-label="breadcrumb" class="mb-4">
		<ol class="breadcrumb">
			<li class="breadcrumb-item"><a href="/courses">Courses</a></li>
			<li class="breadcrumb-item"><a href="/courses/{data.course.id}">{data.course.title}</a></li>
			<li class="breadcrumb-item active" aria-current="page">Edit</li>
		</ol>
	</nav>

	<div class="d-flex justify-content-between align-items-center mb-4">
		<h1>Edit Course</h1>
		<button type="button" class="btn btn-danger" on:click={() => (showDeleteCourseConfirm = true)}>
			Delete Course
		</button>
	</div>

	<!-- Success/Error Messages -->
	{#if form?.success || form?.lessonSuccess}
		<div class="alert alert-success alert-dismissible fade show">
			{form?.message || 'Changes saved successfully!'}
		</div>
	{/if}

	{#if form?.error || form?.lessonError}
		<div class="alert alert-danger alert-dismissible fade show">
			{form?.error || form?.lessonError}
		</div>
	{/if}

	<!-- Course Info Section -->
	<div class="card mb-4">
		<div class="card-header">
			<h2 class="h5 mb-0">Course Information</h2>
		</div>
		<div class="card-body">
			<form method="POST" action="?/updateCourse" use:enhance={handleCourseUpdateEnhance}>
				<div class="mb-3">
					<label for="title" class="form-label">Course Title *</label>
					<input
						type="text"
						id="title"
						name="title"
						class="form-control"
						class:is-invalid={form?.errors?.title}
						value={data.course.title}
						required
					/>
					{#if form?.errors?.title}
						<div class="invalid-feedback">{form.errors.title}</div>
					{/if}
				</div>

				<div class="mb-3">
					<label for="description" class="form-label">Description *</label>
					<textarea
						id="description"
						name="description"
						class="form-control"
						class:is-invalid={form?.errors?.description}
						rows="4"
						required>{data.course.description}</textarea
					>
					{#if form?.errors?.description}
						<div class="invalid-feedback">{form.errors.description}</div>
					{/if}
				</div>

				<button type="submit" class="btn btn-primary"> Save Course Info </button>
			</form>
		</div>
	</div>

	<!-- Lessons Section -->
	<div class="card">
		<div class="card-header d-flex justify-content-between align-items-center">
			<h2 class="h5 mb-0">Lessons</h2>
			{#if !showAddLesson}
				<button type="button" class="btn btn-sm btn-primary" on:click={startAddLesson}>
					+ Add Lesson
				</button>
			{/if}
		</div>
		<div class="card-body">
			<!-- Add New Lesson Form -->
			{#if showAddLesson}
				<div class="lesson-form-card mb-4">
					<h3 class="h6 mb-3">New Lesson</h3>
					<form method="POST" action="?/createLesson" use:enhance={handleLessonActionEnhance}>
						<div class="row">
							<div class="col-md-8 mb-3">
								<label for="newLessonTitle" class="form-label">Title *</label>
								<input
									type="text"
									id="newLessonTitle"
									name="lessonTitle"
									class="form-control form-control-sm"
									class:is-invalid={form?.lessonErrors?.lessonTitle}
									bind:value={newLesson.title}
									required
								/>
								{#if form?.lessonErrors?.lessonTitle}
									<div class="invalid-feedback">{form.lessonErrors.lessonTitle}</div>
								{/if}
							</div>

							<div class="col-md-4 mb-3">
								<label for="newLessonOrder" class="form-label">Order *</label>
								<input
									type="number"
									id="newLessonOrder"
									name="lessonOrder"
									class="form-control form-control-sm"
									bind:value={newLesson.order}
									required
									min="1"
								/>
							</div>
						</div>

						<div class="mb-3">
							<label for="newLessonVideoUrl" class="form-label">Video URL (Optional)</label>
							<input
								type="url"
								id="newLessonVideoUrl"
								name="lessonVideoUrl"
								class="form-control form-control-sm"
								bind:value={newLesson.videoUrl}
								placeholder="https://youtube.com/embed/..."
							/>
						</div>

						<div class="mb-3">
							<label for="newLessonContent" class="form-label">Content *</label>
							<textarea
								id="newLessonContent"
								name="lessonContent"
								class="form-control form-control-sm"
								class:is-invalid={form?.lessonErrors?.lessonContent}
								bind:value={newLesson.content}
								rows="6"
								required
							></textarea>
							{#if form?.lessonErrors?.lessonContent}
								<div class="invalid-feedback">{form.lessonErrors.lessonContent}</div>
							{/if}
						</div>

						<div class="d-flex gap-2">
							<button type="submit" class="btn btn-sm btn-success"> Add Lesson </button>
							<button type="button" class="btn btn-sm btn-secondary" on:click={cancelAddLesson}>
								Cancel
							</button>
						</div>
					</form>
				</div>
			{/if}

			<!-- Existing Lessons List -->
			{#if data.lessons.length === 0}
				<p class="text-muted text-center py-4">No lessons yet. Add your first lesson above!</p>
			{:else}
				<div class="lessons-list">
					{#each data.lessons as lesson (lesson.id)}
						<div class="lesson-item">
							{#if editingLessonId === lesson.id}
								<!-- Edit Mode -->
								<form method="POST" action="?/updateLesson" use:enhance={handleLessonActionEnhance}>
									<input type="hidden" name="lessonId" value={lesson.id} />

									<div class="row">
										<div class="col-md-8 mb-2">
											<input
												type="text"
												name="lessonTitle"
												class="form-control form-control-sm"
												value={lesson.title}
												required
											/>
										</div>

										<div class="col-md-4 mb-2">
											<input
												type="number"
												name="lessonOrder"
												class="form-control form-control-sm"
												value={lesson.order}
												required
												min="1"
											/>
										</div>
									</div>

									<div class="mb-2">
										<input
											type="url"
											name="lessonVideoUrl"
											class="form-control form-control-sm"
											value={lesson.videoUrl || ''}
											placeholder="Video URL (optional)"
										/>
									</div>

									<div class="mb-2">
										<textarea
											name="lessonContent"
											class="form-control form-control-sm"
											rows="4"
											required>{lesson.content}</textarea
										>
									</div>

									<div class="d-flex gap-2">
										<button type="submit" class="btn btn-sm btn-success"> Save </button>
										<button
											type="button"
											class="btn btn-sm btn-secondary"
											on:click={cancelEditLesson}
										>
											Cancel
										</button>
									</div>
								</form>
							{:else}
								<!-- View Mode -->
								<div class="d-flex justify-content-between align-items-start">
									<div class="flex-grow-1">
										<div class="d-flex align-items-center gap-2 mb-2">
											<span class="badge bg-primary">Lesson {lesson.order}</span>
											<h3 class="h6 mb-0">{lesson.title}</h3>
										</div>
										{#if lesson.videoUrl}
											<p class="small text-muted mb-1">📹 Video included</p>
										{/if}
										<p class="small text-muted mb-0">
											{lesson.content.substring(0, 100)}{lesson.content.length > 100 ? '...' : ''}
										</p>
									</div>

									<div class="d-flex gap-2">
										<a
											href="/courses/{data.course.id}/lessons/{lesson.id}"
											class="btn btn-sm btn-outline-primary"
										>
											View
										</a>
										<button
											type="button"
											class="btn btn-sm btn-outline-secondary"
											on:click={() => startEditLesson(lesson.id)}
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
												class="btn btn-sm btn-outline-danger"
												on:click={(e) => {
													if (!confirm('Delete this lesson?')) {
														e.preventDefault();
													}
												}}
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
	{#if showDeleteCourseConfirm}
		<div class="modal-overlay" on:click={() => (showDeleteCourseConfirm = false)}>
			<div class="modal-dialog" on:click={(e) => e.stopPropagation()}>
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">Delete Course</h5>
					</div>
					<div class="modal-body">
						<p>Are you sure you want to delete "<strong>{data.course.title}</strong>"?</p>
						<p class="text-danger">
							This action cannot be undone. All lessons will also be deleted.
						</p>

						{#if form?.deleteError}
							<div class="alert alert-danger">
								{form.deleteError}
							</div>
						{/if}
					</div>
					<div class="modal-footer">
						<form method="POST" action="?/deleteCourse" use:enhance={handleCourseDeleteEnhance}>
							<button type="submit" class="btn btn-danger" disabled={isDeleting}>
								{isDeleting ? 'Deleting...' : 'Yes, Delete Course'}
							</button>
							<button
								type="button"
								class="btn btn-secondary"
								on:click={() => (showDeleteCourseConfirm = false)}
								disabled={isDeleting}
							>
								Cancel
							</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	{/if}
</div>

<style>
	.container {
		max-width: 1000px;
		margin: 0 auto;
		padding: 2rem;
	}

	.breadcrumb {
		background: none;
		padding: 0;
	}

	.breadcrumb-item a {
		color: #2196f3;
		text-decoration: none;
	}

	.breadcrumb-item a:hover {
		text-decoration: underline;
	}

	.lesson-form-card {
		background: #f8f9fa;
		padding: 1.5rem;
		border-radius: 8px;
		border: 2px dashed #dee2e6;
	}

	.lessons-list {
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}

	.lesson-item {
		padding: 1rem;
		border: 1px solid #dee2e6;
		border-radius: 8px;
		background: white;
	}

	.lesson-item:hover {
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	}

	/* Modal Styles */
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}

	.modal-dialog {
		max-width: 500px;
		width: 90%;
	}

	.modal-content {
		background: white;
		border-radius: 8px;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	}

	.modal-header {
		padding: 1.5rem;
		border-bottom: 1px solid #dee2e6;
	}

	.modal-title {
		margin: 0;
		font-size: 1.25rem;
	}

	.modal-body {
		padding: 1.5rem;
	}

	.modal-footer {
		padding: 1.5rem;
		border-top: 1px solid #dee2e6;
	}

	.modal-footer form {
		display: flex;
		gap: 0.5rem;
	}
</style>
