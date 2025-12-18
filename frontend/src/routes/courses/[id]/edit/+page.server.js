import axios from 'axios';
import { fail, redirect } from '@sveltejs/kit';
import { API_BASE_URL } from '$env/static/private';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;

    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        // Fetch course details
        const courseResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        // Fetch existing lessons
        const lessonsResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`,
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
        throw redirect(303, '/courses');
    }
}

export const actions = {
    // Update course info
    updateCourse: async ({ request, params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;
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
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { success: true, message: 'Course updated successfully!' };
            
        } catch (error) {
            console.error('Failed to update course:', error);
            
            return fail(500, { 
                error: 'Failed to update course. Please try again.'
            });
        }
    },

    // Delete course
    deleteCourse: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;

        try {
            await axios.delete(
                `${API_BASE_URL}/api/teacher/courses/${courseId}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            throw redirect(303, '/courses');
            
        } catch (error) {
            console.error('Failed to delete course:', error);
            
            if (error.status === 303) {
                throw error;
            }
            
            return fail(500, { 
                deleteError: 'Failed to delete course. Please try again.'
            });
        }
    },

    // Create new lesson
    createLesson: async ({ request, params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;
        const data = await request.formData();

        const title = data.get('lessonTitle');
        const content = data.get('lessonContent');
        const videoUrl = data.get('lessonVideoUrl');
        const order = data.get('lessonOrder');

        // Validation
        const errors = {};
        
        if (!title || title.trim().length === 0) {
            errors.lessonTitle = 'Lesson title is required';
        }
        
        if (!content || content.trim().length === 0) {
            errors.lessonContent = 'Lesson content is required';
        }

        if (!order || isNaN(parseInt(order))) {
            errors.lessonOrder = 'Order must be a number';
        }

        if (Object.keys(errors).length > 0) {
            return fail(400, { lessonErrors: errors });
        }

        try {
            await axios.post(
                `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`,
                {
                    title: title.trim(),
                    content: content.trim(),
                    videoUrl: videoUrl?.trim() || '',
                    order: parseInt(order)
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { lessonSuccess: true, message: 'Lesson created successfully!' };
            
        } catch (error) {
            console.error('Failed to create lesson:', error);
            
            return fail(500, { 
                lessonError: 'Failed to create lesson. Please try again.'
            });
        }
    },

    // Update existing lesson
    updateLesson: async ({ request, params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;
        const data = await request.formData();

        const lessonId = data.get('lessonId');
        const title = data.get('lessonTitle');
        const content = data.get('lessonContent');
        const videoUrl = data.get('lessonVideoUrl');
        const order = data.get('lessonOrder');

        try {
            await axios.put(
                `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons/${lessonId}`,
                {
                    title: title.trim(),
                    content: content.trim(),
                    videoUrl: videoUrl?.trim() || '',
                    order: parseInt(order)
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { lessonSuccess: true, message: 'Lesson updated successfully!' };
            
        } catch (error) {
            console.error('Failed to update lesson:', error);
            
            return fail(500, { 
                lessonError: 'Failed to update lesson. Please try again.'
            });
        }
    },

    // Delete lesson
    deleteLesson: async ({ request, params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;
        const data = await request.formData();
        const lessonId = data.get('lessonId');

        try {
            await axios.delete(
                `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons/${lessonId}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { lessonSuccess: true, message: 'Lesson deleted successfully!' };
            
        } catch (error) {
            console.error('Failed to delete lesson:', error);
            
            return fail(500, { 
                lessonError: 'Failed to delete lesson. Please try again.'
            });
        }
    }
};