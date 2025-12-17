import axios from "axios";
import { fail, redirect } from '@sveltejs/kit';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id; // ✅ Changed from params.courseId to params.id

    // Check authentication
    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        const courseResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}` // ✅ Added JWT
                }
            }
        );

        const lessonsResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}` // ✅ Added JWT
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
            throw redirect(303, '/courses'); // Course not found, go to courses list
        }
        
        throw redirect(303, '/');
    }
}

export const actions = {
    // Update course action
    update: async ({ request, params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id; // ✅ Changed from params.courseId to params.id
        const data = await request.formData();
        const title = data.get('title');
        const description = data.get('description');

        // Validation
        const errors = {};
        
        if (!title || title.trim().length === 0) {
            errors.title = 'Title is required';
        }
        
        if (!description || description.trim().length === 0) {
            errors.description = 'Description is required';
        }

        if (Object.keys(errors).length > 0) {
            return fail(400, { errors });
        }

        try {
            await axios.put(
                `http://localhost:8080/api/teacher/courses/${courseId}`,
                {
                    title: title.trim(),
                    description: description.trim()
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${jwt_token}` // ✅ Added JWT
                    }
                }
            );

            return { success: true };
            
        } catch (error) {
            console.error('Failed to update course:', error);
            
            return fail(500, { 
                error: 'Failed to update course. Please try again.',
                title,
                description 
            });
        }
    },

    // Delete course action
    delete: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id; // ✅ Changed from params.courseId to params.id

        try {
            await axios.delete(
                `http://localhost:8080/api/teacher/courses/${courseId}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}` // ✅ Added JWT
                    }
                }
            );

            // Redirect to courses list after successful deletion
            throw redirect(303, '/courses');
            
        } catch (error) {
            console.error('Failed to delete course:', error);
            
            // Check if it's our redirect
            if (error.status === 303) {
                throw error;
            }
            
            return fail(500, { 
                deleteError: 'Failed to delete course. Please try again.'
            });
        }
    }
};