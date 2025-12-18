import axios from 'axios';
import { error } from '@sveltejs/kit';
import { API_BASE_URL } from '$env/static/private';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;

    if (!locals.isAuthenticated || !jwt_token) {
        throw error(401, 'Unauthorized');
    }

    try {
        const courseResponse = await axios.get(
            `${API_BASE_URL}/api/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const course = courseResponse.data;

        const lessonsResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const lessons = lessonsResponse.data;

        let progress;
        try {
            const progressResponse = await axios.get(
                `${API_BASE_URL}/api/student/progress/${courseId}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );
            progress = progressResponse.data;
        } catch {
            progress = { completedLessons: 0, percent: 0 };
        }

        return {
            course,
            lessons,
            progress
        };
    } catch (err) {
        console.error('Failed to load course:', err);
        throw error(404, 'Course not found');
    }
}
