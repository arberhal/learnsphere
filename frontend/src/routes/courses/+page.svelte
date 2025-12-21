<script>
  let { data } = $props();
  
  let allCourses = $derived(data.courses || []);
  let searchQuery = $state('');
  
  // Filter courses based on search query
  let filteredCourses = $derived(
    searchQuery.trim() === '' 
      ? allCourses 
      : allCourses.filter(course => 
          course.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
          course.description.toLowerCase().includes(searchQuery.toLowerCase())
        )
  );
  
  function clearSearch() {
    searchQuery = '';
  }
</script>

<div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 py-16 px-4 sm:px-6 lg:px-8">
  <div class="max-w-7xl mx-auto">
    <!-- Elegant header with refined typography -->
    <div class="text-center mb-12">
      <h1 class="text-5xl font-bold text-slate-900 mb-4 tracking-tight">Discover Courses</h1>
      <p class="text-xl text-slate-600 max-w-2xl mx-auto leading-relaxed">Expand your knowledge with our curated selection of courses</p>
    </div>

    <!-- Search Bar -->
    <div class="max-w-2xl mx-auto mb-12">
      <div class="relative">
        <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
          <svg class="h-5 w-5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
        <input
          type="text"
          bind:value={searchQuery}
          placeholder="Search courses by title or description..."
          class="w-full pl-12 pr-12 py-4 bg-white border-2 border-slate-200 rounded-2xl text-slate-900 placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-100 transition-all shadow-sm"
        />
        {#if searchQuery}
          <button
            onclick={clearSearch}
            class="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-slate-600 transition-colors"
            aria-label="Clear search"
          >
            <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        {/if}
      </div>
      
      <!-- Search results count -->
      {#if searchQuery}
        <p class="text-sm text-slate-600 mt-3 text-center">
          Found {filteredCourses.length} {filteredCourses.length === 1 ? 'course' : 'courses'}
        </p>
      {/if}
    </div>

    {#if filteredCourses.length === 0}
      <!-- Empty state -->
      <div class="bg-white rounded-3xl shadow-lg border border-slate-100 p-16 text-center max-w-2xl mx-auto">
        <div class="inline-flex items-center justify-center w-24 h-24 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-full mb-8">
          {#if searchQuery}
            <svg class="w-12 h-12 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          {:else}
            <svg class="w-12 h-12 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          {/if}
        </div>
        {#if searchQuery}
          <h2 class="text-3xl font-bold text-slate-800 mb-3">No Results Found</h2>
          <p class="text-lg text-slate-500 mb-6">No courses match "{searchQuery}"</p>
          <button
            onclick={clearSearch}
            class="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-colors"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            Clear Search
          </button>
        {:else}
          <h2 class="text-3xl font-bold text-slate-800 mb-3">No Courses Yet</h2>
          <p class="text-lg text-slate-500">New learning opportunities are coming soon!</p>
        {/if}
      </div>
    {:else}
      <!-- Enhanced grid with premium card design -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {#each filteredCourses as course}
          <a 
            href={`/courses/${course.id}`} 
            class="group bg-white rounded-2xl shadow-md border border-slate-200 overflow-hidden transition-all duration-300 hover:shadow-2xl hover:-translate-y-2 hover:border-blue-300 flex flex-col"
          >
            <!-- Eye-catching header with subtle gradient and better contrast -->
            <div class="bg-gradient-to-br from-blue-600 to-blue-700 p-8 relative overflow-hidden">
              <!-- Decorative element for visual interest -->
              <div class="absolute top-0 right-0 w-32 h-32 bg-white opacity-5 rounded-full -translate-y-16 translate-x-16"></div>
              <h3 class="text-2xl font-bold text-white line-clamp-2 relative z-10 leading-tight">{course.title}</h3>
            </div>
            
            <!-- Clean content area with optimal spacing -->
            <div class="p-8 flex-1 flex flex-col">
              <p class="text-slate-600 leading-relaxed line-clamp-3 mb-6 flex-1">{course.description}</p>
              
              <!-- Call-to-action with smooth transition -->
              <div class="flex items-center gap-2 text-blue-600 font-semibold group-hover:text-blue-700 group-hover:gap-3 transition-all">
                <span>Explore Course</span>
                <svg class="w-5 h-5 transition-transform group-hover:translate-x-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M13 7l5 5m0 0l-5 5m5-5H6" />
                </svg>
              </div>
            </div>
          </a>
        {/each}
      </div>
    {/if}
  </div>
</div>
