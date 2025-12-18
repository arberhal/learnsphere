<script>
  let { data } = $props();
  
  let myCourses = $derived(data.myCourses || []);
  let progressMap = $derived(data.progressMap || {});
</script>

<div class="container">
  <div class="header">
    <h1>My Courses</h1>
    <p class="subtitle">Courses where you've made progress</p>
  </div>

  {#if myCourses.length === 0}
    <div class="empty-state">
      <div class="empty-icon">📖</div>
      <h2>No courses started yet</h2>
      <p>Start a course to see your progress here!</p>
      <a href="/courses" class="btn-browse">Browse Courses</a>
    </div>
  {:else}
    <div class="courses-list">
      {#each myCourses as course}
        {@const progress = progressMap[course.id] || { completedLessons: 0, percent: 0 }}
        <a href="/courses/{course.id}" class="course-card">
          <div class="course-info">
            <h3>{course.title}</h3>
            <p class="course-description">{course.description}</p>
          </div>
          
          <div class="progress-section">
            <div class="progress-stats">
              <span class="lessons-completed">
                {progress.completedLessons} lessons completed
              </span>
              <span class="progress-percent">
                {Math.round(progress.percent)}%
              </span>
            </div>
            <div class="progress-bar-container">
              <div 
                class="progress-bar" 
                style="width: {progress.percent}%"
                class:completed={progress.percent >= 100}
              ></div>
            </div>
          </div>

          <div class="course-footer">
            {#if progress.percent >= 100}
              <span class="badge-completed">✓ Completed</span>
            {:else}
              <button class="btn-continue">Continue Learning →</button>
            {/if}
          </div>
        </a>
      {/each}
    </div>
  {/if}
</div>

<style>
  .container {
    max-width: 900px;
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
    margin-bottom: 2rem;
  }

  .btn-browse {
    display: inline-block;
    padding: 0.75rem 2rem;
    background: #667eea;
    color: white;
    text-decoration: none;
    border-radius: 6px;
    font-weight: 600;
    transition: background 0.2s;
  }

  .btn-browse:hover {
    background: #5568d3;
  }

  .courses-list {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .course-card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 2rem;
    transition: all 0.3s ease;
    text-decoration: none;
    color: inherit;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .course-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .course-info h3 {
    color: #333;
    font-size: 1.5rem;
    margin: 0 0 0.5rem 0;
  }

  .course-description {
    color: #666;
    margin: 0;
    line-height: 1.6;
  }

  .progress-section {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .progress-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .lessons-completed {
    color: #666;
    font-size: 0.9rem;
  }

  .progress-percent {
    color: #667eea;
    font-weight: 700;
    font-size: 1.1rem;
  }

  .progress-bar-container {
    height: 12px;
    background: #e0e0e0;
    border-radius: 6px;
    overflow: hidden;
  }

  .progress-bar {
    height: 100%;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    transition: width 0.5s ease;
    border-radius: 6px;
  }

  .progress-bar.completed {
    background: linear-gradient(90deg, #28a745 0%, #20c997 100%);
  }

  .course-footer {
    display: flex;
    justify-content: flex-end;
  }

  .badge-completed {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    background: #28a745;
    color: white;
    border-radius: 6px;
    font-weight: 600;
    font-size: 0.9rem;
  }

  .btn-continue {
    padding: 0.75rem 1.5rem;
    background: #667eea;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s;
  }

  .btn-continue:hover {
    background: #5568d3;
  }

  @media (max-width: 768px) {
    .container {
      padding: 1rem;
    }

    .course-card {
      padding: 1.5rem;
    }
  }
</style>