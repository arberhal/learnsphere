export async function load({ locals }) {
	return {
		user: locals.user,
		isAuthenticated: locals.isAuthenticated
	};
}