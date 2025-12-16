<script>
  import { goto, invalidateAll } from '$app/navigation';
  import { enhance } from '$app/forms';

  let { data, form } = $props();

  let isEditing = $state(false);
  let isDeleting = $state(false);
  let showDeleteConfirm = $state(false);

  // Editable fields
  let editTitle = $state(data.course.title);
  let editDescription = $state(data.course.description);

  function startEdit() {
    editTitle = data.course.title;
    editDescription = data.course.description;
    isEditing = true;
  }

  function cancelEdit() {
    isEditing = false;
    editTitle = data.course.title;
    editDescription = data.course.description;
  }

  function handleUpdateEnhance() {
    return async ({ result, update }) => {
      if (result.type === 'success') {
        isEditing = false;
        await invalidateAll(); // Reload data
      }
      await update();
    };
  }

  function handleDeleteEnhance() {
    isDeleting = true;
    
    return async ({ result }) => {
      if (result.type === 'redirect') {
        goto(result.location);
      } else {
        isDeleting = false;
        showDeleteConfirm = false;
      }
    };
  }
</script>

<div class="container">
  <div class="header">
    <a href="/" class="back-link">← Back to Courses</a>
  </div>

  {#if !isEditing}
    <!-- View Mode -->
    <div class="course-header">
      <div class="course-info">
        <h1>{data.course.title}</h1>
        <p class="description">{data.course.description}</p>
      </div>
      
      <div class="action-buttons">
        <button on:click={startEdit} class="btn-edit">
          Edit Course
        </button>
        <button on:click={() => showDeleteConfirm = true} class="btn-delete">
          Delete Course
        </button>
      </div>
    </div>
  {:else}
    <!-- Edit Mode -->
    <div class="edit-section">
      <h2>Edit Course</h2>
      
      <form method="POST" action="?/update" use:enhance={handleUpdateEnhance}>
        <div class="form-group">
          <label for="title">Course Title *</label>
          <input
            type="text"
            id="title"
            name="title"
            bind:value={editTitle}
            required
            placeholder="Enter course title"
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
            bind:value={editDescription}
            required
            rows="4"
            placeholder="Enter course description"
          ></textarea>
          {#if form?.errors?.description}
            <span class="error">{form.errors.description}</span>
          {/if}
        </div>

        {#if form?.error}
          <div class="error-message">
            {form.error}
          </div>
        {/if}

        {#if form?.success}
          <div class="success-message">
            Course updated successfully!
          </div>
        {/if}

        <div class="button-group">
          <button type="submit" class="btn-primary">
            Save Changes
          </button>
          <button type="button" on:click={cancelEdit} class="btn-secondary">
            Cancel
          </button>
        </div>
      </form>
    </div>
  {/if}

  <!-- Delete Confirmation Modal -->
  {#if showDeleteConfirm}
    <div class="modal-overlay" on:click={() => showDeleteConfirm = false}>
      <div class="modal" on:click={(e) => e.stopPropagation()}>
        <h3>Delete Course</h3>
        <p>Are you sure you want to delete "{data.course.title}"?</p>
        <p class="warning">This action cannot be undone. All lessons will remain but won't be accessible through this course.</p>
        
        {#if form?.deleteError}
          <div class="error-message">
            {form.deleteError}
          </div>
        {/if}

        <form method="POST" action="?/delete" use:enhance={handleDeleteEnhance}>
          <div class="modal-buttons">
            <button 
              type="submit" 
              class="btn-delete" 
              disabled={isDeleting}
            >
              {isDeleting ? 'Deleting...' : 'Yes, Delete'}
            </button>
            <button 
              type="button" 
              on:click={() => showDeleteConfirm = false} 
              class="btn-secondary"
              disabled={isDeleting}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  {/if}

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

  .btn-edit,
  .btn-delete,
  .btn-primary,
  .btn-secondary {
    padding: 0.75rem 1.5rem;
    border: none;
    border-radius: 4px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;
  }

  .btn-edit {
    background-color: #2196F3;
    color: white;
  }

  .btn-edit:hover {
    background-color: #1976D2;
  }

  .btn-delete {
    background-color: #f44336;
    color: white;
  }

  .btn-delete:hover {
    background-color: #d32f2f;
  }

  .btn-delete:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }

  .btn-primary {
    background-color: #4CAF50;
    color: white;
  }

  .btn-primary:hover {
    background-color: #45a049;
  }

  .btn-secondary {
    background-color: #f5f5f5;
    color: #333;
    border: 1px solid #ddd;
  }

  .btn-secondary:hover {
    background-color: #eeeeee;
  }

  .btn-secondary:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .edit-section {
    background: #f9f9f9;
    padding: 2rem;
    border-radius: 8px;
    margin-bottom: 2rem;
  }

  .edit-section h2 {
    margin-top: 0;
    margin-bottom: 1.5rem;
    color: #333;
  }

  .form-group {
    margin-bottom: 1.5rem;
  }

  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 600;
    color: #555;
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
    margin-top: 1.5rem;
  }

  /* Modal Styles */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }

  .modal {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    max-width: 500px;
    width: 90%;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  .modal h3 {
    margin-top: 0;
    margin-bottom: 1rem;
    color: #333;
  }

  .modal p {
    margin-bottom: 1rem;
    color: #666;
  }

  .warning {
    color: #f57c00;
    font-weight: 500;
  }

  .modal-buttons {
    display: flex;
    gap: 1rem;
    margin-top: 1.5rem;
  }

  .modal-buttons button {
    flex: 1;
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

    .action-buttons button {
      width: 100%;
    }
  }
</style>