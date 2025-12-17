import axios from "axios";
import { redirect } from '@sveltejs/kit';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;

    // Check authentication
    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        const courseResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const lessonsResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        return {
            course: courseResponse.data,
            lessons: lessonsResponse.data
        };
    } catch (error) {
        console.error('Failed to load course:', error);
        
        if (error.response?.status === 404) {
            throw redirect(303, '/courses');
        }
        
        throw redirect(303, '/');
    }
}