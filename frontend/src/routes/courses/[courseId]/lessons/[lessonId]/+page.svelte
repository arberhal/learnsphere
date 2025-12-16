<script>
	let { data } = $props();

	const isCompleted = data.progress && data.progress.percent >= 100;
</script>

<h1>{data.lesson.title}</h1>

<p>{data.lesson.content}</p>

{#if data.lesson.videoUrl}
	<video controls width="800">
		<source src={data.lesson.videoUrl} type="video/mp4" />
		Your browser does not support the video tag.
	</video>
{/if}

{#if !isCompleted}
	<form method="POST">
		<input type="hidden" name="courseId" value={data.courseId} />
		<input
			type="hidden"
			name="completedLessons"
			value={(data.progress?.completedLessons ?? 0) + 1}
		/>
		<button type="submit" formaction="?/markCompleted">
			Mark as completed
		</button>
	</form>
{:else}
	<p><strong>Lesson completed ✔</strong></p>
{/if}

{#if data.progress}
	<p>Progress: {Math.min(100, Math.round(data.progress.percent))}%</p>
{/if}
