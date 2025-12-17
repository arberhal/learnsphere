import axios from 'axios';

export async function load({ locals }) {
	const jwt_token = locals.jwt_token;

	// If not authenticated, return empty courses
	if (!locals.isAuthenticated || !jwt_token) {
		return {
			courses: []
		};
	}

	try {
		const response = await axios.get('http://localhost:8080/api/teacher/courses', {
			headers: {
				Authorization: `Bearer ${jwt_token}` // ✅ ADD THIS!
			}
		});

		return {
			courses: response.data
		};
	} catch (error) {
		console.error('Error loading courses:', error);
		return {
			courses: [],
			error: 'Failed to load courses'
		};
	}
}