function enableEdit(button) {
    const row = button.closest('tr');
    row.querySelectorAll('.view-field, .view-actions').forEach(el => el.style.display = 'none');
    row.querySelectorAll('.edit-field, .edit-actions').forEach(el => el.style.display = '');
}

function cancelEdit(button) {
    const row = button.closest('tr');
    row.querySelectorAll('.view-field, .view-actions').forEach(el => el.style.display = '');
    row.querySelectorAll('.edit-field, .edit-actions').forEach(el => el.style.display = 'none');
}

function updateClient(button) {
    const row = button.closest('tr');
    const id = row.getAttribute('data-id');
    const name = row.querySelector('input[name="name"]').value;
    const phoneNumber = row.querySelector('input[name="phoneNumber"]').value;

    const formData = new URLSearchParams();
    formData.append('name', name);
    formData.append('phoneNumber', phoneNumber);

    fetch('/clients/update/' + id, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData
    })
        .then(response => {
            if (response.ok) {
                // Обновяваме текста в "view" режима
                row.querySelector('.name-text').innerText = name;
                row.querySelector('.phone-text').innerText = phoneNumber;

                // Връщаме изгледа към "четене"
                row.querySelectorAll('.view-field, .view-actions').forEach(el => el.style.display = '');
                row.querySelectorAll('.edit-field, .edit-actions').forEach(el => el.style.display = 'none');

                // Зелен ефект за успех
                row.style.backgroundColor = 'rgba(16, 185, 129, 0.2)'; // #d1fae5 с прозрачност
                setTimeout(() => row.style.backgroundColor = '', 500);
            } else {
                alert('Error updating client. Please check the data.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('A network error occurred.');
        });
}