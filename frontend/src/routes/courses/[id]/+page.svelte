<script>
  let { data } = $props();

  const currentLesson = $derived(
    data.lessons.find((l) => l.id === data.lessonId) || data.lessons[0]
  );

  const currentIndex = $derived(
    data.lessons.findIndex((l) => l.id === currentLesson?.id) ?? 0
  );

  const prevLesson = $derived.by(() => {
    if (currentIndex > 0) {
      return data.lessons[currentIndex - 1];
    }
    return null;
  });

  const nextLesson = $derived.by(() => {
    if (currentIndex < data.lessons.length - 1) {
      return data.lessons[currentIndex + 1];
    }
    return null;
  });


</script>

<div class="min-h-screen bg-gray-50 flex flex-col">
  <!-- Header -->
  <header class="bg-white border-b border-gray-200 sticky top-0 z-10">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center py-4">
        <div class="flex items-center gap-4">
          <a 
            href="/courses/" 
            class="text-blue-600 hover:text-blue-700 font-medium transition-colors"
          >
            ← Back to Courses
          </a>
          <div class="hidden sm:block w-px h-6 bg-gray-300"></div>
          <h1 class="hidden sm:block text-lg font-semibold text-gray-900 truncate max-w-md">
            {data.course.title}
          </h1>
        </div>
        
        <div class="flex items-center gap-2 text-sm text-gray-600">
          <span class="font-medium">Lesson {currentLesson?.order}</span>
          <span class="text-gray-400">of {data.lessons.length}</span>
        </div>
      </div>
    </div>
  </header>

  <!-- Main Content -->
  <main class="flex-1 flex flex-col lg:flex-row max-w-7xl mx-auto w-full">
    <!-- Lesson Content -->
    <div class="flex-1 px-4 sm:px-6 lg:px-8 py-8">
      <article class="max-w-3xl mx-auto">
        <div class="flex justify-between items-start mb-6">
          <h2 class="text-3xl font-bold text-gray-900 leading-tight">
            {currentLesson?.title}
          </h2>
          <a 
            href="/courses/{data.course.id}/edit"
            class="inline-flex items-center gap-2 px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-lg transition-colors text-sm"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
            Edit
          </a>
        </div>

        <div class="prose prose-lg max-w-none">
          <div class="text-gray-700 leading-relaxed whitespace-pre-wrap">
            {currentLesson?.content || 'No content available.'}
          </div>
        </div>

        <!-- Navigation Buttons -->
        <div class="flex justify-between items-center mt-12 pt-8 border-t border-gray-200">
          {#if prevLesson}
            <a 
              href="/courses/{data.course.id}/lessons/{prevLesson.id}"
              class="inline-flex items-center gap-2 px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-lg transition-colors"
            >
              ← Previous Lesson
            </a>
          {:else}
            <div></div>
          {/if}

         
        </div>
      </article>
    </div>

    <!-- Lessons Sidebar -->
    <aside class="lg:w-96 bg-white border-t lg:border-t-0 lg:border-l border-gray-200">
      <div class="sticky top-20 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Course Content</h3>
        
        <ul class="space-y-2">
          {#each data.lessons as lesson, index}
            <li>
              <a 
                href="/courses/{data.course.id}/lessons/{lesson.id}"
                class="block px-4 py-3 rounded-lg transition-colors {lesson.id === currentLesson?.id 
                  ? 'bg-blue-50 border border-blue-200' 
                  : 'hover:bg-gray-50 border border-transparent'}"
              >
                <div class="flex items-start gap-3">
                  <span class="flex-shrink-0 w-6 h-6 flex items-center justify-center rounded-full text-xs font-bold {lesson.id === currentLesson?.id 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-200 text-gray-600'}">
                    {lesson.order}
                  </span>
                  <span class="text-sm font-medium {lesson.id === currentLesson?.id 
                    ? 'text-blue-700' 
                    : 'text-gray-700'}">
                    {lesson.title}
                  </span>
                </div>
              </a>
            </li>
          {/each}
        </ul>
      </div>
    </aside>
  </main>
</div>
