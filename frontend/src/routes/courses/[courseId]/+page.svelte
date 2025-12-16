<script>
    let { data } = $props();
</script>

<h1>{data.course.title}</h1>
<p>{data.course.description}</p>

<h2>Lessons</h2>

{#if data.lessons.length === 0}
    <p>No lessons available.</p>
{:else}
    <ul>
        {#each data.lessons as lesson}
            <li>
                <strong>{lesson.order}. {lesson.title}</strong>
                <br />
                <a href={`/courses/${data.course.id}/lessons/${lesson.id}`}>
                    Open lesson
                </a>
            </li>
        {/each}
    </ul>
{/if}
