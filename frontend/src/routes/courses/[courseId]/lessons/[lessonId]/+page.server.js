import axios from "axios";
import { API_BASE_URL } from '$env/static/private';

export async function load({ params }) {
    const { courseId, lessonId } = params;

    const lessonsResponse = await axios.get(
        `${API_BASE_URL}/api/teacher/courses/${courseId}/lessons`
    );

    const lesson = lessonsResponse.data.find(
        l => l.id === lessonId
    );

    if (!lesson) {
        throw new Error('Lesson not found');
    }

    return {
        lesson,
        courseId
    };
}
