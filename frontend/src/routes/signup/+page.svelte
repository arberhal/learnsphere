<script>
	import { enhance } from '$app/forms';
	
	let { data, form } = $props();
	let isAuthenticated = $derived(data.isAuthenticated);
	
	let signupFormElement;
</script>

<div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 flex items-center justify-center p-4">
	<div class="w-full max-w-md">
		<div class="bg-white rounded-2xl shadow-xl p-8">

			<div class="text-center mb-8">
				<div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl flex items-center justify-center mx-auto mb-4">
					<svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
					</svg>
				</div>
				<h1 class="text-2xl font-bold text-gray-900">Create Account</h1>
				<p class="text-gray-600 mt-2">Join LearnSphere today</p>
			</div>

			{#if form?.error}
				<div class="mb-6 bg-red-50 border border-red-200 rounded-lg p-4 flex items-start gap-3">
					<svg class="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
						<path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
					</svg>
					<p class="text-red-800 text-sm font-medium">{form.error}</p>
				</div>
			{/if}


			<form
				method="POST"
				action="?/signup"
				bind:this={signupFormElement}
				use:enhance
				novalidate
				class="space-y-5"
			>

				<div>
					<label for="email" class="block text-sm font-medium text-gray-700 mb-2">
						E-Mail
					</label>
					<input
						type="email"
						class="w-full px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
						id="email"
						name="email"
						placeholder="you@example.com"
						required
					/>
				</div>


				<div class="grid grid-cols-2 gap-4">
					<div>
						<label for="firstName" class="block text-sm font-medium text-gray-700 mb-2">
							First Name
						</label>
						<input
							type="text"
							class="w-full px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
							id="firstName"
							name="firstName"
							placeholder="John"
						/>
					</div>
					
					<div>
						<label for="lastName" class="block text-sm font-medium text-gray-700 mb-2">
							Last Name
						</label>
						<input
							type="text"
							class="w-full px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
							id="lastName"
							name="lastName"
							placeholder="Doe"
						/>
					</div>
				</div>


				<div>
					<label for="password" class="block text-sm font-medium text-gray-700 mb-2">
						Password
					</label>
					<input
						type="password"
						class="w-full px-4 py-3 bg-gray-50 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none"
						id="password"
						name="password"
						placeholder="••••••••"
						required
					/>
				</div>


				<button
					type="submit"
					class="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white font-semibold py-3 px-4 rounded-lg hover:from-blue-700 hover:to-purple-700 focus:ring-4 focus:ring-blue-300 transition-all shadow-lg hover:shadow-xl"
				>
					Sign up
				</button>


				<div class="text-center pt-4">
					<p class="text-gray-600 text-sm">
						Already have an account?
						<a href="/login" class="text-blue-600 hover:text-blue-700 font-semibold hover:underline">
							Log in
						</a>
					</p>
				</div>
			</form>
		</div>
	</div>
</div>
