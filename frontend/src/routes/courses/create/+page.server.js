import axios from 'axios';
import { API_BASE_URL } from '$env/static/private';
import { fail, redirect } from '@sveltejs/kit';

export const actions = {
  default: async ({ request }) => {
    const data = await request.formData();
    const title = data.get('title');
    const description = data.get('description');

    // Validation
    const errors = {};
    
    if (!title || title.trim().length === 0) {
      errors.title = 'Title is required';
    }
    
    if (!description || description.trim().length === 0) {
      errors.description = 'Description is required';
    }

    if (Object.keys(errors).length > 0) {
      return fail(400, { errors });
    }

    try {
      // Call backend API to create course
      const response = await axios.post(
        `${API_BASE_URL}/api/teacher/courses`,
        {
          title: title.trim(),
          description: description.trim()
        },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      // Redirect to the courses list or course detail page
      throw redirect(303, '/');
      
    } catch (error) {
      console.error('Failed to create course:', error);
      
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