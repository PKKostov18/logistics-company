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
    API_DELAY_MS: 700
};

// --- РЪЧНО ЗАДАДЕНИ КООРДИНАТИ ---
const FIXED_COORDINATES = {
    "Sofia Central":   { lat: 42.686347974763784, lon: 23.317319303068395 }, // бул. Витоша 100
    "Burgas Central":  { lat: 42.49213373458485, lon: 27.47288188490549 }, // ул. Александровска 10 (до ЖП Гара)
    "Plovdiv Central": { lat: 42.14124942493237, lon: 24.749587550403838 }, // ул. Иван Вазов 5 (до пл. Централен)
    "Varna Central":   { lat: 43.21009909305862, lon: 27.908600768537255 }  // бул. Вл. Варненчик 50 (близо до Образцов дом)
};

// Глобални променливи
let map;
let markers = [];

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
 * Зарежда офисите от глобалната променлива window.officesData
 */
function loadOffices() {
    const offices = window.officesData || [];

    if (offices.length === 0) {
        console.warn("LogiTrace: Няма намерени офиси за зареждане.");
        return;
    }

    offices.forEach((office, index) => {
        // 1. ПРОВЕРКА: Имаме ли готови координати за този офис?
        if (FIXED_COORDINATES[office.name]) {
            const coords = FIXED_COORDINATES[office.name];
            // Създаваме маркера веднага, без да чакаме API
            createMarker(coords.lat, coords.lon, office);
        }
        // 2. Ако нямаме (за бъдещи офиси), ползваме старата логика с търсене
        else {
            setTimeout(() => {
                geocodeAndPlot(office);
            }, index * CONFIG.API_DELAY_MS);
        }
    });
}

/**
 * Преобразува адрес в координати и добавя маркер на картата (за непознати офиси)
 */
function geocodeAndPlot(office) {
    if (!office.address) return;

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

    // Прикачваме данните за офиса към маркера
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
            <p style="margin: 0; font-size: 0.9em; color: #666;">
                <i class="fa-solid fa-location-dot" style="color: #ef4444;"></i>
                ${office.address}
            </p>
        </div>
    `;
    marker.bindPopup(popupContent);

    // Запазваме в масива за функцията focusOnOffice
    markers.push(marker);
}

/**
 * Фокусира картата върху конкретен офис при клик от списъка.
 */
function focusOnOffice(name, address) {
    // Търсим маркера в паметта
    const targetMarker = markers.find(m => m.officeData.name === name);

    if (targetMarker) {
        const latLng = targetMarker.getLatLng();

        // Плавно плъзгане на картата
        map.flyTo(latLng, CONFIG.ZOOM_ON_FOCUS, {
            duration: CONFIG.ANIMATION_DURATION
        });

        // Отваряме информационното прозорче
        targetMarker.openPopup();

        // Скролваме екрана до картата
        const mapElement = document.getElementById('map');
        if (mapElement) {
            mapElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    } else {
        // Fallback ако маркерът още не е заредил (само за динамичните)
        console.warn("Маркерът все още не е зареден.");
    }
}