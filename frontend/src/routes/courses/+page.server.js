import axios from 'axios';
import { redirect } from '@sveltejs/kit';

export async function load({ locals }) {
    const jwt_token = locals.jwt_token;

    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    // ✨ Redirect teachers to My Courses
    const isTeacher = locals.role === 'teacher' || locals.isTeacher;
    if (isTeacher) {
        throw redirect(303, '/my-courses');
    }

    try {
        // Fetch all available courses (for students only)
        const response = await axios.get(
            'http://localhost:8080/api/courses',
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        return {
            courses: response.data
        };
    } catch (error) {
        console.error('Failed to load courses:', error);
        return {
            courses: []
        };
    }
}