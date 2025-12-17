<script>
	let { data } = $props();
	let courses = $derived(data.courses || []);
</script>

<div class="container mt-4">
	<div class="d-flex justify-content-between align-items-center mb-4">
		<h1>My Courses</h1>
		<a href="/courses/create" class="btn btn-primary">Create New Course</a>
	</div>

	{#if data.error}
		<div class="alert alert-danger" role="alert">
			{data.error}
		</div>
	{/if}

	{#if courses.length === 0}
		<div class="card">
			<div class="card-body text-center py-5">
				<h5 class="card-title">No Courses Yet</h5>
				<p class="card-text text-muted">Get started by creating your first course!</p>
				<a href="/courses/create" class="btn btn-primary">Create Course</a>
			</div>
		</div>
	{:else}
		<div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
			{#each courses as course}
				<div class="col">
					<div class="card h-100">
						<div class="card-body">
							<h5 class="card-title">{course.title}</h5>
							<p class="card-text">{course.description}</p>
						</div>
						<div class="card-footer bg-transparent">
							<a href="/courses/{course.id}" class="btn btn-sm btn-primary">
								View Course
							</a>
							<a href="/courses/{course.id}/edit" class="btn btn-sm btn-outline-secondary">
								Edit
							</a>
						</div>
					</div>
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
	.card {
		transition: transform 0.2s;
	}

	.card:hover {
		transform: translateY(-5px);
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
	}
</style>