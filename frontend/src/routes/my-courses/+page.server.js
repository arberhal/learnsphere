import { API_BASE_URL } from '$env/static/private';
import axios from 'axios';
import { redirect } from '@sveltejs/kit';
export async function load({ locals }) {
	const jwt_token = locals.jwt_token;

	if (!locals.isAuthenticated || !jwt_token) {
		throw redirect(303, '/login');
	}

	try {
		const coursesResponse = await axios.get(`${API_BASE_URL}/api/my-courses`, {
			headers: {
				Authorization: `Bearer ${jwt_token}`
			}
		});

		const myCourses = coursesResponse.data;

		const progressPromises = myCourses.map((course) =>
			axios
				.get(`${API_BASE_URL}/api/student/progress/${course.id}`, {
					headers: {
						Authorization: `Bearer ${jwt_token}`
					}
				})
				.catch(() => ({ data: { completedLessons: 0, percent: 0 } }))
		);

		const progressResults = await Promise.all(progressPromises);

		const progressMap = {};
		myCourses.forEach((course, index) => {
			progressMap[course.id] = progressResults[index].data;
		});

		return {
			myCourses,
			progressMap
		};
	} catch (error) {
		console.error('Failed to load my courses:', error);
		return {
			myCourses: [],
			progressMap: {}
		};
	}
}
