import { env } from '$env/dynamic/private';
import axios from 'axios';

export async function load({ locals }) {
	const jwt_token = locals.jwt_token;
	const API_BASE_URL = env.API_BASE_URL ?? 'http://localhost:8080';

	if (!locals.isAuthenticated || !jwt_token) {
		return { courses: [] };
	}

	try {
		const response = await axios.get(
			`${API_BASE_URL}/api/teacher/courses`,
			{
				headers: {
					Authorization: `Bearer ${jwt_token}`
				}
			}
		);

		return { courses: response.data };
	} catch (error) {
		console.error('Error loading courses:', error);
		return {
			courses: [],
			error: 'Failed to load courses'
		};
	}
}
