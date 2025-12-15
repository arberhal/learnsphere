<script>
    export let data;

    let title = '';
    let description = '';
</script>

<h1>LearnSphere – Courses</h1>

<ul>
    {#each data.courses as course}
        <li>
            <strong>{course.title}</strong> – {course.description}
        </li>
    {/each}
</ul>

<h2>New Course</h2>

<form method="POST">
    <input name="title" placeholder="Title" bind:value={title} />
    <input name="description" placeholder="Description" bind:value={description} />
    <button formaction="?/createCourse">Create</button>
</form>
