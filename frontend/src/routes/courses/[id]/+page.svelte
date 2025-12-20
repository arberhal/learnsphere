<script>
  import { enhance } from '$app/forms';
  import ProgressBadge from '$lib/components/ProgressBadge.svelte';
  
  let { data } = $props();
  
  let progress = $derived(data.progress);
  let totalLessons = $derived(data.lessons.length);
  let isEnrolled = $derived(!!progress); // Has progress = enrolled
  let isEnrolling = $state(false);
  let isTeacher = $derived(data.isTeacher); // ✨ NEW: Check if user is teacher
  let isOwnCourse = $derived(data.isOwnCourse); // ✨ NEW: Check if teacher owns this course
</script>

<div class="min-h-screen bg-gray-50">
  <!-- Header -->
  <header class="bg-white border-b border-gray-200">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div class="flex items-center gap-4 mb-4">
        <a 
          href="/courses" 
          class="text-blue-600 hover:text-blue-700 font-medium transition-colors"
        >
          ← Back to Courses
        </a>
      </div>
      
      <div class="flex items-start justify-between gap-6">
        <div class="flex-1">
          <h1 class="text-4xl font-bold text-gray-900 mb-3">
            {data.course.title}
          </h1>
          <p class="text-lg text-gray-600 leading-relaxed">
            {data.course.description}
          </p>
        </div>
        
        <div class="flex-shrink-0 flex gap-3">
          {#if isEnrolled && !isTeacher}
            <!-- Student: Show Progress Badge -->
            <ProgressBadge 
              status={progress.status}
              completedLessons={progress.completedLessons}
              totalLessons={totalLessons}
            />
          {/if}
          
          {#if isOwnCourse}
            <!-- Teacher: Show Edit Button -->
            <a 
              href="/courses/{data.course.id}/edit"
              class="inline-flex items-center gap-2 px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-lg transition-colors"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
              Edit Course
            </a>
          {/if}
        </div>
      </div>
      
      <!-- Progress Bar (only for enrolled students) -->
      {#if isEnrolled && !isTeacher}
        <div class="mt-6">
          <div class="flex justify-between text-sm text-gray-600 mb-2">
            <span>{progress.completedLessons} of {totalLessons} lessons completed</span>
            <span>{Math.round(progress.percent)}%</span>
          </div>
          <div class="h-3 bg-gray-200 rounded-full overflow-hidden">
            <div 
              class="h-full bg-gradient-to-r from-blue-600 to-indigo-600 transition-all duration-500 rounded-full"
              style="width: {progress.percent}%"
            ></div>
          </div>
        </div>
      {/if}
    </div>
  </header>

  <!-- Main Content -->
  <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-2xl font-bold text-gray-900">Course Content</h2>
        <p class="text-gray-600 mt-1">{totalLessons} {totalLessons === 1 ? 'lesson' : 'lessons'}</p>
      </div>
      
      {#if data.lessons.length === 0}
        <div class="p-12 text-center">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-1">No Lessons Yet</h3>
          <p class="text-gray-600">This course doesn't have any lessons.</p>
        </div>
      {:else}
        <ul class="divide-y divide-gray-200">
          {#each data.lessons as lesson, index}
            <li>
              {#if isEnrolled || isTeacher}
                <!-- Enrolled student OR Teacher: Clickable lesson -->
                <a 
                  href="/courses/{data.course.id}/lessons/{lesson.id}"
                  class="block px-6 py-5 hover:bg-gray-50 transition-colors"
                >
                  <div class="flex items-center gap-4">
                    <!-- Lesson Number -->
                    <div class="flex-shrink-0">
                      <div class="w-10 h-10 flex items-center justify-center rounded-full bg-blue-100 text-blue-700 font-bold">
                        {lesson.order}
                      </div>
                    </div>
                    
                    <!-- Lesson Info -->
                    <div class="flex-1 min-w-0">
                      <h3 class="text-lg font-semibold text-gray-900 mb-1">
                        {lesson.title}
                      </h3>
                      {#if lesson.content}
                        <p class="text-sm text-gray-600 line-clamp-2">
                          {lesson.content.substring(0, 150)}...
                        </p>
                      {/if}
                    </div>
                    
                    <!-- Completion Status (only for students) -->
                    {#if !isTeacher}
                      <div class="flex-shrink-0">
                        {#if progress.completedLessons >= lesson.order}
                          <div class="flex items-center gap-2 text-green-600">
                            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                            </svg>
                            <span class="text-sm font-medium">Completed</span>
                          </div>
                        {:else}
                          <svg class="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                          </svg>
                        {/if}
                      </div>
                    {:else}
                      <!-- Teacher: Show arrow -->
                      <svg class="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                      </svg>
                    {/if}
                  </div>
                </a>
              {:else}
                <!-- Not enrolled student: Locked lesson (not clickable) -->
                <div class="px-6 py-5 bg-gray-50 opacity-75">
                  <div class="flex items-center gap-4">
                    <!-- Lesson Number (locked) -->
                    <div class="flex-shrink-0">
                      <div class="w-10 h-10 flex items-center justify-center rounded-full bg-gray-200 text-gray-500 font-bold">
                        {lesson.order}
                      </div>
                    </div>
                    
                    <!-- Lesson Info -->
                    <div class="flex-1 min-w-0">
                      <h3 class="text-lg font-semibold text-gray-600 mb-1">
                        {lesson.title}
                      </h3>
                      {#if lesson.content}
                        <p class="text-sm text-gray-500 line-clamp-2">
                          {lesson.content.substring(0, 150)}...
                        </p>
                      {/if}
                    </div>
                    
                    <!-- Lock Icon -->
                    <div class="flex-shrink-0">
                      <svg class="w-6 h-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                      </svg>
                    </div>
                  </div>
                </div>
              {/if}
            </li>
          {/each}
        </ul>
      {/if}
    </div>
    
    <!-- Action Buttons (only for students) -->
    {#if !isTeacher && data.lessons.length > 0}
      <div class="mt-8 flex justify-center">
        {#if !isEnrolled}
          <!-- ENROLL BUTTON (Students only) -->
          <form method="POST" action="?/enroll" use:enhance={() => {
            isEnrolling = true;
            return async ({ result, update }) => {
              isEnrolling = false;
              await update();
            };
          }}>
            <button
              type="submit"
              disabled={isEnrolling}
              class="inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-green-600 to-emerald-600 text-white font-semibold rounded-xl hover:from-green-700 hover:to-emerald-700 transform hover:scale-105 transition-all shadow-lg disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
            >
              {#if isEnrolling}
                <svg class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Enrolling...
              {:else}
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
                </svg>
                Enroll in Course
              {/if}
            </button>
          </form>
        {:else if progress.status === 'COMPLETED'}
          <!-- COMPLETED - Review Course -->
          <a 
            href="/courses/{data.course.id}/lessons/{data.lessons[0].id}"
            class="inline-flex items-center gap-2 px-8 py-4 bg-green-600 text-white font-semibold rounded-xl hover:bg-green-700 transition-colors shadow-lg"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
            </svg>
            Review Course
          </a>
        {:else if progress.status === 'NOT_STARTED'}
          <!-- START COURSE -->
          <a 
            href="/courses/{data.course.id}/lessons/{data.lessons[0].id}"
            class="inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl hover:from-blue-700 hover:to-indigo-700 transform hover:scale-105 transition-all shadow-lg"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" />
            </svg>
            Start Course
          </a>
        {:else}
          <!-- IN_PROGRESS - Continue -->
          {@const nextLessonIndex = Math.min(progress.completedLessons, data.lessons.length - 1)}
          <a 
            href="/courses/{data.course.id}/lessons/{data.lessons[nextLessonIndex].id}"
            class="inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl hover:from-blue-700 hover:to-indigo-700 transform hover:scale-105 transition-all shadow-lg"
          >
            Continue Learning
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
            </svg>
          </a>
        {/if}
      </div>
    {/if}
  </main>
</div>