// Функция за активиране на редакция
function enableEdit(id) {
    const row = document.getElementById('row-' + id);

    // Намираме всички input полета в реда
    const inputs = row.querySelectorAll('input');

    inputs.forEach(input => {
        input.disabled = false; // Отключваме полетата
    });

    // Фокусираме името
    const nameInput = row.querySelector('input[name="name"]');
    if(nameInput) nameInput.focus();

    // Сменяме бутоните
    const editBtn = row.querySelector('.btn-edit');
    const saveBtn = row.querySelector('.btn-save');

    if(editBtn) editBtn.style.display = 'none';
    if(saveBtn) saveBtn.style.display = 'inline-flex';
}

// Функция за запазване на промените
function updateClient(id) {
    const row = document.getElementById('row-' + id);

    // Събираме данните от input полетата
    const updatedData = {
        name: row.querySelector('input[name="name"]').value,
        phoneNumber: row.querySelector('input[name="phoneNumber"]').value,
        username: row.querySelector('input[name="username"]').value
    };

    // Изпращаме JSON към сървъра
    fetch(`/client/update/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedData)
    })
        .then(response => {
            if (response.ok || response.redirected) {
                // Успех! Заключва полетата обратно
                const inputs = row.querySelectorAll('input');
                inputs.forEach(input => input.disabled = true);

                // Връщаме бутоните
                const editBtn = row.querySelector('.btn-edit');
                const saveBtn = row.querySelector('.btn-save');

                if(saveBtn) saveBtn.style.display = 'none';
                if(editBtn) editBtn.style.display = 'inline-flex';
            } else {
                return response.text().then(text => { alert('Error: ' + text); });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to update client.');
        });
}