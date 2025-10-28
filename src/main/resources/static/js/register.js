/* register.js */
document.addEventListener('DOMContentLoaded', (event) => {
    // We wrap everything in DOMContentLoaded to ensure the HTML is loaded
    // before we try to find the form.

    const registrationForm = document.getElementById('registrationForm');

    // Check if the form actually exists on this page
    if (registrationForm) {
        registrationForm.addEventListener('submit', function(event) {
            // 1. Stop the default form submission
            event.preventDefault();

            const errorMessageDiv = document.getElementById('error-message');
            if (errorMessageDiv) {
                errorMessageDiv.textContent = ''; // Clear previous errors
            }

            // 2. Get data from the form
            const formData = new FormData(event.target);
            const data = Object.fromEntries(formData.entries());

            // 3. Client-side validation (passwords must match)
            if (data.password !== data.confirmPassword) {
                if (errorMessageDiv) {
                    errorMessageDiv.textContent = 'Passwords do not match.';
                }
                return;
            }

            // 4. Send data as JSON to the API endpoint
            fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                // 'confirmPassword' is not needed by the backend DTO, so we remove it
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
                        // 5. Success - redirect to login
                        alert('Registration successful! You can now log in.');
                        window.location.href = '/login';
                    } else {
                        // 6. Error - display message from the server
                        return response.text().then(text => {
                            if (errorMessageDiv) {
                                errorMessageDiv.textContent = 'Registration Failed: ' + text;
                            }
                        });
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    if (errorMessageDiv) {
                        errorMessageDiv.textContent = 'A network error occurred. Please try again.';
                    }
                });
        });
    }
});