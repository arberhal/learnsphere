import axios from "axios";
import { API_BASE_URL } from '$env/static/private';

export async function load({ params }) {
    const courseId = params.courseId;

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
}
