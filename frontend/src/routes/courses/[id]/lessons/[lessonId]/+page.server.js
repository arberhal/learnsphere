import axios from 'axios';
import { redirect } from '@sveltejs/kit';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;
    const lessonId = params.lessonId;

    // Check authentication
    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        // Fetch lesson details
        const lessonResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}/lessons/${lessonId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        // Fetch course info for breadcrumb
        const courseResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        return {
            lesson: lessonResponse.data,
            course: courseResponse.data
        };
    } catch (error) {
        console.error('Failed to load lesson:', error);
        
        if (error.response?.status === 404) {
            throw redirect(303, `/courses/${courseId}`);
        }
        
        throw redirect(303, '/courses');
    }
}