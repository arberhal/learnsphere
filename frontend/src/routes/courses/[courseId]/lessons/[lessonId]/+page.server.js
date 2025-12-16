import axios from "axios";
import { API_BASE_URL } from '$env/static/private';

export async function load({ params }) {
	const { courseId, lessonId } = params;

	const lessonsResponse = await axios.get(
		`${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`
	);

	const lesson = lessonsResponse.data.find(l => l.id === lessonId);

	const progressResponse = await axios
		.get(`${API_BASE_URL}/api/student/progress/${courseId}`)
		.catch(() => ({ data: null }));

	return {
		lesson,
		courseId,
		progress: progressResponse.data
	};
}

export const actions = {
	markCompleted: async ({ request }) => {
		const data = await request.formData();
		const courseId = data.get('courseId');
		const completedLessons = data.get('completedLessons');

		await axios.post(
			`${API_BASE_URL}/api/student/progress/${courseId}/${completedLessons}`
		);

		return { success: true };
	}
};
