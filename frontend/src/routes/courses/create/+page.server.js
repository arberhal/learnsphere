import axios from 'axios';
import { fail, redirect } from '@sveltejs/kit';

export const actions = {
	default: async ({ request, locals }) => {
		// ✅ Add locals
		const jwt_token = locals.jwt_token; // ✅ Get JWT

		const data = await request.formData();
		const title = data.get('title');
		const description = data.get('description');

		// Parse lessons from form data
		const lessons = [];
		let lessonIndex = 0;

		while (data.has(`lessons[${lessonIndex}].title`)) {
			const lessonTitle = data.get(`lessons[${lessonIndex}].title`);
			const lessonContent = data.get(`lessons[${lessonIndex}].content`);
			const lessonVideoUrl = data.get(`lessons[${lessonIndex}].videoUrl`);
			const lessonOrder = data.get(`lessons[${lessonIndex}].order`);

			if (lessonTitle || lessonContent) {
				lessons.push({
					title: lessonTitle?.trim() || '',
					content: lessonContent?.trim() || '',
					videoUrl: lessonVideoUrl?.trim() || '',
					order: parseInt(lessonOrder) || lessonIndex + 1
				});
			}

			lessonIndex++;
		}

		// Validation
		const errors = {};

		if (!title || title.trim().length === 0) {
			errors.title = 'Title is required';
		}

		if (!description || description.trim().length === 0) {
			errors.description = 'Description is required';
		}

		// Validate lessons
		for (let i = 0; i < lessons.length; i++) {
			if (!lessons[i].title || lessons[i].title.length === 0) {
				errors[`lesson_${i}_title`] = `Lesson ${i + 1} title is required`;
			}
			if (!lessons[i].content || lessons[i].content.length === 0) {
				errors[`lesson_${i}_content`] = `Lesson ${i + 1} content is required`;
			}
		}

		if (Object.keys(errors).length > 0) {
			return fail(400, { errors, title, description });
		}

		try {
			// Step 1: Create the course
			const courseResponse = await axios.post(
				`http://localhost:8080/api/teacher/courses`,
				{
					title: title.trim(),
					description: description.trim()
				},
				{
					headers: {
						'Content-Type': 'application/json',
						Authorization: `Bearer ${jwt_token}` // ✅ ADD JWT!
					}
				}
			);

			console.log('📦 Course response:', courseResponse.data);
			console.log('🔍 Course ID:', courseResponse.data.id);
			console.log('🔍 Course _id:', courseResponse.data._id);

			const courseId = courseResponse.data.id || courseResponse.data._id; // ✅ Try both!

			// Step 2: Create lessons if any
			if (lessons.length > 0) {
				const lessonPromises = lessons.map((lesson) =>
					axios.post(
						`http://localhost:8080/api/teacher/courses/${courseId}/lessons`,
						{
							title: lesson.title,
							content: lesson.content,
							videoUrl: lesson.videoUrl || '',
							order: lesson.order
						},
						{
							headers: {
								'Content-Type': 'application/json',
								Authorization: `Bearer ${jwt_token}` // ✅ ADD JWT!
							}
						}
					)
				);

				// Create all lessons in parallel
				await Promise.all(lessonPromises);
			}

			// Redirect to the newly created course or course list
			throw redirect(303, `/courses/${courseId}`);
		} catch (error) {
			console.error('Failed to create course or lessons:', error);

			// Check if it's our redirect
			if (error.status === 303) {
				throw error;
			}

			return fail(500, {
				error: 'Failed to create course. Please try again.',
				title,
				description
			});
		}
	}
};
