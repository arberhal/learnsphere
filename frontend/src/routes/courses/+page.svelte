<script>
  let { data } = $props();
  
  let courses = $derived(data.courses || []);
</script>

<div class="container">
  <div class="header">
    <h1>All Courses</h1>
    <p class="subtitle">Browse all available courses</p>
  </div>

  {#if courses.length === 0}
    <div class="empty-state">
      <div class="empty-icon">📚</div>
      <h2>No courses available yet</h2>
      <p>Check back later for new courses!</p>
    </div>
  {:else}
    <div class="courses-grid">
      {#each courses as course}
        <a href="/courses/{course.id}" class="course-card">
          <div class="course-header">
            <h3>{course.title}</h3>
          </div>
          <div class="course-body">
            <p class="course-description">{course.description}</p>
          </div>
          <div class="course-footer">
            <button class="btn-view">View Course →</button>
          </div>
        </a>
      {/each}
    </div>
  {/if}
</div>

<style>
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
  }

  .header {
    margin-bottom: 3rem;
    text-align: center;
  }

  .header h1 {
    font-size: 2.5rem;
    color: #333;
    margin-bottom: 0.5rem;
  }

  .subtitle {
    font-size: 1.1rem;
    color: #666;
  }

  .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
  }

  .empty-state h2 {
    color: #666;
    margin-bottom: 0.5rem;
  }

  .empty-state p {
    color: #999;
  }

  .courses-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 2rem;
  }

  .course-card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    overflow: hidden;
    text-decoration: none;
    color: inherit;
    display: flex;
    flex-direction: column;
  }

  .course-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }

  .course-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 1.5rem;
  }

  .course-header h3 {
    margin: 0;
    font-size: 1.3rem;
  }

  .course-body {
    padding: 1.5rem;
    flex: 1;
  }

  .course-description {
    color: #666;
    line-height: 1.6;
    margin: 0;
  }

  .course-footer {
    padding: 0 1.5rem 1.5rem 1.5rem;
  }

  .btn-view {
    width: 100%;
    padding: 0.75rem;
    background: #667eea;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s;
  }

  .btn-view:hover {
    background: #5568d3;
  }

  @media (max-width: 768px) {
    .courses-grid {
      grid-template-columns: 1fr;
    }
  }
</style>