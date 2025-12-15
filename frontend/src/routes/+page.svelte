<script>
    export let data;
</script>

<h1>LearnSphere</h1>

<p>Available Courses</p>

{#if data.courses.length === 0}
    <p>No courses available.</p>
{:else}
    <ul>
        {#each data.courses as course}
            <li>
                <h3>{course.title}</h3>
                <p>{course.description}</p>

                <!-- Vorbereitung für Issue 4 -->
                <a href={`/courses/${course.id}`}>
                    View course
                </a>
            </li>
        {/each}
    </ul>
{/if}
