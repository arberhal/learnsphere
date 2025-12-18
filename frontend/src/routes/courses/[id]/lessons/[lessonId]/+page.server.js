import axios from 'axios';
import { fail, redirect } from '@sveltejs/kit';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;
    const lessonId = params.lessonId;

    // Check authentication
    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        // Fetch lesson details (PUBLIC endpoint)
        const lessonResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}/lessons/${lessonId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        // Fetch course info for breadcrumb (PUBLIC endpoint)
        const courseResponse = await axios.get(
            `http://localhost:8080/api/courses/${courseId}`,  // ✅ Changed from /teacher/courses
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        // Fetch all lessons to determine total and current progress
        const lessonsResponse = await axios.get(
            `http://localhost:8080/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        // Fetch current progress
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
        } catch (error) {
            // Progress doesn't exist yet, that's okay
            console.log('No progress found for this course yet');
        }

        return {
            lesson: lessonResponse.data,
            course: courseResponse.data,
            allLessons: lessonsResponse.data,
            progress: progress,
            currentLessonOrder: lessonResponse.data.order
        };
    } catch (error) {
        console.error('Failed to load lesson:', error);
        
        if (error.response?.status === 404) {
            throw redirect(303, `/courses/${courseId}`);
        }
        
        throw redirect(303, '/courses');
    }
}

export const actions = {
    // Mark lesson as complete
    complete: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;

        try {
            // Fetch all lessons to get total count
            const lessonsResponse = await axios.get(
                `http://localhost:8080/api/teacher/courses/${courseId}/lessons`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            // Get current progress
            let currentCompleted = 0;
            try {
                const progressResponse = await axios.get(
                    `http://localhost:8080/api/student/progress/${courseId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${jwt_token}`
                        }
                    }
                );
                currentCompleted = progressResponse.data.completedLessons;
            } catch (error) {
                // No progress yet, start at 0
            }

            // Increment completed lessons
            const newCompleted = Math.min(currentCompleted + 1, lessonsResponse.data.length);

            // Update progress
            await axios.post(
                `http://localhost:8080/api/student/progress/${courseId}/${newCompleted}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { success: true, message: 'Lesson marked as complete!' };
            
        } catch (error) {
            console.error('Failed to mark lesson as complete:', error);
            
            return fail(500, { 
                error: 'Failed to mark lesson as complete. Please try again.'
            });
        }
    }
};