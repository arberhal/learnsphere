<script>
  import { enhance } from '$app/forms';
  import { invalidateAll } from '$app/navigation';
  import Quiz from '$lib/components/Quiz.svelte';

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
  let nextLesson = $derived.by(() => {
    const sortedLessons = [...data.allLessons].sort((a, b) => a.order - b.order);
    const currentIndex = sortedLessons.findIndex(l => l.id === data.lesson.id);
    return currentIndex < sortedLessons.length - 1 ? sortedLessons[currentIndex + 1] : null;
  });

  // Find previous lesson
  let prevLesson = $derived.by(() => {
    const sortedLessons = [...data.allLessons].sort((a, b) => a.order - b.order);
    const currentIndex = sortedLessons.findIndex(l => l.id === data.lesson.id);
    return currentIndex > 0 ? sortedLessons[currentIndex - 1] : null;
  });
</script>

<div class="max-w-4xl mx-auto px-4 py-8">
  <!-- Header with breadcrumb -->
  <nav aria-label="breadcrumb" class="mb-4">
    <ol class="flex items-center gap-2 text-sm">
      <li><a href="/courses" class="text-blue-600 hover:underline">Courses</a></li>
      <li class="text-gray-400">/</li>
      <li><a href="/courses/{data.course.id}" class="text-blue-600 hover:underline">{data.course.title}</a></li>
      <li class="text-gray-400">/</li>
      <li class="text-gray-700" aria-current="page">{data.lesson.title}</li>
    </ol>
  </nav>

  <!-- Success/Error Messages -->
  {#if form?.success}
    <div class="mb-4 p-4 bg-green-50 border border-green-200 text-green-800 rounded-lg">
      {form.message}
    </div>
  {/if}

  {#if form?.error}
    <div class="mb-4 p-4 bg-red-50 border border-red-200 text-red-800 rounded-lg">
      {form.error}
    </div>
  {/if}

  <!-- Lesson Header -->
  <div class="mb-6">
    <div class="flex flex-col md:flex-row md:justify-between md:items-start gap-4 mb-4">
      <div>
        <div class="flex items-center gap-2 mb-2">
          <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-600 text-white">
            Lesson {data.lesson.order}
          </span>
          {#if isLessonCompleted}
            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-600 text-white">
              ✓ Completed
            </span>
          {/if}
        </div>
        <h1 class="text-3xl font-bold text-gray-900">{data.lesson.title}</h1>
      </div>
      
      <div class="flex gap-2">
        <!-- Teacher edit button (will hide with roles later) -->
        <a 
          href="/courses/{data.course.id}/edit" 
          class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
        >
          Edit Course
        </a>
        <a 
          href="/courses/{data.course.id}" 
          class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
        >
          Back to Course
        </a>
      </div>
    </div>

    <!-- Progress Bar -->
    {#if data.progress}
      <div class="p-4 bg-gray-50 rounded-lg">
        <div class="flex justify-between items-center mb-2">
          <span class="text-sm text-gray-600">Course Progress</span>
          <span class="text-sm text-gray-600">
            {data.progress.completedLessons} / {data.allLessons.length} Lessons
          </span>
        </div>
        <div class="w-full h-6 bg-gray-200 rounded-full overflow-hidden">
          <div 
            class="h-full bg-green-600 flex items-center justify-center text-white text-sm font-semibold transition-all duration-300"
            style="width: {data.progress.percent}%"
            role="progressbar" 
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
    <div class="mb-6 rounded-lg overflow-hidden shadow-md">
      <div class="relative aspect-video">
        <iframe 
          src={data.lesson.videoUrl} 
          title={data.lesson.title}
          allowfullscreen
          class="absolute inset-0 w-full h-full"
        ></iframe>
      </div>
    </div>
  {/if}

  <!-- Lesson Content -->
  <div class="mb-6">
    <div class="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
      <div class="p-6">
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Lesson Content</h2>
        <div class="whitespace-pre-wrap leading-relaxed text-gray-700 text-base">
          {data.lesson.content}
        </div>
      </div>
    </div>
  </div>

  <!-- 🎯 AI-POWERED QUIZ SECTION -->
  <div class="mb-8">
    <Quiz lessonId={data.lesson.id} />
  </div>

  <!-- Complete Button & Navigation -->
  <div class="mt-8 pt-6 border-t-2 border-gray-200">
    <div class="flex flex-col md:flex-row justify-between items-stretch md:items-center gap-4">
      <!-- Previous Lesson -->
      {#if prevLesson}
        <a 
          href="/courses/{data.course.id}/lessons/{prevLesson.id}" 
          class="inline-flex items-center justify-center px-6 py-3 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
        >
          ← Previous Lesson
        </a>
      {:else}
        <div class="hidden md:block"></div>
      {/if}

      <!-- Complete Button -->
      <div class="flex gap-2">
        {#if !isLessonCompleted}
          <form method="POST" action="?/complete" use:enhance={handleCompleteEnhance}>
            <button 
              type="submit" 
              class="inline-flex items-center justify-center px-6 py-3 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              disabled={isCompleting}
            >
              {isCompleting ? 'Marking Complete...' : 'Mark as Complete'}
            </button>
          </form>
        {:else}
          <button class="inline-flex items-center justify-center px-6 py-3 text-sm font-medium text-white bg-green-600 rounded-lg opacity-50 cursor-not-allowed" disabled>
            ✓ Completed
          </button>
        {/if}

        <!-- Next Lesson -->
        {#if nextLesson}
          <a 
            href="/courses/{data.course.id}/lessons/{nextLesson.id}" 
            class="inline-flex items-center justify-center px-6 py-3 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Next Lesson →
          </a>
        {/if}
      </div>
    </div>
  </div>
</div>