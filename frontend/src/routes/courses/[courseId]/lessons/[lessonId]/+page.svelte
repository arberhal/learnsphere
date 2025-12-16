<script>
    let { data } = $props();
</script>

<h1>{data.lesson.title}</h1>

<p>{data.lesson.content}</p>

<p>
    <a href={`/courses/${data.courseId}`}>
        ← Back to course
    </a>
</p>
