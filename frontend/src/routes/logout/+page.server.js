import { redirect } from '@sveltejs/kit';

export function load({ cookies }) {
    // Clear the JWT cookie
    cookies.delete('jwt_token', { path: '/' });
    
    // Redirect to login
    throw redirect(303, '/login');
} 