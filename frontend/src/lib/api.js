const API = import.meta.env.VITE_API_BASE_URL;

export async function getCourses() {
  const res = await fetch(`${API}/api/teacher/courses`);
  if (!res.ok) throw new Error('Failed to load courses');
  return res.json();
}

export async function createCourse(title, description) {
  const res = await fetch(`${API}/api/teacher/courses`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, description })
  });

  if (!res.ok) throw new Error('Failed to create course');
  return res.json();
}

export async function getCourse(courseId) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}`);
  if (!res.ok) throw new Error('Failed to load course');
  return res.json();
}

export async function updateCourse(courseId, title, description) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, description })
  });

  if (!res.ok) throw new Error('Failed to update course');
  return res.json();
}

export async function deleteCourse(courseId) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}`, {
    method: 'DELETE'
  });

  if (!res.ok) throw new Error('Failed to delete course');
}