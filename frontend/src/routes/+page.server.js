import axios from "axios";

import { API_BASE_URL } from '$env/static/private';

export async function load() {
    try {
        const response = await axios.get(
            `${API_BASE_URL}/api/teacher/courses`
        );

        return {
            courses: response.data
        };
    } catch (error) {
        console.log('Error loading courses:', error);
        return {
            courses: []
        };
    }
}

export const actions = {
    createCourse: async ({ request }) => {
        const data = await request.formData();

        const course = {
            title: data.get('title'),
            description: data.get('description')
        };

        try {
            await axios.post(
                `${API_BASE_URL}/api/teacher/courses`,
                course,
                { headers: { "Content-Type": "application/json" } }
            );

            return { success: true };
        } catch (error) {
            console.log('Error creating course:', error);
            return { success: false };
        }
    }
};
