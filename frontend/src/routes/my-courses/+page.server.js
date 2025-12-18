import axios from 'axios';
import { redirect } from '@sveltejs/kit';

export async function load({ locals }) {
    const jwt_token = locals.jwt_token;

    if (!locals.isAuthenticated || !jwt_token) {
        throw redirect(303, '/login');
    }

    try {
        // Fetch courses where student has made progress
        const coursesResponse = await axios.get(
            'http://localhost:8080/api/my-courses',
            {
                headers: {
                    Authorization: `Bearer ${jwt_token}`
                }
            }
        );

        const myCourses = coursesResponse.data;

        // Fetch progress for each course
        const progressPromises = myCourses.map(course => 
            axios.get(
                `http://localhost:8080/api/student/progress/${course.id}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt_token}`
                    }
                }
            ).catch(() => ({ data: { completedLessons: 0, percent: 0 } }))
        );

        const progressResults = await Promise.all(progressPromises);

        // Create a map of courseId -> progress
        const progressMap = {};
        myCourses.forEach((course, index) => {
            progressMap[course.id] = progressResults[index].data;
        });

        return {
            myCourses,
            progressMap
        };
    } catch (error) {
        console.error('Failed to load my courses:', error);
        return {
            myCourses: [],
            progressMap: {}
        };
    }
}