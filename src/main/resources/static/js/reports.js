function toggleFilters() {
    const type = document.getElementById('reportType').value;

    // Елементи
    const placeholder = document.getElementById('filter-placeholder');
    const employeeSelect = document.getElementById('filter-employee');
    const clientSelect = document.getElementById('filter-client');

    // Ресет: Скриваме всичко
    if(placeholder) placeholder.style.display = 'none';
    if(employeeSelect) employeeSelect.style.display = 'none';
    if(clientSelect) clientSelect.style.display = 'none';

    // Махаме name атрибута, за да не се пращат празни параметри
    if(employeeSelect) employeeSelect.removeAttribute('name');
    if(clientSelect) clientSelect.removeAttribute('name');

    // Логика
    if (type === 'by_employee') {
        if(employeeSelect) {
            employeeSelect.style.display = 'block';
            employeeSelect.setAttribute('name', 'employeeId');
        }
    } else if (type === 'sent_by_client' || type === 'received_by_client') {
        if(clientSelect) {
            clientSelect.style.display = 'block';
            clientSelect.setAttribute('name', 'clientId');
        }
    } else {
        if(placeholder) placeholder.style.display = 'block';
    }
}

// Стартиране при зареждане
document.addEventListener('DOMContentLoaded', toggleFilters);