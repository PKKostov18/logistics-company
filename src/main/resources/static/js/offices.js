/**
 * Public Map Script
 * Управлява визуализацията на офисите върху картата (Leaflet + OpenStreetMap)
 */

// --- КОНФИГУРАЦИЯ ---
const CONFIG = {
    DEFAULT_CENTER: [42.7339, 25.4858], // Географски център на България
    DEFAULT_ZOOM: 7,
    ZOOM_ON_FOCUS: 16,
    ANIMATION_DURATION: 1.5,
    API_DELAY_MS: 700 // Забавяне между заявките, за да не бъдем блокирани от Nominatim
};

// Глобални променливи
let map;
let markers = []; // Тук пазим всички добавени маркери, за да ги намираме бързо

document.addEventListener("DOMContentLoaded", function () {
    initMap();
    loadOffices();
});

/**
 * Инициализира картата и слоя с плочки (Tile Layer)
 */
function initMap() {
    map = L.map('map').setView(CONFIG.DEFAULT_CENTER, CONFIG.DEFAULT_ZOOM);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);
}

/**
 * Зарежда офисите от глобалната променлива window.officesData (подадена от Thymeleaf)
 */
function loadOffices() {
    const offices = window.officesData || [];

    if (offices.length === 0) {
        console.warn("LogiTrace: Няма намерени офиси за зареждане.");
        return;
    }

    // Обхождаме всеки офис и търсим координатите му
    offices.forEach((office, index) => {
        // Използваме setTimeout, за да спазим правилата на Nominatim API (Rate Limiting)
        setTimeout(() => {
            geocodeAndPlot(office);
        }, index * CONFIG.API_DELAY_MS);
    });
}

/**
 * Преобразува адрес в координати и добавя маркер на картата
 * @param {Object} office - Обект с данни за офиса {name, address}
 */
function geocodeAndPlot(office) {
    if (!office.address) return;

    // Добавяме ", Bulgaria", за да уточним търсенето
    const query = `${office.address}, Bulgaria`;
    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data && data.length > 0) {
                const lat = data[0].lat;
                const lon = data[0].lon;

                createMarker(lat, lon, office);
            } else {
                console.warn(`Неуспешно намиране на адрес: ${office.address}`);
            }
        })
        .catch(err => console.error("Грешка при геокодиране:", err));
}

/**
 * Създава маркер и го добавя към картата и масива markers
 */
function createMarker(lat, lon, office) {
    const marker = L.marker([lat, lon]).addTo(map);

    // ВАЖНО: Прикачваме данните за офиса директно към маркера.
    // Това ни позволява по-късно да го намерим без нови API заявки.
    marker.officeData = {
        name: office.name,
        address: office.address
    };

    // Tooltip (балонче при посочване)
    marker.bindTooltip(`<b>${office.name}</b>`, {
        permanent: false,
        direction: 'top'
    });

    // Popup (прозорец при клик)
    const popupContent = `
        <div style="text-align:center;">
            <h3 style="margin: 0 0 5px 0; color: #333;">${office.name}</h3>
            <p style="margin: 0; font-size: 0.9em; color: #666;">${office.address}</p>
        </div>
    `;
    marker.bindPopup(popupContent);

    // Запазваме в масива
    markers.push(marker);
}

/**
 * Фокусира картата върху конкретен офис при клик от списъка.
 * ОПТИМИЗАЦИЯ: Търси във вече заредените маркери, вместо да прави нова API заявка.
 * * @param {string} name - Името на офиса
 * @param {string} address - Адресът на офиса (за резервно сравнение)
 */
function focusOnOffice(name, address) {
    // 1. Търсим маркера в паметта
    const targetMarker = markers.find(m => m.officeData.name === name);

    if (targetMarker) {
        // 2. Взимаме координатите от самия маркер
        const latLng = targetMarker.getLatLng();

        // 3. Плавно плъзгане на картата
        map.flyTo(latLng, CONFIG.ZOOM_ON_FOCUS, {
            duration: CONFIG.ANIMATION_DURATION
        });

        // 4. Отваряме информационното прозорче
        targetMarker.openPopup();

        // 5. Скролваме екрана до картата (полезно за мобилни устройства)
        const mapElement = document.getElementById('map');
        if (mapElement) {
            mapElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    } else {
        console.error("Маркерът за този офис все още не е зареден или адресът е грешен.");
        alert("Картата все още зарежда данните за този офис. Моля, изчакайте.");
    }
}