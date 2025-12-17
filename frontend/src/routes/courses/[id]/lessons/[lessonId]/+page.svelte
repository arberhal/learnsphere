<script>
  import { enhance } from '$app/forms';
  import { invalidateAll } from '$app/navigation';

  let { data, form } = $props();
  
  let isCompleting = $state(false);

  // Check if this lesson is completed
  let isLessonCompleted = $derived(
    data.progress && data.currentLessonOrder <= data.progress.completedLessons
  );

  function handleCompleteEnhance() {
    isCompleting = true;
    
    return async ({ result, update }) => {
      isCompleting = false;
      
      if (result.type === 'success') {
        await invalidateAll();
      }
      
      await update();
    };
  }

  // Find next lesson
  let nextLesson = $derived(() => {
    const sortedLessons = [...data.allLessons].sort((a, b) => a.order - b.order);
    const currentIndex = sortedLessons.findIndex(l => l.id === data.lesson.id);
    return currentIndex < sortedLessons.length - 1 ? sortedLessons[currentIndex + 1] : null;
  });

  // Find previous lesson
  let prevLesson = $derived(() => {
    const sortedLessons = [...data.allLessons].sort((a, b) => a.order - b.order);
    const currentIndex = sortedLessons.findIndex(l => l.id === data.lesson.id);
    return currentIndex > 0 ? sortedLessons[currentIndex - 1] : null;
  });
</script>

<div class="container">
  <!-- Header with breadcrumb -->
  <nav aria-label="breadcrumb" class="mb-4">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="/courses">Courses</a></li>
      <li class="breadcrumb-item"><a href="/courses/{data.course.id}">{data.course.title}</a></li>
      <li class="breadcrumb-item active" aria-current="page">{data.lesson.title}</li>
    </ol>
  </nav>

  <!-- Success/Error Messages -->
  {#if form?.success}
    <div class="alert alert-success alert-dismissible fade show">
      {form.message}
    </div>
  {/if}

  {#if form?.error}
    <div class="alert alert-danger alert-dismissible fade show">
      {form.error}
    </div>
  {/if}

  <!-- Lesson Header -->
  <div class="lesson-header">
    <div class="d-flex justify-content-between align-items-start mb-4">
      <div>
        <div class="d-flex align-items-center gap-2 mb-2">
          <span class="badge bg-primary">Lesson {data.lesson.order}</span>
          {#if isLessonCompleted}
            <span class="badge bg-success">✓ Completed</span>
          {/if}
        </div>
        <h1>{data.lesson.title}</h1>
      </div>
      
      <div class="d-flex gap-2">
        <!-- Teacher edit button (will hide with roles later) -->
        <a 
          href="/courses/{data.course.id}/edit" 
          class="btn btn-outline-secondary btn-sm"
        >
          Edit Course
        </a>
        <a 
          href="/courses/{data.course.id}" 
          class="btn btn-outline-secondary btn-sm"
        >
          Back to Course
        </a>
      </div>
    </div>

    <!-- Progress Bar -->
    {#if data.progress}
      <div class="progress-container mb-4">
        <div class="d-flex justify-content-between mb-2">
          <span class="text-muted">Course Progress</span>
          <span class="text-muted">
            {data.progress.completedLessons} / {data.allLessons.length} Lessons
          </span>
        </div>
        <div class="progress">
          <div 
            class="progress-bar" 
            role="progressbar" 
            style="width: {data.progress.percent}%"
            aria-valuenow={data.progress.percent}
            aria-valuemin="0" 
            aria-valuemax="100"
          >
            {Math.round(data.progress.percent)}%
          </div>
        </div>
      </div>
    {/if}
  </div>

  <!-- Video Section (if video URL exists) -->
  {#if data.lesson.videoUrl}
    <div class="video-section mb-4">
      <div class="ratio ratio-16x9">
        <iframe 
          src={data.lesson.videoUrl} 
          title={data.lesson.title}
          allowfullscreen
        ></iframe>
      </div>
    </div>
  {/if}

  <!-- Lesson Content -->
  <div class="lesson-content mb-4">
    <div class="card">
      <div class="card-body">
        <h2 class="card-title h4 mb-3">Lesson Content</h2>
        <div class="content-text">
          {data.lesson.content}
        </div>
      </div>
    </div>
  </div>

  <!-- Complete Button & Navigation -->
  <div class="lesson-actions">
    <div class="d-flex justify-content-between align-items-center">
      <!-- Previous Lesson -->
      {#if prevLesson()}
        <a 
          href="/courses/{data.course.id}/lessons/{prevLesson().id}" 
          class="btn btn-outline-secondary"
        >
          ← Previous Lesson
        </a>
      {:else}
        <div></div>
      {/if}

      <!-- Complete Button -->
      <div class="d-flex gap-2">
        {#if !isLessonCompleted}
          <form method="POST" action="?/complete" use:enhance={handleCompleteEnhance}>
            <button 
              type="submit" 
              class="btn btn-success"
              disabled={isCompleting}
            >
              {isCompleting ? 'Marking Complete...' : 'Mark as Complete'}
            </button>
          </form>
        {:else}
          <button class="btn btn-success" disabled>
            ✓ Completed
          </button>
        {/if}

        <!-- Next Lesson -->
        {#if nextLesson()}
          <a 
            href="/courses/{data.course.id}/lessons/{nextLesson().id}" 
            class="btn btn-primary"
          >
            Next Lesson →
          </a>
        {/if}
      </div>
    </div>
  </div>
</div>

<style>
  .container {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem;
  }

  .breadcrumb {
    background: none;
    padding: 0;
  }

  .breadcrumb-item a {
    color: #2196F3;
    text-decoration: none;
  }

  .breadcrumb-item a:hover {
    text-decoration: underline;
  }

  .lesson-header h1 {
    margin: 0;
    color: #333;
    font-size: 2rem;
  }

  .progress-container {
    padding: 1rem;
    background: #f8f9fa;
    border-radius: 8px;
  }

  .progress {
    height: 25px;
    border-radius: 12px;
  }

  .progress-bar {
    background-color: #28a745;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .video-section {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .lesson-content {
    margin-top: 2rem;
  }

  .content-text {
    white-space: pre-wrap;
    line-height: 1.8;
    color: #444;
    font-size: 1.05rem;
  }

  .card {
    border: none;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .lesson-actions {
    margin-top: 3rem;
    padding-top: 2rem;
    border-top: 2px solid #dee2e6;
  }

  @media (max-width: 768px) {
    .lesson-header .d-flex {
      flex-direction: column;
      gap: 1rem;
    }

    .lesson-actions .d-flex {
      flex-direction: column;
      gap: 1rem;
    }

    .lesson-actions .d-flex > * {
      width: 100%;
    }
  }
</style>