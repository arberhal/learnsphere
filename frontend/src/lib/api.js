const API = import.meta.env.VITE_API_BASE_URL;

// ============ Course API ============

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

// ============ Lesson API ============

export async function getLessons(courseId) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}/lessons`);
  if (!res.ok) throw new Error('Failed to load lessons');
  return res.json();
}

export async function createLesson(courseId, title, content, videoUrl, order) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}/lessons`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, content, videoUrl, order })
  });

  if (!res.ok) throw new Error('Failed to create lesson');
  return res.json();
}

export async function updateLesson(courseId, lessonId, title, content, videoUrl, order) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}/lessons/${lessonId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, content, videoUrl, order })
  });

  if (!res.ok) throw new Error('Failed to update lesson');
  return res.json();
}

export async function deleteLesson(courseId, lessonId) {
  const res = await fetch(`${API}/api/teacher/courses/${courseId}/lessons/${lessonId}`, {
    method: 'DELETE'
  });

  if (!res.ok) throw new Error('Failed to delete lesson');
}

// ============ Progress API ============

export async function getProgress(courseId) {
  const res = await fetch(`${API}/api/student/progress/${courseId}`);
  if (!res.ok) throw new Error('Failed to load progress');
  return res.json();
}

export async function updateProgress(courseId, completedLessons) {
  const res = await fetch(`${API}/api/student/progress/${courseId}/${completedLessons}`, {
    method: 'POST'
  });

  if (!res.ok) throw new Error('Failed to update progress');
  return res.json();
}