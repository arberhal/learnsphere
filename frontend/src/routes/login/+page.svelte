<script>
	import { enhance } from '$app/forms';
	
	let { data, form } = $props();
	let isAuthenticated = $derived(data.isAuthenticated);
	
	let loginFormElement;
</script>

<div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 px-4 py-12">
	<div class="w-full max-w-md">
		<!-- Logo/Header Section -->
		<div class="text-center mb-8">
			<div class="inline-flex items-center justify-center w-16 h-16 bg-blue-600 rounded-2xl mb-4">
				<svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
				</svg>
			</div>
			<h1 class="text-3xl font-bold text-slate-900 mb-2">Welcome back</h1>
			<p class="text-slate-600">Sign in to continue to LearnSphere</p>
		</div>

		<!-- Login Card -->
		<div class="bg-white rounded-2xl shadow-xl border border-slate-200 p-8">
			<form
				method="POST"
				bind:this={loginFormElement}
				use:enhance
				novalidate
				class="space-y-6"
			>
				<!-- Error Alert -->
				{#if form?.error}
					<div class="bg-red-50 border border-red-200 text-red-800 rounded-xl p-4 flex items-start gap-3" role="alert">
						<svg class="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
							<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
						</svg>
						<span class="text-sm font-medium">{form.error}</span>
					</div>
				{/if}
				
				<!-- Email Field -->
				<div class="space-y-2">
					<label for="email" class="block text-sm font-semibold text-slate-700">
						Email Address
					</label>
					<input 
						type="email" 
						class="w-full px-4 py-3 bg-slate-50 border border-slate-300 rounded-xl text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-shadow" 
						id="email" 
						name="email"
						placeholder="you@example.com"
						required 
					/>
				</div>
				
				<!-- Password Field -->
				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<label for="password" class="block text-sm font-semibold text-slate-700">
							Password
						</label>
						<a href="/forgot-password" class="text-sm font-medium text-blue-600 hover:text-blue-700 transition-colors">
							Forgot?
						</a>
					</div>
					<input 
						type="password" 
						class="w-full px-4 py-3 bg-slate-50 border border-slate-300 rounded-xl text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-shadow" 
						id="password" 
						name="password"
						placeholder="••••••••"
						required 
					/>
				</div>
				
				<!-- Submit Button -->
				<button 
					type="submit" 
					class="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-4 rounded-xl transition-colors shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40"
				>
					Sign In
				</button>
			</form>
		</div>

		<!-- Sign Up Link -->
		<div class="text-center mt-6">
			<p class="text-slate-600">
				Don't have an account? 
				<a href="/signup" class="font-semibold text-blue-600 hover:text-blue-700 transition-colors">
					Sign up for free
				</a>
			</p>
		</div>
	</div>
</div>
