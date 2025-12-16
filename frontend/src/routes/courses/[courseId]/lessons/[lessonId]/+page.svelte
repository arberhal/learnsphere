<script>
	let { data } = $props();
</script>

<h1>{data.lesson.title}</h1>

<p>{data.lesson.content}</p>

{#if data.lesson.videoUrl}
	<h3>Video</h3>
	<video controls width="800">
		<source src={data.lesson.videoUrl} type="video/mp4" />
		Your browser does not support the video tag.
	</video>
{:else}
	<p>No video available for this lesson.</p>
{/if}

<p>
	<a href={`/courses/${data.courseId}`}>
		← Back to course
	</a>
</p>
