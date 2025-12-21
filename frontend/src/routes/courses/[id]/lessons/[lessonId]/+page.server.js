import axios from 'axios';
import { fail, redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL ?? 'http://localhost:8080';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;
    const lessonId = params.lessonId;

    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    // ✨ Check if user is teacher
    const isTeacher = locals.role === 'teacher' || locals.isTeacher;

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

        // ✨ Check if teacher owns this course
        const userSub = locals.jwt?.sub || locals.user?.sub;
        const isOwnCourse = isTeacher && courseResponse.data.teacherSub === userSub;

        // ✨ Only fetch progress for students
        let progress = null;
        if (!isTeacher) {
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
        }

        return {
            lesson: lessonResponse.data,
            course: courseResponse.data,
            allLessons: lessonsResponse.data,
            progress,
            currentLessonOrder: lessonResponse.data.order,
            isTeacher,      // ✨ Pass to frontend
            isOwnCourse     // ✨ Pass to frontend
        };
    } catch (error) {
        console.error('Failed to load lesson:', error);

        if (error.response?.status === 404) {
            throw redirect(303, `/courses/${courseId}`);
        }

        throw redirect(303, isTeacher ? '/my-courses' : '/courses');
    }
}

export const actions = {
    complete: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;

        // ✨ SECURITY: Block teachers from completing lessons
        const isTeacher = locals.role === 'teacher' || locals.isTeacher;
        if (isTeacher) {
            return fail(403, {
                error: 'Teachers cannot mark lessons as complete.'
            });
        }

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
    },

    // 🎯 Generate Quiz Action
    generateQuiz: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const lessonId = params.lessonId;
        
        const baseUrl = API_BASE_URL || 'http://localhost:8080';

        try {
            const quizResponse = await axios.get(
                `${baseUrl}/api/lessons/${lessonId}/quiz`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { 
                quiz: quizResponse.data 
            };
        } catch (error) {
            console.error('Failed to generate quiz:', error);
            console.error('Error details:', error.response?.data || error.message);

            return fail(400, {
                error: 'Failed to generate quiz. Please try again.'
            });
        }
    }
};
