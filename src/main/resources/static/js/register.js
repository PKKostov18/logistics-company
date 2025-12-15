
document.addEventListener('DOMContentLoaded', (event) => {


    const registrationForm = document.getElementById('registrationForm');


    if (registrationForm) {
        registrationForm.addEventListener('submit', function(event) {

            event.preventDefault();

            const errorMessageDiv = document.getElementById('error-message');
            if (errorMessageDiv) {
                errorMessageDiv.textContent = '';
            }


            const formData = new FormData(event.target);
            const data = Object.fromEntries(formData.entries());


            if (data.password !== data.confirmPassword) {
                if (errorMessageDiv) {
                    errorMessageDiv.textContent = 'Passwords do not match.';
                }
                return;
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

                        alert('Registration successful! You can now log in.');
                        window.location.href = '/login';
                    } else {

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