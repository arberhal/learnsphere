<script>
  let { data } = $props();
</script>

<div class="container">
  <div class="header">
    <a href="/courses" class="back-link">← Back to Courses</a>
  </div>

  <!-- Course Header -->
  <div class="course-header">
    <div class="course-info">
      <h1>{data.course.title}</h1>
      <p class="description">{data.course.description}</p>
    </div>
    
    <div class="action-buttons">
      <a href="/courses/{data.course.id}/edit" class="btn-edit">
        Edit Course
      </a>
    </div>
  </div>

  <!-- Lessons Section -->
  <section class="lessons-section">
    <h2>Lessons</h2>

    {#if data.lessons.length === 0}
      <p class="empty-state">No lessons available yet.</p>
    {:else}
      <ul class="lessons-list">
        {#each data.lessons as lesson}
          <li class="lesson-item">
            <div class="lesson-info">
              <span class="lesson-order">{lesson.order}.</span>
              <strong>{lesson.title}</strong>
            </div>
            <a href={`/courses/${data.course.id}/lessons/${lesson.id}`} class="lesson-link">
              Open lesson →
            </a>
          </li>
        {/each}
      </ul>
    {/if}
  </section>
</div>

<style>
  .container {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem;
  }

  .header {
    margin-bottom: 2rem;
  }

  .back-link {
    color: #2196F3;
    text-decoration: none;
    font-weight: 500;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
  }

  .back-link:hover {
    text-decoration: underline;
  }

  .course-header {
    background: #f9f9f9;
    padding: 2rem;
    border-radius: 8px;
    margin-bottom: 2rem;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 2rem;
  }

  .course-info {
    flex: 1;
  }

  h1 {
    margin: 0 0 1rem 0;
    color: #333;
    font-size: 2rem;
  }

  .description {
    margin: 0;
    color: #666;
    font-size: 1.1rem;
    line-height: 1.6;
  }

  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    min-width: 150px;
  }

  .btn-edit {
    padding: 0.75rem 1.5rem;
    background-color: #2196F3;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;
    text-decoration: none;
    text-align: center;
  }

  .btn-edit:hover {
    background-color: #1976D2;
  }

  /* Lessons Section */
  .lessons-section {
    margin-top: 2rem;
  }

  .lessons-section h2 {
    margin-bottom: 1.5rem;
    color: #333;
  }

  .empty-state {
    text-align: center;
    color: #999;
    padding: 2rem;
    font-style: italic;
  }

  .lessons-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .lesson-item {
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    padding: 1rem 1.5rem;
    margin-bottom: 0.75rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all 0.2s;
  }

  .lesson-item:hover {
    border-color: #2196F3;
    box-shadow: 0 2px 4px rgba(33, 150, 243, 0.1);
  }

  .lesson-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .lesson-order {
    color: #2196F3;
    font-weight: 700;
    font-size: 1.1rem;
  }

  .lesson-link {
    color: #2196F3;
    text-decoration: none;
    font-weight: 500;
    white-space: nowrap;
  }

  .lesson-link:hover {
    text-decoration: underline;
  }

  @media (max-width: 768px) {
    .course-header {
      flex-direction: column;
    }

    .action-buttons {
      width: 100%;
    }

    .action-buttons a {
      width: 100%;
    }
  }
</style>