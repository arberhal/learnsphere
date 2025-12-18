import axios from 'axios';
import { fail, redirect } from '@sveltejs/kit';
import { API_BASE_URL } from '$env/static/private';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;
    const lessonId = params.lessonId;

    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        const lessonResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons/${lessonId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const courseResponse = await axios.get(
            `${API_BASE_URL}/api/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const lessonsResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        let progress = null;
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
            // no progress yet
        }

        return {
            lesson: lessonResponse.data,
            course: courseResponse.data,
            allLessons: lessonsResponse.data,
            progress,
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
    complete: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;

        try {
            const lessonsResponse = await axios.get(
                `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            let currentCompleted = 0;
            try {
                const progressResponse = await axios.get(
                    `${API_BASE_URL}/api/student/progress/${courseId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${jwt_token}`
                        }
                    }
                );
                currentCompleted = progressResponse.data.completedLessons;
            } catch {
                // start at 0
            }

            const newCompleted = Math.min(
                currentCompleted + 1,
                lessonsResponse.data.length
            );

            await axios.post(
                `${API_BASE_URL}/api/student/progress/${courseId}/${newCompleted}`,
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
