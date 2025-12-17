<script>
	let { data } = $props();
	let { isAuthenticated } = data;
</script>

{#if isAuthenticated}
	<h1>Welcome to Learnsphere!</h1>
	<p>Your Learning Management System for creating and taking courses.</p>
	<div class="mt-4">
		<a href="/courses" class="btn btn-primary">View Courses</a>
	</div>
{:else}
	<div class="container mt-5">
		<div class="row justify-content-center">
			<div class="col-md-6">
				<div class="card">
					<div class="card-header">Welcome to Learnsphere</div>
					<div class="card-body text-center">
						<h5 class="card-title">Get Started</h5>
						<p class="card-text">
							Please log in to access your courses or sign up to create a new account.
						</p>
						<a href="/login" class="btn btn-primary me-2">Login</a>
						<a href="/signup" class="btn btn-outline-primary">Sign Up</a>
					</div>
				</div>
			</div>
		</div>
	</div>
{/if}