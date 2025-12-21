import { redirect } from '@sveltejs/kit';
import axios from 'axios';

export async function handle({ event, resolve }) {
    const jwt_token = event.cookies.get('jwt_token');
    const userInfoCookie = event.cookies.get('user_info');

    event.locals.jwt_token = jwt_token;

    if (userInfoCookie) {
        try {
            event.locals.user = JSON.parse(decodeURIComponent(userInfoCookie));

            if (event.locals.user.user_roles && Array.isArray(event.locals.user.user_roles)) {
                event.locals.isTeacher = event.locals.user.user_roles.includes('teacher');
                event.locals.role = event.locals.user.user_roles[0];
            } else {
                event.locals.isTeacher = false;
                event.locals.role = 'student';
            }
        } catch (error) {
            console.error('Failed to parse user info cookie:', error);
            event.locals.user = {};
            event.locals.isTeacher = false;
            event.locals.role = 'student';
        }
    } else {
        event.locals.user = {};
        event.locals.isTeacher = false;
        event.locals.role = null;
    }

    if (jwt_token && event.locals.user && event.locals.user.name) {
        event.locals.isAuthenticated = true;
    } else {
        event.locals.isAuthenticated = false;
    }

    if (event.request.method === 'OPTIONS') {
        return new Response(null, {
            status: 200,
            headers: {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
                'Access-Control-Allow-Headers': 'Content-Type, Authorization',
            },
        });
    }

    return resolve(event);
}
