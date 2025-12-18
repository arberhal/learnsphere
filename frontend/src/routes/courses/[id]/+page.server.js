import axios from 'axios';
import { error } from '@sveltejs/kit';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;

    if (!locals.isAuthenticated || !jwt_token) {
        throw error(401, 'Unauthorized');
    }

    try {
        // Use PUBLIC endpoint instead of teacher-only endpoint
        const courseResponse = await axios.get(
            `http://localhost:8080/api/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const course = courseResponse.data;

        // Fetch lessons for this course
        const lessonsResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const lessons = lessonsResponse.data;

        // Fetch student progress
        let progress = null;
        try {
            const progressResponse = await axios.get(
                `http://localhost:8080/api/student/progress/${courseId}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );
            progress = progressResponse.data;
        } catch (err) {
            // No progress yet, that's okay
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