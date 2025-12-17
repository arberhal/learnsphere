import axios from "axios";
import { API_BASE_URL } from '$env/static/private';
import { fail, redirect } from '@sveltejs/kit';

export async function load({ params }) {
    const courseId = params.courseId;
    const lessonId = params.lessonId;

    try {
        // Fetch course details
        const courseResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}`
        );

        // Fetch all lessons for the course
        const lessonsResponse = await axios.get(
            `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`
        );

        // Find the specific lesson
        const lesson = lessonsResponse.data.find(l => l.id === lessonId);
        
        if (!lesson) {
            throw redirect(303, `/courses/${courseId}`);
        }

        // Fetch student progress
        let progress = null;
        try {
            const progressResponse = await axios.get(
                `${API_BASE_URL}/api/student/progress/${courseId}`
            );
            progress = progressResponse.data;
        } catch (error) {
            // Progress might not exist yet, that's okay
            console.log('No progress found yet');
        }

        return {
            course: courseResponse.data,
            lesson: lesson,
            allLessons: lessonsResponse.data,
            totalLessons: lessonsResponse.data.length,
            progress: progress
        };
    } catch (error) {
        console.error('Failed to load lesson:', error);
        
        // Check if it's our redirect
        if (error.status === 303) {
            throw error;
        }
        
        throw redirect(303, '/');
    }
}

export const actions = {
    // Mark lesson as completed
    default: async ({ params }) => {
        const courseId = params.courseId;
        const lessonId = params.lessonId;

        try {
            // First, get all lessons to find the current lesson's order
            const lessonsResponse = await axios.get(
                `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`
            );

            const currentLesson = lessonsResponse.data.find(l => l.id === lessonId);
            
            if (!currentLesson) {
                return fail(404, { 
                    error: 'Lesson not found'
                });
            }

            // Get current progress
            let currentProgress = null;
            try {
                const progressResponse = await axios.get(
                    `${API_BASE_URL}/api/student/progress/${courseId}`
                );
                currentProgress = progressResponse.data;
            } catch (error) {
                // No progress yet, start from 0
                console.log('No existing progress');
            }

            // Calculate new completed lessons count
            // If current lesson order is greater than current progress, update to current lesson order
            const newCompletedLessons = currentProgress 
                ? Math.max(currentProgress.completedLessons, currentLesson.order)
                : currentLesson.order;

            // Update progress
            await axios.post(
                `${API_BASE_URL}/api/student/progress/${courseId}/${newCompletedLessons}`
            );

            return { success: true };
            
        } catch (error) {
            console.error('Failed to update progress:', error);
            
            return fail(500, { 
                error: 'Failed to update progress. Please try again.'
            });
        }
    }
};