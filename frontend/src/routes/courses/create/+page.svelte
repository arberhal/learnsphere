<script>
  import { enhance } from '$app/forms';
  import { goto } from '$app/navigation';

  export let form;

  let isSubmitting = false;
  let lessons = [];
  let nextLessonId = 1;

  // Add a new lesson input
  function addLesson() {
    lessons = [
      ...lessons,
      {
        id: nextLessonId++,
        title: '',
        content: '',
        videoUrl: '',
        order: lessons.length + 1
      }
    ];
  }

  // Remove a lesson by id
  function removeLesson(id) {
    lessons = lessons.filter(l => l.id !== id);
    // Recalculate order numbers
    lessons = lessons.map((lesson, index) => ({
      ...lesson,
      order: index + 1
    }));
  }

  // Move lesson up
  function moveLessonUp(index) {
    if (index === 0) return;
    const newLessons = [...lessons];
    [newLessons[index - 1], newLessons[index]] = [newLessons[index], newLessons[index - 1]];
    // Recalculate order
    lessons = newLessons.map((lesson, idx) => ({
      ...lesson,
      order: idx + 1
    }));
  }

  // Move lesson down
  function moveLessonDown(index) {
    if (index === lessons.length - 1) return;
    const newLessons = [...lessons];
    [newLessons[index], newLessons[index + 1]] = [newLessons[index + 1], newLessons[index]];
    // Recalculate order
    lessons = newLessons.map((lesson, idx) => ({
      ...lesson,
      order: idx + 1
    }));
  }

  function handleEnhance() {
    isSubmitting = true;
    
    return async ({ result, update }) => {
      isSubmitting = false;
      
      if (result.type === 'redirect') {
        goto(result.location);
      } else {
        await update();
      }
    };
  }
</script>

