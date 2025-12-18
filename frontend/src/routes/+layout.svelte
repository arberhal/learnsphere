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
</script>

<div class="app">
  <!-- Navigation Bar -->
  <nav class="navbar">
    <div class="nav-container">
      <!-- Logo/Brand -->
      <a href="/" class="brand">
        <span class="brand-icon">📚</span>
        <span class="brand-text">LearnSphere</span>
      </a>

      <!-- Navigation Links -->
      <div class="nav-links">
        {#if isAuthenticated}
          <!-- Common Links (All Authenticated Users) -->
          <a 
            href="/courses" 
            class="nav-link"
            class:active={currentPath.startsWith('/courses')}
          >
            Courses
          </a>

          <!-- Teacher-Only Links -->
          {#if isTeacher}
            <a 
              href="/courses/create" 
              class="nav-link teacher-link"
              class:active={currentPath === '/courses/create'}
            >
              <span class="link-icon">➕</span>
              Create Course
            </a>
          {/if}

          <!-- Student-Only Links -->
          {#if isStudent}
            <a 
              href="/my-courses" 
              class="nav-link student-link"
              class:active={currentPath === '/my-courses'}
            >
              <span class="link-icon">📖</span>
              My Courses
            </a>
          {/if}

          <!-- User Menu -->
          <div class="user-menu">
            <span class="user-name">{data.user?.name || data.user?.email || 'User'}</span>
            {#if isTeacher}
              <span class="badge badge-teacher">Teacher</span>
            {:else if isStudent}
              <span class="badge badge-student">Student</span>
            {/if}
            <a href="/logout" class="nav-link logout-link">Logout</a>
          </div>
        {:else}
          <!-- Not Authenticated -->
          <a href="/login" class="nav-link">Login</a>
          <a href="/signup" class="nav-link btn-primary">Sign Up</a>
        {/if}
      </div>
    </div>
  </nav>

  <!-- Main Content -->
  <main class="main-content">
    {@render children()}
  </main>

  <!-- Footer -->
  <footer class="footer">
    <div class="footer-content">
      <p>&copy; 2025 LearnSphere. All rights reserved.</p>
      {#if isAuthenticated}
        <p class="footer-user-info">
          Logged in as: {data.user?.email || 'Unknown'}
          {#if isTeacher}
            (Teacher)
          {:else if isStudent}
            (Student)
          {/if}
        </p>
      {/if}
    </div>
  </footer>
</div>

<style>
  .app {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }

  /* Navigation Bar */
  .navbar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 1rem 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    text-decoration: none;
    color: white;
    font-size: 1.5rem;
    font-weight: 700;
    transition: transform 0.2s;
  }

  .brand:hover {
    transform: scale(1.05);
  }

  .brand-icon {
    font-size: 2rem;
  }

  .brand-text {
    font-weight: 700;
  }

  .nav-links {
    display: flex;
    align-items: center;
    gap: 1.5rem;
  }

  .nav-link {
    color: white;
    text-decoration: none;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    transition: all 0.2s;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .nav-link:hover {
    background: rgba(255, 255, 255, 0.2);
  }

  .nav-link.active {
    background: rgba(255, 255, 255, 0.3);
    font-weight: 600;
  }

  .teacher-link {
    background: rgba(255, 193, 7, 0.2);
  }

  .teacher-link:hover {
    background: rgba(255, 193, 7, 0.3);
  }

  .student-link {
    background: rgba(33, 150, 243, 0.2);
  }

  .student-link:hover {
    background: rgba(33, 150, 243, 0.3);
  }

  .link-icon {
    font-size: 1.1rem;
  }

  .user-menu {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-left: 1rem;
    padding-left: 1rem;
    border-left: 1px solid rgba(255, 255, 255, 0.3);
  }

  .user-name {
    color: white;
    font-weight: 500;
  }

  .badge {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
  }

  .badge-teacher {
    background: #ffc107;
    color: #000;
  }

  .badge-student {
    background: #2196f3;
    color: white;
  }

  .logout-link {
    background: rgba(244, 67, 54, 0.2);
  }

  .logout-link:hover {
    background: rgba(244, 67, 54, 0.3);
  }

  .btn-primary {
    background: white;
    color: #667eea;
    font-weight: 600;
  }

  .btn-primary:hover {
    background: rgba(255, 255, 255, 0.9);
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  }

  /* Main Content */
  .main-content {
    flex: 1;
    background: #f5f5f5;
  }

  /* Footer */
  .footer {
    background: #333;
    color: white;
    padding: 2rem 0;
    margin-top: auto;
  }

  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
    text-align: center;
  }

  .footer-user-info {
    margin-top: 0.5rem;
    font-size: 0.875rem;
    opacity: 0.8;
  }

  /* Responsive */
  @media (max-width: 768px) {
    .nav-container {
      flex-direction: column;
      gap: 1rem;
    }

    .nav-links {
      flex-wrap: wrap;
      justify-content: center;
    }

    .user-menu {
      flex-direction: column;
      margin-left: 0;
      padding-left: 0;
      border-left: none;
      border-top: 1px solid rgba(255, 255, 255, 0.3);
      padding-top: 1rem;
    }

    .brand-text {
      display: none;
    }
  }

  /* Global Styles */
  :global(body) {
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  }
</style>