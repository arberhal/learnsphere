<script>
  let { data } = $props();
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

  <!-- Lesson Header -->
  <div class="lesson-header">
    <div class="d-flex justify-content-between align-items-start mb-4">
      <div>
        <span class="badge bg-primary mb-2">Lesson {data.lesson.order}</span>
        <h1>{data.lesson.title}</h1>
      </div>
      <div class="btn-group">
        <a href="/courses/{data.course.id}/lessons/{data.lesson.id}/edit" class="btn btn-outline-primary">
          Edit Lesson
        </a>
        <a href="/courses/{data.course.id}" class="btn btn-outline-secondary">
          Back to Course
        </a>
      </div>
    </div>
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
  <div class="lesson-content">
    <div class="card">
      <div class="card-body">
        <h2 class="card-title h4 mb-3">Lesson Content</h2>
        <div class="content-text">
          {data.lesson.content}
        </div>
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

  @media (max-width: 768px) {
    .lesson-header .d-flex {
      flex-direction: column;
      gap: 1rem;
    }

    .btn-group {
      width: 100%;
    }

    .btn-group .btn {
      width: 100%;
    }
  }
</style>