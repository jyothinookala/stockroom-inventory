const form = document.querySelector('#login-form') || document.querySelector('#register-form') || document.querySelector('#forgot-form');
const errorMessage = document.querySelector('#form-error');

form.addEventListener('submit', async event => {
    event.preventDefault();
    errorMessage.textContent = '';
    const formData = Object.fromEntries(new FormData(form).entries());
    const endpoint = form.id === 'login-form' ? '/api/auth/login' : form.id === 'register-form' ? '/api/auth/register' : '/api/auth/forgot-password';
    try {
        const response = await fetch(endpoint, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(formData) });
        const result = await response.json();
        if (!response.ok) throw new Error(result.message || 'Something went wrong.');
        window.location.href = form.id === 'forgot-form' ? '/login' : '/inventory';
    } catch (error) {
        errorMessage.textContent = error.message;
    }
});