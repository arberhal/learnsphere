<script>
	let { status, completedLessons, totalLessons } = $props();

	// Status colors and icons (3 states)
	const statusConfig = {
		NOT_STARTED: {
			color: 'bg-slate-100 text-slate-700 border-slate-300',
			icon: '⏸️',
			text: 'Not Started'
		},
		IN_PROGRESS: {
			color: 'bg-blue-100 text-blue-700 border-blue-300',
			icon: '📚',
			text: 'In Progress'
		},
		COMPLETED: {
			color: 'bg-green-100 text-green-700 border-green-300',
			icon: '✓',
			text: 'Completed'
		}
	};

	// Default to NOT_STARTED if status is missing or invalid
	let config = $derived(statusConfig[status] || statusConfig.NOT_STARTED);
</script>

<span
	class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full text-sm font-semibold border-2 {config.color}"
>
	<span class="text-base">{config.icon}</span>
	<span>{config.text}</span>
	{#if status === 'IN_PROGRESS' && completedLessons !== undefined && totalLessons !== undefined}
		<span class="text-xs opacity-75">({completedLessons}/{totalLessons})</span>
	{/if}
</span>

<style>
	span {
		transition: all 0.2s ease;
	}
</style>