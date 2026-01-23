document.addEventListener('DOMContentLoaded', (event) => {

    const registrationForm = document.getElementById('registrationForm');

    if (registrationForm) {
        registrationForm.addEventListener('submit', function(event) {
            event.preventDefault();

            const errorMessageDiv = document.getElementById('error-message');
            const submitButton = registrationForm.querySelector('button[type="submit"]');

            if (errorMessageDiv) {
                errorMessageDiv.textContent = '';
                errorMessageDiv.style.display = 'none';
            }

            const formData = new FormData(event.target);
            const data = Object.fromEntries(formData.entries());

            if (data.password !== data.confirmPassword) {
                if (errorMessageDiv) {
                    errorMessageDiv.textContent = 'Passwords do not match.';
                    errorMessageDiv.style.display = 'block';
                }
                return;
            }

            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = 'Registering...';
            }

            fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: data.username,
                    firstName: data.firstName,
                    lastName: data.lastName,
                    email: data.email,
                    phoneNumber: data.phoneNumber,
                    password: data.password
                })
            })
                .then(response => {
                    if (response.ok) {
                        window.location.href = '/login?success';
                    } else {
                        return response.text().then(text => {
                            if (errorMessageDiv) {
                                errorMessageDiv.textContent = text;
                                errorMessageDiv.style.display = 'block';
                            }
                        });
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    if (errorMessageDiv) {
                        errorMessageDiv.textContent = 'A network error occurred. Please try again.';
                        errorMessageDiv.style.display = 'block';
                    }
                })
                .finally(() => {
                    if (submitButton) {
                        submitButton.disabled = false;
                        submitButton.textContent = 'Sign Up';
                    }
                });
        });
    }
});