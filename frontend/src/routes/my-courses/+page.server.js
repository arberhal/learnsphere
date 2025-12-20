import { API_BASE_URL } from '$env/static/private';
import axios from 'axios';
import { redirect } from '@sveltejs/kit';

export async function load({ locals }) {
	const jwt_token = locals.jwt_token;

	if (!locals.isAuthenticated || !jwt_token) {
		throw redirect(303, '/login');
	}

	// ✨ Check if user is teacher
	const isTeacher = locals.role === 'teacher' || locals.isTeacher;

	try {
		if (isTeacher) {
			// ✨ TEACHER: Show courses they created
			const coursesResponse = await axios.get(`${API_BASE_URL}/api/teacher/courses`, {
				headers: {
					Authorization: `Bearer ${jwt_token}`
				}
			});

			const teacherCourses = coursesResponse.data;

			// Fetch lesson count for each course
			const coursesWithLessons = await Promise.all(
				teacherCourses.map(async (course) => {
					try {
						const lessonsResponse = await axios.get(
							`${API_BASE_URL}/api/teacher/courses/${course.id}/lessons`,
							{ headers: { Authorization: `Bearer ${jwt_token}` }}
						);
						return {
							...course,
							totalLessons: lessonsResponse.data.length
						};
					} catch {
						return {
							...course,
							totalLessons: 0
						};
					}
				})
			);

			return {
				myCourses: coursesWithLessons,
				progressMap: {}, // Teachers don't have progress
				isTeacher: true
			};

		} else {
			// ✨ STUDENT: Show enrolled courses (existing logic)
			const coursesResponse = await axios.get(`${API_BASE_URL}/api/my-courses`, {
				headers: {
					Authorization: `Bearer ${jwt_token}`
				}
			});

			const myCourses = coursesResponse.data;

			// Fetch both progress AND lessons for each course
			const courseDataPromises = myCourses.map(async (course) => {
				// Get progress
				const progressPromise = axios
					.get(`${API_BASE_URL}/api/student/progress/${course.id}`, {
						headers: { Authorization: `Bearer ${jwt_token}` }
					})
					.catch(() => ({ 
						data: { 
							completedLessons: 0, 
							percent: 0, 
							status: 'NOT_STARTED' 
						} 
					}));

				// Get lessons to count total
				const lessonsPromise = axios
					.get(`${API_BASE_URL}/api/teacher/courses/${course.id}/lessons`, {
						headers: { Authorization: `Bearer ${jwt_token}` }
					})
					.catch(() => ({ data: [] }));

				const [progressResult, lessonsResult] = await Promise.all([
					progressPromise,
					lessonsPromise
				]);

				return {
					progress: progressResult.data,
					totalLessons: lessonsResult.data.length
				};
			});

			const courseDataResults = await Promise.all(courseDataPromises);

			// Create progressMap with totalLessons
			const progressMap = {};
			const coursesWithLessons = myCourses.map((course, index) => ({
				...course,
				totalLessons: courseDataResults[index].totalLessons
			}));

			myCourses.forEach((course, index) => {
				progressMap[course.id] = courseDataResults[index].progress;
			});

			return {
				myCourses: coursesWithLessons,
				progressMap,
				isTeacher: false
			};
		}
	} catch (error) {
		console.error('Failed to load my courses:', error);
		return {
			myCourses: [],
			progressMap: {},
			isTeacher
		};
	}
}