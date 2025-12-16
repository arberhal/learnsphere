<script>
  import { invalidateAll } from '$app/navigation';
  import { enhance } from '$app/forms';

  let { data, form } = $props();

  let isMarkingComplete = $state(false);

  // Check if current lesson is completed
  let isLessonCompleted = $derived(
    data.progress && data.progress.completedLessons >= data.lesson.order
  );

  function handleCompleteEnhance() {
    isMarkingComplete = true;
    
    return async ({ result, update }) => {
      isMarkingComplete = false;
      
      if (result.type === 'success') {
        await invalidateAll(); // Refresh progress data
      }
      await update();
    };
  }
</script>

<div class="container">
  <!-- Header with navigation -->
  <div class="header">
    <a href="/courses/{data.course.id}" class="back-link">
      ← Back to {data.course.title}
    </a>
  </div>

  <!-- Progress Bar -->
  {#if data.progress}
    <div class="progress-section">
      <div class="progress-info">
        <span class="progress-label">Course Progress</span>
        <span class="progress-percentage">{data.progress.percent.toFixed(0)}%</span>
      </div>
      <div class="progress-bar-container">
        <div 
          class="progress-bar-fill" 
          style="width: {data.progress.percent}%"
        ></div>
      </div>
      <div class="progress-stats">
        {data.progress.completedLessons} of {data.totalLessons} lessons completed
      </div>
    </div>
  {/if}

  <!-- Lesson Content -->
  <article class="lesson-content">
    <div class="lesson-header">
      <span class="lesson-number">Lesson {data.lesson.order}</span>
      <h1>{data.lesson.title}</h1>
      
      {#if isLessonCompleted}
        <span class="completed-badge">✓ Completed</span>
      {/if}
    </div>

    <!-- Video Section -->
    {#if data.lesson.videoUrl && data.lesson.videoUrl.trim() !== ''}
      <div class="video-section">
        <h2>Video</h2>
        <div class="video-container">
          {#if data.lesson.videoUrl.includes('youtube.com') || data.lesson.videoUrl.includes('youtu.be')}
            {@const videoId = data.lesson.videoUrl.includes('youtu.be') 
              ? data.lesson.videoUrl.split('youtu.be/')[1]?.split('?')[0]
              : data.lesson.videoUrl.split('v=')[1]?.split('&')[0]}
            {#if videoId}
              <iframe
                src="https://www.youtube.com/embed/{videoId}"
                title={data.lesson.title}
                frameborder="0"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen
              ></iframe>
            {:else}
              <a href={data.lesson.videoUrl} target="_blank" rel="noopener noreferrer" class="video-link">
                Watch Video →
              </a>
            {/if}
          {:else}
            <a href={data.lesson.videoUrl} target="_blank" rel="noopener noreferrer" class="video-link">
              Watch Video →
            </a>
          {/if}
        </div>
      </div>
    {/if}

    <!-- Lesson Content -->
    <div class="content-section">
      <h2>Lesson Content</h2>
      <div class="content-text">
        {data.lesson.content}
      </div>
    </div>

    <!-- Mark as Complete Button -->
    <div class="completion-section">
      {#if form?.success}
        <div class="success-message">
          ✓ Lesson marked as completed! Progress updated.
        </div>
      {/if}

      {#if form?.error}
        <div class="error-message">
          {form.error}
        </div>
      {/if}

      <form method="POST" use:enhance={handleCompleteEnhance}>
        <button 
          type="submit" 
          class="btn-complete"
          class:completed={isLessonCompleted}
          disabled={isMarkingComplete || isLessonCompleted}
        >
          {#if isMarkingComplete}
            Updating...
          {:else if isLessonCompleted}
            ✓ Completed
          {:else}
            Mark as Completed
          {/if}
        </button>
      </form>

      {#if !isLessonCompleted}
        <p class="completion-hint">
          Complete this lesson to update your progress
        </p>
      {/if}
    </div>
  </article>

  <!-- Lesson Navigation -->
  <nav class="lesson-navigation">
    <h3>Other Lessons</h3>
    <ul class="lessons-list">
      {#each data.allLessons as lesson}
        {@const isCompleted = data.progress && data.progress.completedLessons >= lesson.order}
        <li class="lesson-item" class:current={lesson.id === data.lesson.id}>
          <a href="/courses/{data.course.id}/lessons/{lesson.id}">
            <span class="lesson-nav-number">{lesson.order}.</span>
            <span class="lesson-nav-title">{lesson.title}</span>
            {#if isCompleted}
              <span class="lesson-nav-check">✓</span>
            {/if}
          </a>
        </li>
      {/each}
    </ul>
  </nav>
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

  /* Progress Section */
  .progress-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 1.5rem;
    border-radius: 12px;
    margin-bottom: 2rem;
    color: white;
  }

  .progress-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.75rem;
  }

  .progress-label {
    font-weight: 600;
    font-size: 0.9rem;
    opacity: 0.95;
  }

  .progress-percentage {
    font-weight: 700;
    font-size: 1.5rem;
  }

  .progress-bar-container {
    background: rgba(255, 255, 255, 0.2);
    height: 12px;
    border-radius: 6px;
    overflow: hidden;
    margin-bottom: 0.75rem;
  }

  .progress-bar-fill {
    background: white;
    height: 100%;
    border-radius: 6px;
    transition: width 0.5s ease;
  }

  .progress-stats {
    font-size: 0.9rem;
    opacity: 0.9;
    text-align: center;
  }

  /* Lesson Content */
  .lesson-content {
    background: white;
    border-radius: 8px;
    padding: 2rem;
    margin-bottom: 2rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .lesson-header {
    margin-bottom: 2rem;
    padding-bottom: 1.5rem;
    border-bottom: 2px solid #f0f0f0;
  }

  .lesson-number {
    display: inline-block;
    background: #2196F3;
    color: white;
    padding: 0.25rem 0.75rem;
    border-radius: 4px;
    font-size: 0.85rem;
    font-weight: 600;
    margin-bottom: 0.75rem;
  }

  h1 {
    margin: 0.75rem 0 0 0;
    color: #333;
    font-size: 2rem;
  }

  .completed-badge {
    display: inline-block;
    background: #4CAF50;
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
    margin-top: 1rem;
  }

  /* Video Section */
  .video-section {
    margin-bottom: 2rem;
  }

  .video-section h2 {
    margin-top: 0;
    margin-bottom: 1rem;
    color: #444;
    font-size: 1.25rem;
  }

  .video-container {
    position: relative;
    padding-bottom: 56.25%; /* 16:9 aspect ratio */
    height: 0;
    overflow: hidden;
    border-radius: 8px;
    background: #f0f0f0;
  }

  .video-container iframe {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  .video-link {
    display: block;
    padding: 4rem 2rem;
    text-align: center;
    background: #f5f5f5;
    color: #2196F3;
    text-decoration: none;
    font-weight: 600;
    font-size: 1.1rem;
    border-radius: 8px;
    transition: all 0.2s;
  }

  .video-link:hover {
    background: #e3f2fd;
  }

  /* Content Section */
  .content-section h2 {
    margin-top: 0;
    margin-bottom: 1rem;
    color: #444;
    font-size: 1.25rem;
  }

  .content-text {
    color: #555;
    line-height: 1.8;
    font-size: 1.05rem;
    white-space: pre-wrap;
  }

  /* Completion Section */
  .completion-section {
    margin-top: 3rem;
    padding-top: 2rem;
    border-top: 2px solid #f0f0f0;
    text-align: center;
  }

  .btn-complete {
    padding: 1rem 3rem;
    background: #4CAF50;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 8px rgba(76, 175, 80, 0.3);
  }

  .btn-complete:hover:not(:disabled) {
    background: #45a049;
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(76, 175, 80, 0.4);
  }

  .btn-complete:disabled {
    background: #ccc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }

  .btn-complete.completed {
    background: #66BB6A;
  }

  .completion-hint {
    margin-top: 1rem;
    color: #999;
    font-size: 0.9rem;
  }

  .success-message {
    padding: 1rem;
    background: #e8f5e9;
    color: #2e7d32;
    border-radius: 6px;
    margin-bottom: 1rem;
    font-weight: 500;
  }

  .error-message {
    padding: 1rem;
    background: #ffebee;
    color: #c62828;
    border-radius: 6px;
    margin-bottom: 1rem;
  }

  /* Lesson Navigation */
  .lesson-navigation {
    background: white;
    border-radius: 8px;
    padding: 1.5rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .lesson-navigation h3 {
    margin-top: 0;
    margin-bottom: 1rem;
    color: #333;
    font-size: 1.1rem;
  }

  .lessons-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .lesson-item {
    border-bottom: 1px solid #f0f0f0;
  }

  .lesson-item:last-child {
    border-bottom: none;
  }

  .lesson-item.current {
    background: #e3f2fd;
  }

  .lesson-item a {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.75rem;
    text-decoration: none;
    color: #333;
    transition: background 0.2s;
  }

  .lesson-item a:hover {
    background: #f5f5f5;
  }

  .lesson-item.current a:hover {
    background: #e3f2fd;
  }

  .lesson-nav-number {
    color: #2196F3;
    font-weight: 700;
    min-width: 30px;
  }

  .lesson-nav-title {
    flex: 1;
  }

  .lesson-nav-check {
    color: #4CAF50;
    font-weight: 700;
    font-size: 1.2rem;
  }

  @media (max-width: 768px) {
    .container {
      padding: 1rem;
    }

    h1 {
      font-size: 1.5rem;
    }

    .lesson-content {
      padding: 1.5rem;
    }
  }
</style>