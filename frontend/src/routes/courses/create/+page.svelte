<script lang="ts">
  import { enhance } from '$app/forms';
  import { goto } from '$app/navigation';

  let { form } = $props();

  let isSubmitting = $state(false);
  let lessons = $state([]);
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

<div class="max-w-4xl mx-auto my-8 px-4 md:px-8">
  <h1 class="text-3xl font-bold text-gray-900 mb-8">Create New Course</h1>

  <form method="POST" use:enhance={handleEnhance}>
    <!-- Course Details Section -->
    <section class="bg-gray-50 rounded-lg p-6 mb-8">
      <h2 class="text-xl font-semibold text-gray-700 mb-6">Course Details</h2>
      
      <div class="mb-4">
        <label for="title" class="block mb-2 text-sm font-semibold text-gray-600">Course Title *</label>
        <input
          type="text"
          id="title"
          name="title"
          required
          placeholder="Enter course title"
          disabled={isSubmitting}
          value={form?.title || ''}
          class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed text-base"
        />
        {#if form?.errors?.title}
          <span class="block text-red-600 text-sm mt-1">{form.errors.title}</span>
        {/if}
      </div>

      <div class="mb-0">
        <label for="description" class="block mb-2 text-sm font-semibold text-gray-600">Description *</label>
        <textarea
          id="description"
          name="description"
          required
          rows="4"
          placeholder="Enter course description"
          disabled={isSubmitting}
          value={form?.description || ''}
          class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed text-base"
        ></textarea>
        {#if form?.errors?.description}
          <span class="block text-red-600 text-sm mt-1">{form.errors.description}</span>
        {/if}
      </div>
    </section>

    <!-- Lessons Section -->
    <section class="bg-gray-50 rounded-lg p-6 mb-8">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-semibold text-gray-700">Lessons (Optional)</h2>
        <button 
          type="button" 
          class="px-4 py-2 bg-blue-500 text-white rounded font-semibold text-sm hover:bg-blue-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed" 
          onclick={addLesson} 
          disabled={isSubmitting}
        >
          + Add Lesson
        </button>
      </div>

      {#if lessons.length === 0}
        <p class="text-center text-gray-400 py-8 italic">No lessons added yet. Click "Add Lesson" to create your first lesson.</p>
      {/if}

      {#each lessons as lesson, index (lesson.id)}
        <div class="bg-white rounded-md p-6 mb-4 border border-gray-200">
          <div class="flex justify-between items-center mb-4 pb-3 border-b-2 border-gray-100">
            <span class="font-bold text-blue-500 text-base">Lesson {lesson.order}</span>
            <div class="flex gap-2">
              <button 
                type="button" 
                class="w-8 h-8 bg-gray-100 border border-gray-300 rounded hover:bg-gray-200 hover:border-gray-400 transition-all disabled:opacity-40 disabled:cursor-not-allowed text-base" 
                onclick={() => moveLessonUp(index)}
                disabled={index === 0 || isSubmitting}
                title="Move up"
              >
                ↑
              </button>
              <button 
                type="button" 
                class="w-8 h-8 bg-gray-100 border border-gray-300 rounded hover:bg-gray-200 hover:border-gray-400 transition-all disabled:opacity-40 disabled:cursor-not-allowed text-base" 
                onclick={() => moveLessonDown(index)}
                disabled={index === lessons.length - 1 || isSubmitting}
                title="Move down"
              >
                ↓
              </button>
              <button 
                type="button" 
                class="w-8 h-8 bg-red-50 text-red-700 border border-red-200 rounded hover:bg-red-100 hover:border-red-300 transition-all disabled:opacity-40 disabled:cursor-not-allowed text-lg" 
                onclick={() => removeLesson(lesson.id)}
                disabled={isSubmitting}
                title="Remove lesson"
              >
                ✕
              </button>
            </div>
          </div>

          <input type="hidden" name="lessons[{index}].order" value={lesson.order} />

          <div class="mb-4">
            <label for="lesson-title-{lesson.id}" class="block mb-2 text-sm font-semibold text-gray-600">Lesson Title *</label>
            <input
              type="text"
              id="lesson-title-{lesson.id}"
              name="lessons[{index}].title"
              bind:value={lesson.title}
              required
              placeholder="Enter lesson title"
              disabled={isSubmitting}
              class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed text-base"
            />
          </div>

          <div class="mb-4">
            <label for="lesson-content-{lesson.id}" class="block mb-2 text-sm font-semibold text-gray-600">Content *</label>
            <textarea
              id="lesson-content-{lesson.id}"
              name="lessons[{index}].content"
              bind:value={lesson.content}
              required
              rows="3"
              placeholder="Enter lesson content"
              disabled={isSubmitting}
              class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed text-base"
            ></textarea>
          </div>

          <div class="mb-0">
            <label for="lesson-video-{lesson.id}" class="block mb-2 text-sm font-semibold text-gray-600">Video URL (Optional)</label>
            <input
              type="url"
              id="lesson-video-{lesson.id}"
              name="lessons[{index}].videoUrl"
              bind:value={lesson.videoUrl}
              placeholder="https://youtube.com/watch?v=..."
              disabled={isSubmitting}
              class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent disabled:bg-gray-100 disabled:cursor-not-allowed text-base"
            />
          </div>
        </div>
      {/each}
    </section>

    {#if form?.error}
      <div class="px-3 py-2 bg-red-50 text-red-800 rounded mb-4">
        {form.error}
      </div>
    {/if}

    {#if form?.success}
      <div class="px-3 py-2 bg-green-50 text-green-800 rounded mb-4">
        Course and lessons created successfully! Redirecting...
      </div>
    {/if}

    <div class="flex gap-4 mt-8">
      <button 
        type="submit" 
        disabled={isSubmitting} 
        class="flex-1 px-6 py-3 bg-green-500 text-white rounded text-base font-semibold hover:bg-green-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
      >
        {isSubmitting ? 'Creating...' : 'Create Course'}
      </button>
      <a 
        href="/" 
        class="flex-1 px-6 py-3 bg-gray-100 text-gray-900 border border-gray-300 rounded text-base font-semibold text-center hover:bg-gray-200 transition-colors flex items-center justify-center"
      >
        Cancel
      </a>
    </div>
  </form>
</div>
