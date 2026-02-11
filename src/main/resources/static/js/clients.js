function enableEdit(btn) {
    const row = btn.closest('tr');

    // Скрива текстовете, показва инпутите
    row.querySelectorAll('.view-field').forEach(el => el.style.display = 'none');
    row.querySelectorAll('.edit-field').forEach(el => el.style.display = 'block'); // Вече включва и username

    // Сменя бутоните
    row.querySelector('.view-actions').style.display = 'none';
    row.querySelector('.edit-actions').style.display = 'inline-block';
}

function cancelEdit(btn) {
    const row = btn.closest('tr');

    // Връща обратно
    row.querySelectorAll('.edit-field').forEach(el => el.style.display = 'none');
    row.querySelectorAll('.view-field').forEach(el => el.style.display = 'inline-block'); // или block

    // Връща бутоните
    row.querySelector('.edit-actions').style.display = 'none';
    row.querySelector('.view-actions').style.display = 'inline-block';
}

function updateClient(btn) {
    const row = btn.closest('tr');
    const clientId = row.getAttribute('data-id');

    const updatedData = {
        name: row.querySelector('input[name="name"]').value,
        phoneNumber: row.querySelector('input[name="phoneNumber"]').value,
        username: row.querySelector('input[name="username"]').value
    };

    fetch(`/client/update/${clientId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedData)
    })
        .then(response => {
            if (response.ok) {
                row.querySelector('.name-text').textContent = updatedData.name;
                row.querySelector('.phone-text').textContent = updatedData.phoneNumber;

                const userBadge = row.querySelector('.username-badge');
                userBadge.textContent = updatedData.username ? '@' + updatedData.username : 'Guest';

                cancelEdit(btn);
            } else {
                return response.text().then(text => { alert('Error: ' + text); });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to update.');
        });
}