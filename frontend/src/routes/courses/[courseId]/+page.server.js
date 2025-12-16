import axios from "axios";
import { API_BASE_URL } from '$env/static/private';
import { fail, redirect } from '@sveltejs/kit';

export async function load({ params }) {
    const courseId = params.courseId;

    try {
        const courseResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}`
        );

        const lessonsResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`
        );

        return {
            course: courseResponse.data,
            lessons: lessonsResponse.data
        };
    } catch (error) {
        console.error('Failed to load course:', error);
        throw redirect(303, '/');
    }
}

export const actions = {
    // Update course action
    update: async ({ request, params }) => {
        const courseId = params.courseId;
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
                `${API_BASE_URL}/api/teacher/courses/${courseId}`,
                {
                    title: title.trim(),
                    description: description.trim()
                },
                {
                    headers: {
                        'Content-Type': 'application/json'
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
    delete: async ({ params }) => {
        const courseId = params.courseId;

        try {
            await axios.delete(
                `${API_BASE_URL}/api/teacher/courses/${courseId}`
            );

            // Redirect to home page after successful deletion
            throw redirect(303, '/');
            
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