<script>
	import favicon from '$lib/assets/favicon.svg';

	let { children } = $props();
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<nav>
	<ul>
		<li><a href="/">Home</a></li>
		<li><a href="/">Courses</a></li>
		<li><a href="#">Login</a></li>
		<li><a href="#">Register</a></li>
		<a href="/courses/create">Create New Course</a>
	</ul>
</nav>

<hr />

{@render children()}
