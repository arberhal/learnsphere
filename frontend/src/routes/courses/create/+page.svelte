<script>
  import { enhance } from '$app/forms';
  import { goto } from '$app/navigation';

  export let form;

  let isSubmitting = false;

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
    <div class="form-group">
      <label for="title">Course Title *</label>
      <input
        type="text"
        id="title"
        name="title"
        required
        placeholder="Enter course title"
        disabled={isSubmitting}
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
        rows="6"
        placeholder="Enter course description"
        disabled={isSubmitting}
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
        Course created successfully! Redirecting...
      </div>
    {/if}

    <div class="button-group">
      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Creating...' : 'Create Course'}
      </button>
      <a href="/" class="cancel-btn">Cancel</a>
    </div>
  </form>
</div>

<style>
  .container {
    max-width: 600px;
    margin: 2rem auto;
    padding: 2rem;
  }

  h1 {
    margin-bottom: 2rem;
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

  button {
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

  button:hover:not(:disabled) {
    background-color: #45a049;
  }

  button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }

  .cancel-btn {
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
  }

  .cancel-btn:hover {
    background-color: #eeeeee;
  }
</style>