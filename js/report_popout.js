// js/report_popout.js
// Manejo del modal/popout para mostrar el JSON_Reporte en lenguaje natural.
// Busca botones con .view-json-btn y usa el overlay con id rpOverlay, contenido en rpContent.

(function(){
    function decodeB64ToString(b64) {
        try {
            // atob maneja base64 ASCII; si hay UTF-8 se necesita una decodificación extra.
            var binary = atob(b64);
            // Convert binary string to UTF-8 string
            var bytes = Uint8Array.from(binary, c => c.charCodeAt(0));
            var decoder = new TextDecoder('utf-8');
            return decoder.decode(bytes);
        } catch (e) {
            return '';
        }
    }

    function humanizeReport(jsonObj) {
        // Esperamos estructura: { area: {...}, car_reports: [ { car_name, responses: {...}, observacion?, incidencia? }, ... ] }
        var lines = [];
        if (!jsonObj || typeof jsonObj !== 'object') return ['Reporte vacío o inválido.'];

        if (jsonObj.area) {
            var an = jsonObj.area.area_name || jsonObj.area.Nombre_Area || jsonObj.area.name || '';
            var ad = jsonObj.area.area_description || jsonObj.area.Descripcion_Area || jsonObj.area.description || '';
            if (an) lines.push('Área: ' + an);
            if (ad) lines.push('Descripción: ' + ad);
            lines.push(''); // separación
        }

        var cars = jsonObj.car_reports || jsonObj.carReports || [];
        if (!Array.isArray(cars) || cars.length === 0) {
            lines.push('No hay C.A.R. reportados.');
            return lines;
        }

        cars.forEach(function(cr, idx){
            var cname = cr.car_name || cr.name || ('C.A.R. ' + (idx+1));
            lines.push('--- ' + cname + ' ---');
            if (cr.responses && typeof cr.responses === 'object') {
                // respuestas es map label => value
                Object.keys(cr.responses).forEach(function(label){
                    var val = cr.responses[label];
                    var display = String(val);
                    // formateo booleano
                    if (typeof val === 'boolean') display = (val ? 'Sí' : 'No');
                    // si valor es number en string tratar igual
                    lines.push(String(label) + ': ' + display);
                });
            } else {
                lines.push('No hay respuestas.');
            }
            if (cr.observacion || cr.observaciones) {
                lines.push('Observación: ' + (cr.observacion || cr.observaciones));
            }
            if (cr.incidencia || cr.incidencias) {
                lines.push('Incidencia: ' + (cr.incidencia || cr.incidencias));
            }
            lines.push(''); // separación entre cars
        });

        return lines;
    }

    // Modal control
    var overlay = document.getElementById('rpOverlay');
    var contentEl = document.getElementById('rpContent');
    var closeBtn = document.getElementById('rpClose');
    var titleEl = document.getElementById('rpTitle');

    function openModal(title, lines) {
        // limpiar
        contentEl.innerHTML = '';
        titleEl.textContent = title || 'Detalle del reporte';
        lines.forEach(function(l){
            var p = document.createElement('div');
            p.textContent = l;
            contentEl.appendChild(p);
        });
        overlay.style.display = 'flex';
        overlay.setAttribute('aria-hidden','false');
    }

    function closeModal() {
        overlay.style.display = 'none';
        overlay.setAttribute('aria-hidden','true');
        contentEl.innerHTML = '';
    }

    closeBtn.addEventListener('click', closeModal);
    overlay.addEventListener('click', function(e){
        if (e.target === overlay) closeModal();
    });
    document.addEventListener('keydown', function(e){
        if (e.key === 'Escape') closeModal();
    });

    // Attach handlers to buttons (delegation)
    document.addEventListener('click', function(e){
        var btn = e.target.closest && e.target.closest('.view-json-btn');
        if (!btn) return;
        e.preventDefault();

        var b64 = btn.getAttribute('data-json') || '';
        var reportId = btn.getAttribute('data-report-id') || '';
        if (!b64) {
            openModal('Detalle reporte ' + reportId, ['No hay JSON disponible para este reporte.']);
            return;
        }

        var jsonStr = decodeB64ToString(b64);
        if (!jsonStr) {
            openModal('Detalle reporte ' + reportId, ['JSON inválido o no se pudo decodificar.']);
            return;
        }

        var parsed = null;
        try {
            parsed = JSON.parse(jsonStr);
        } catch (err) {
            openModal('Detalle reporte ' + reportId, ['JSON inválido: ' + err.message]);
            return;
        }

        var lines = humanizeReport(parsed);
        openModal('Reporte ' + reportId, lines);
    });

})();
