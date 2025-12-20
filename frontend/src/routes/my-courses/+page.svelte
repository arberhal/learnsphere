<script>
  import ProgressBadge from '$lib/components/ProgressBadge.svelte';
  
  let { data } = $props();
  
  let myCourses = $derived(data.myCourses || []);
  let progressMap = $derived(data.progressMap || {});
  let isTeacher = $derived(data.isTeacher);
</script>

<div class="container">
  <div class="header">
    <h1>My Courses</h1>
    <p class="subtitle">
      {#if isTeacher}
        Courses you've created
      {:else}
        Courses where you've made progress
      {/if}
    </p>
  </div>

  {#if myCourses.length === 0}
    <div class="empty-state">
      <div class="empty-icon">📖</div>
      {#if isTeacher}
        <h2>No courses created yet</h2>
        <p>Create your first course to get started!</p>
        <a href="/courses/create" class="btn-browse">Create Course</a>
      {:else}
        <h2>No courses started yet</h2>
        <p>Enroll in a course to see your progress here!</p>
        <a href="/courses" class="btn-browse">Browse Courses</a>
      {/if}
    </div>
  {:else}
    <div class="courses-list">
      {#each myCourses as course}
        {#if isTeacher}
          <!-- TEACHER VIEW -->
          <a href="/courses/{course.id}" class="course-card">
            <div class="course-info">
              <div class="course-header">
                <h3>{course.title}</h3>
                <span class="badge-teacher">
                  <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3zM3.31 9.397L5 10.12v4.102a8.969 8.969 0 00-1.05-.174 1 1 0 01-.89-.89 11.115 11.115 0 01.25-3.762zM9.3 16.573A9.026 9.026 0 007 14.935v-3.957l1.818.78a3 3 0 002.364 0l5.508-2.361a11.026 11.026 0 01.25 3.762 1 1 0 01-.89.89 8.968 8.968 0 00-5.35 2.524 1 1 0 01-1.4 0zM6 18a1 1 0 001-1v-2.065a8.935 8.935 0 00-2-.712V17a1 1 0 001 1z" />
                  </svg>
                  Your Course
                </span>
              </div>
              <p class="course-description">{course.description}</p>
            </div>
            
            <div class="course-stats">
              <div class="stat">
                <svg class="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                <span>{course.totalLessons || 0} {course.totalLessons === 1 ? 'lesson' : 'lessons'}</span>
              </div>
            </div>

            <div class="course-footer">
              <button 
                onclick={(e) => { e.stopPropagation(); window.location.href = '/courses/' + course.id + '/edit'; }}
                class="btn-edit"
              >
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                Edit Course
              </button>
            </div>
          </a>
        {:else}
          <!-- STUDENT VIEW -->
          {@const progress = progressMap[course.id] || { completedLessons: 0, percent: 0, status: 'NOT_STARTED' }}
          <a href="/courses/{course.id}" class="course-card">
            <div class="course-info">
              <div class="course-header">
                <h3>{course.title}</h3>
                <ProgressBadge 
                  status={progress.status}
                  completedLessons={progress.completedLessons}
                  totalLessons={course.totalLessons || 0}
                />
              </div>
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
              {#if progress.status === 'COMPLETED'}
                <span class="badge-completed">✓ Completed</span>
              {:else if progress.status === 'IN_PROGRESS'}
                <button class="btn-continue">Continue Learning →</button>
              {:else}
                <button class="btn-start">Start Course →</button>
              {/if}
            </div>
          </a>
        {/if}
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

  .course-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
    margin-bottom: 0.5rem;
  }

  .course-info h3 {
    color: #333;
    font-size: 1.5rem;
    margin: 0;
    flex: 1;
  }

  .course-description {
    color: #666;
    margin: 0;
    line-height: 1.6;
  }

  .badge-teacher {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    background: #fbbf24;
    color: #78350f;
    border-radius: 6px;
    font-weight: 600;
    font-size: 0.9rem;
  }

  .course-stats {
    display: flex;
    gap: 2rem;
  }

  .stat {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #666;
    font-size: 0.9rem;
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

  .btn-continue,
  .btn-start,
  .btn-edit {
    padding: 0.75rem 1.5rem;
    background: #667eea;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
  }

  .btn-continue:hover,
  .btn-start:hover,
  .btn-edit:hover {
    background: #5568d3;
  }

  .btn-start {
    background: #48bb78;
  }

  .btn-start:hover {
    background: #38a169;
  }

  .btn-edit {
    background: #6b7280;
  }

  .btn-edit:hover {
    background: #4b5563;
  }

  @media (max-width: 768px) {
    .container {
      padding: 1rem;
    }

    .course-card {
      padding: 1.5rem;
    }

    .course-header {
      flex-direction: column;
      align-items: flex-start;
    }
  }
</style>