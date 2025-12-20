import axios from 'axios';
import { error, fail } from '@sveltejs/kit';
import { API_BASE_URL } from '$env/static/private';

export async function load({ params, locals }) {
    const jwt_token = locals.jwt_token;
    const courseId = params.id;

    if (!locals.isAuthenticated || !jwt_token) {
        throw error(401, 'Unauthorized');
    }

    try {
        const courseResponse = await axios.get(
            `${API_BASE_URL}/api/courses/${courseId}`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const course = courseResponse.data;

        const lessonsResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`,
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const lessons = lessonsResponse.data;

        // ✨ Check if user is teacher
        const isTeacher = locals.role === 'teacher' || locals.isTeacher;
        
        // ✨ Check if teacher owns this course
        const userSub = locals.jwt?.sub || locals.user?.sub;
        const isOwnCourse = isTeacher && course.teacherSub === userSub;

        // ✨ Check if progress exists (NO AUTO-CREATE!)
        // Only for students - teachers don't have progress
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
                // Progress doesn't exist - student is NOT enrolled
                progress = null;
            }
        }

        return {
            course,
            lessons,
            progress,      // null for teachers or unenrolled students
            isTeacher,     // true if teacher
            isOwnCourse    // true if teacher owns this course
        };
    } catch (err) {
        console.error('Failed to load course:', err);
        throw error(404, 'Course not found');
    }
}

export const actions = {
    // ✨ Enroll action - ONLY for students!
    enroll: async ({ params, locals }) => {
        const jwt_token = locals.jwt_token;
        const courseId = params.id;

        if (!locals.isAuthenticated || !jwt_token) {
            return fail(401, { error: 'Unauthorized' });
        }

        // ✨ Check if user is teacher - teachers cannot enroll!
        const isTeacher = locals.role === 'teacher' || locals.isTeacher;
        if (isTeacher) {
            return fail(403, { error: 'Teachers cannot enroll in courses' });
        }

        try {
            // Create progress with completedLessons = 0 (NOT_STARTED)
            const response = await axios.post(
                `${API_BASE_URL}/api/student/progress/${courseId}/0`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            );

            return { success: true, progress: response.data };
        } catch (error) {
            console.error('Failed to enroll:', error);
            return fail(500, { error: 'Failed to enroll in course' });
        }
    }
};