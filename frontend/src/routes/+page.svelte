<script>
	let { data } = $props();
	let { isAuthenticated } = data;
	  import "../app.css";
</script>

<div class="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
  <div class="text-center">
    <h1 class="text-4xl font-bold tracking-tight text-foreground sm:text-6xl">
      Welcome to LearnSphere
    </h1>
    <p class="mt-6 text-lg leading-8 text-muted-foreground">
      Your modern learning platform for teachers and students
    </p>
    <div class="mt-10 flex items-center justify-center gap-x-6">
      {#if data.isAuthenticated}
        <a href="/courses" class="rounded-lg bg-primary px-6 py-3 text-sm font-semibold text-primary-foreground shadow-sm transition-all hover:bg-primary/90 hover:shadow-md">
          Browse Courses
        </a>
      {:else}
        <a href="/signup" class="rounded-lg bg-primary px-6 py-3 text-sm font-semibold text-primary-foreground shadow-sm transition-all hover:bg-primary/90 hover:shadow-md">
          Get Started
        </a>
        <a href="/login" class="text-sm font-semibold leading-6 text-foreground hover:text-primary">
          Login <span aria-hidden="true">→</span>
        </a>
      {/if}
    </div>
  </div>
</div>
