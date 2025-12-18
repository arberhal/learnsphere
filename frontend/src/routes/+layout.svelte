<script>
  import { page } from '$app/stores';
  
  let { data, children } = $props();
  
  // Check if user is authenticated
  let isAuthenticated = $derived(data.isAuthenticated || false);
  
  // Get user roles from data
  let userRoles = $derived(data.user?.user_roles || []);
  
  // Check roles
  let isTeacher = $derived(userRoles.includes('teacher'));
  let isStudent = $derived(userRoles.includes('student'));
  
  // Current path for active link highlighting
  let currentPath = $derived($page.url.pathname);
  
  // Mobile menu state
  let mobileMenuOpen = $state(false);
</script>

<div class="flex min-h-screen flex-col bg-background">
  <!-- Navigation Bar -->
  <nav class="sticky top-0 z-50 border-b border-border bg-white/80 backdrop-blur-lg">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="flex h-16 items-center justify-between">
        <!-- Logo/Brand -->
        <a href="/" class="group flex items-center gap-3 transition-transform hover:scale-105">
          <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-xl shadow-lg">
            📚
          </div>
          <span class="text-xl font-bold text-foreground">LearnSphere</span>
        </a>

        <!-- Desktop Navigation Links -->
        <div class="hidden items-center gap-2 md:flex">
          {#if isAuthenticated}
            <!-- Common Links -->
            <a 
              href="/courses" 
              class="rounded-lg px-4 py-2 text-sm font-medium text-foreground transition-colors hover:bg-accent hover:text-accent-foreground {currentPath.startsWith('/courses') && !currentPath.includes('create') ? 'bg-accent' : ''}"
            >
              Courses
            </a>

            <!-- Teacher-Only Links -->
            {#if isTeacher}
              <a 
                href="/courses/create" 
                class="flex items-center gap-2 rounded-lg bg-amber-500/10 px-4 py-2 text-sm font-medium text-amber-700 transition-all hover:bg-amber-500/20 {currentPath === '/courses/create' ? 'bg-amber-500/20 shadow-sm' : ''}"
              >
                <span>➕</span>
                Create Course
              </a>
            {/if}

            <!-- Student-Only Links -->
            {#if isStudent}
              <a 
                href="/my-courses" 
                class="flex items-center gap-2 rounded-lg bg-blue-500/10 px-4 py-2 text-sm font-medium text-blue-700 transition-all hover:bg-blue-500/20 {currentPath === '/my-courses' ? 'bg-blue-500/20 shadow-sm' : ''}"
              >
                <span>📖</span>
                My Courses
              </a>
            {/if}

            <!-- User Menu -->
            <div class="ml-4 flex items-center gap-3 border-l border-border pl-4">
              <div class="flex flex-col items-end">
                <span class="text-sm font-medium text-foreground">{data.user?.name || data.user?.email || 'User'}</span>
                {#if isTeacher}
                  <span class="inline-flex items-center rounded-full bg-amber-100 px-2.5 py-0.5 text-xs font-semibold text-amber-800">Teacher</span>
                {:else if isStudent}
                  <span class="inline-flex items-center rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-800">Student</span>
                {/if}
              </div>
              <a 
                href="/logout" 
                class="rounded-lg bg-red-50 px-4 py-2 text-sm font-medium text-red-700 transition-all hover:bg-red-100 hover:shadow-sm"
              >
                Logout
              </a>
            </div>
          {:else}
            <!-- Not Authenticated -->
            <a href="/login" class="rounded-lg px-4 py-2 text-sm font-medium text-foreground transition-colors hover:bg-accent hover:text-accent-foreground">
              Login
            </a>
            <a href="/signup" class="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground shadow-sm transition-all hover:bg-primary/90 hover:shadow-md">
              Sign Up
            </a>
          {/if}
        </div>

        <!-- Mobile menu button -->
        <button 
          onclick={() => mobileMenuOpen = !mobileMenuOpen}
          class="inline-flex items-center justify-center rounded-lg p-2 text-foreground transition-colors hover:bg-accent md:hidden"
          aria-label="Toggle menu"
        >
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            {#if mobileMenuOpen}
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            {:else}
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            {/if}
          </svg>
        </button>
      </div>

      <!-- Mobile Navigation Menu -->
      {#if mobileMenuOpen}
        <div class="border-t border-border py-4 md:hidden">
          {#if isAuthenticated}
            <div class="space-y-1">
              <a 
                href="/courses" 
                class="block rounded-lg px-4 py-2 text-sm font-medium text-foreground transition-colors hover:bg-accent {currentPath.startsWith('/courses') && !currentPath.includes('create') ? 'bg-accent' : ''}"
              >
                Courses
              </a>

              {#if isTeacher}
                <a 
                  href="/courses/create" 
                  class="flex items-center gap-2 rounded-lg bg-amber-500/10 px-4 py-2 text-sm font-medium text-amber-700 transition-colors hover:bg-amber-500/20 {currentPath === '/courses/create' ? 'bg-amber-500/20' : ''}"
                >
                  <span>➕</span>
                  Create Course
                </a>
              {/if}

              {#if isStudent}
                <a 
                  href="/my-courses" 
                  class="flex items-center gap-2 rounded-lg bg-blue-500/10 px-4 py-2 text-sm font-medium text-blue-700 transition-colors hover:bg-blue-500/20 {currentPath === '/my-courses' ? 'bg-blue-500/20' : ''}"
                >
                  <span>📖</span>
                  My Courses
                </a>
              {/if}

              <div class="border-t border-border pt-4 mt-4">
                <div class="px-4 py-2">
                  <p class="text-sm font-medium text-foreground">{data.user?.name || data.user?.email || 'User'}</p>
                  <div class="mt-1">
                    {#if isTeacher}
                      <span class="inline-flex items-center rounded-full bg-amber-100 px-2.5 py-0.5 text-xs font-semibold text-amber-800">Teacher</span>
                    {:else if isStudent}
                      <span class="inline-flex items-center rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-800">Student</span>
                    {/if}
                  </div>
                </div>
                <a 
                  href="/logout" 
                  class="mt-2 block rounded-lg bg-red-50 px-4 py-2 text-sm font-medium text-red-700 transition-colors hover:bg-red-100"
                >
                  Logout
                </a>
              </div>
            </div>
          {:else}
            <div class="space-y-1">
              <a href="/login" class="block rounded-lg px-4 py-2 text-sm font-medium text-foreground transition-colors hover:bg-accent">
                Login
              </a>
              <a href="/signup" class="block rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90">
                Sign Up
              </a>
            </div>
          {/if}
        </div>
      {/if}
    </div>
  </nav>

  <!-- Main Content -->
  <main class="flex-1">
    {@render children()}
  </main>

  <!-- Footer -->
  <footer class="border-t border-border bg-muted/30">
    <div class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <div class="flex flex-col items-center justify-between gap-4 sm:flex-row">
        <p class="text-sm text-muted-foreground">&copy; 2025 LearnSphere. All rights reserved.</p>
        {#if isAuthenticated}
          <p class="text-sm text-muted-foreground">
            Logged in as <span class="font-medium text-foreground">{data.user?.email || 'Unknown'}</span>
            {#if isTeacher}
              <span class="ml-2 text-amber-700">(Teacher)</span>
            {:else if isStudent}
              <span class="ml-2 text-blue-700">(Student)</span>
            {/if}
          </p>
        {/if}
      </div>
    </div>
  </footer>
</div>