<div class="container">
  <h1>Create New Course</h1>

  <form method="POST" use:enhance={handleEnhance}>
    <!-- Course Details Section -->
    <section class="section">
      <h2>Course Details</h2>
      
      <div class="form-group">
        <label for="title">Course Title *</label>
        <input
          type="text"
          id="title"
          name="title"
          required
          placeholder="Enter course title"
          disabled={isSubmitting}
          value={form?.title || ''}
        />
        {#if form?.errors?.title}
          <span class="error">{form.errors.title}</span>
        {/if}
      </div>

      <div class="form-group">
        <label for="description">Description *</label>
        <textarea
          id="description"
          name="description"
          required
          rows="4"
          placeholder="Enter course description"
          disabled={isSubmitting}
          value={form?.description || ''}
        ></textarea>
        {#if form?.errors?.description}
          <span class="error">{form.errors.description}</span>
        {/if}
      </div>
    </section>

    <!-- Lessons Section -->
    <section class="section">
      <div class="section-header">
        <h2>Lessons (Optional)</h2>
        <button type="button" class="btn-add-lesson" on:click={addLesson} disabled={isSubmitting}>
          + Add Lesson
        </button>
      </div>

      {#if lessons.length === 0}
        <p class="empty-state">No lessons added yet. Click "Add Lesson" to create your first lesson.</p>
      {/if}

      {#each lessons as lesson, index (lesson.id)}
        <div class="lesson-card">
          <div class="lesson-header">
            <span class="lesson-number">Lesson {lesson.order}</span>
            <div class="lesson-controls">
              <button 
                type="button" 
                class="btn-icon" 
                on:click={() => moveLessonUp(index)}
                disabled={index === 0 || isSubmitting}
                title="Move up"
              >
                ↑
              </button>
              <button 
                type="button" 
                class="btn-icon" 
                on:click={() => moveLessonDown(index)}
                disabled={index === lessons.length - 1 || isSubmitting}
                title="Move down"
              >
                ↓
              </button>
              <button 
                type="button" 
                class="btn-remove" 
                on:click={() => removeLesson(lesson.id)}
                disabled={isSubmitting}
                title="Remove lesson"
              >
                ✕
              </button>
            </div>
          </div>

          <input type="hidden" name="lessons[{index}].order" value={lesson.order} />

          <div class="form-group">
            <label for="lesson-title-{lesson.id}">Lesson Title *</label>
            <input
              type="text"
              id="lesson-title-{lesson.id}"
              name="lessons[{index}].title"
              bind:value={lesson.title}
              required
              placeholder="Enter lesson title"
              disabled={isSubmitting}
            />
          </div>

          <div class="form-group">
            <label for="lesson-content-{lesson.id}">Content *</label>
            <textarea
              id="lesson-content-{lesson.id}"
              name="lessons[{index}].content"
              bind:value={lesson.content}
              required
              rows="3"
              placeholder="Enter lesson content"
              disabled={isSubmitting}
            ></textarea>
          </div>

          <div class="form-group">
            <label for="lesson-video-{lesson.id}">Video URL (Optional)</label>
            <input
              type="url"
              id="lesson-video-{lesson.id}"
              name="lessons[{index}].videoUrl"
              bind:value={lesson.videoUrl}
              placeholder="https://youtube.com/watch?v=..."
              disabled={isSubmitting}
            />
          </div>
        </div>
      {/each}
    </section>

    {#if form?.error}
      <div class="error-message">
        {form.error}
      </div>
    {/if}

    {#if form?.success}
      <div class="success-message">
        Course and lessons created successfully! Redirecting...
      </div>
    {/if}

    <div class="button-group">
      <button type="submit" disabled={isSubmitting} class="btn-primary">
        {isSubmitting ? 'Creating...' : 'Create Course'}
      </button>
      <a href="/" class="btn-cancel">Cancel</a>
    </div>
  </form>
</div>

<style>
  .container {
    max-width: 800px;
    margin: 2rem auto;
    padding: 2rem;
  }

  h1 {
    margin-bottom: 2rem;
    color: #333;
  }

  .section {
    background: #f9f9f9;
    padding: 1.5rem;
    border-radius: 8px;
    margin-bottom: 2rem;
  }

  .section h2 {
    margin-top: 0;
    margin-bottom: 1.5rem;
    color: #444;
    font-size: 1.25rem;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }

  .section-header h2 {
    margin: 0;
  }

  .btn-add-lesson {
    padding: 0.5rem 1rem;
    background-color: #2196F3;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .btn-add-lesson:hover:not(:disabled) {
    background-color: #1976D2;
  }

  .btn-add-lesson:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }

  .empty-state {
    text-align: center;
    color: #999;
    padding: 2rem;
    font-style: italic;
  }

  .lesson-card {
    background: white;
    padding: 1.5rem;
    border-radius: 6px;
    margin-bottom: 1rem;
    border: 1px solid #e0e0e0;
  }

  .lesson-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    padding-bottom: 0.75rem;
    border-bottom: 2px solid #f0f0f0;
  }

  .lesson-number {
    font-weight: 700;
    color: #2196F3;
    font-size: 1rem;
  }

  .lesson-controls {
    display: flex;
    gap: 0.5rem;
  }

  .btn-icon {
    width: 32px;
    height: 32px;
    padding: 0;
    background-color: #f5f5f5;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
    transition: all 0.2s;
  }

  .btn-icon:hover:not(:disabled) {
    background-color: #e0e0e0;
    border-color: #bbb;
  }

  .btn-icon:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  .btn-remove {
    width: 32px;
    height: 32px;
    padding: 0;
    background-color: #ffebee;
    color: #c62828;
    border: 1px solid #ffcdd2;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1.2rem;
    transition: all 0.2s;
  }

  .btn-remove:hover:not(:disabled) {
    background-color: #ffcdd2;
    border-color: #e57373;
  }

  .btn-remove:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  .form-group:last-child {
    margin-bottom: 0;
  }

  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 600;
    color: #555;
    font-size: 0.9rem;
  }

  input,
  textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
    font-family: inherit;
  }

  input:focus,
  textarea:focus {
    outline: none;
    border-color: #4CAF50;
    box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
  }

  input:disabled,
  textarea:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
  }

  .error {
    display: block;
    color: #d32f2f;
    font-size: 0.875rem;
    margin-top: 0.25rem;
  }

  .error-message {
    padding: 0.75rem;
    background-color: #ffebee;
    color: #c62828;
    border-radius: 4px;
    margin-bottom: 1rem;
  }

  .success-message {
    padding: 0.75rem;
    background-color: #e8f5e9;
    color: #2e7d32;
    border-radius: 4px;
    margin-bottom: 1rem;
  }

  .button-group {
    display: flex;
    gap: 1rem;
    margin-top: 2rem;
  }

  .btn-primary {
    flex: 1;
    padding: 0.75rem 1.5rem;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .btn-primary:hover:not(:disabled) {
    background-color: #45a049;
  }

  .btn-primary:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }

  .btn-cancel {
    flex: 1;
    padding: 0.75rem 1.5rem;
    background-color: #f5f5f5;
    color: #333;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
    font-weight: 600;
    text-align: center;
    text-decoration: none;
    cursor: pointer;
    transition: background-color 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .btn-cancel:hover {
    background-color: #eeeeee;
  }
</style>