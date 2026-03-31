// js/report_popout.js
// Manejo del modal/popout para mostrar el JSON_Reporte en lenguaje natural.
// Busca botones con .view-json-btn y usa el overlay con id rpOverlay, contenido en rpContent.

(function () {
    function decodeB64ToString(b64) {
        try {
            var binary = atob(b64);
            var bytes = Uint8Array.from(binary, c => c.charCodeAt(0));
            var decoder = new TextDecoder('utf-8');
            return decoder.decode(bytes);
        } catch (e) {
            return '';
        }
    }

    /**
     * Genera el bloque de resumen/generalización a partir del array de car_reports.
     * Calcula: total revisados, OK/No OK, lista de incidencias, lista de observaciones.
     * Retorna un array de líneas con prefijos especiales para el renderizado estilizado.
     */
    function buildSummarySection(cars) {
        if (!Array.isArray(cars) || cars.length === 0) return [];

        var total = cars.length;
        var okCount = 0, nokCount = 0;
        var incidencias = [], observaciones = [];

        cars.forEach(function (cr) {
            var nombre = cr.car_name || cr.name || 'C.A.R.';
            var tieneIncidencia = !!(cr.incidencia || cr.incidencias);
            var tieneObs = !!(cr.observacion || cr.observaciones);

            // No OK si tiene campo incidencia, o si algún booleano en respuestas es false
            var esNOK = tieneIncidencia;
            if (!esNOK && cr.responses && typeof cr.responses === 'object') {
                Object.values(cr.responses).forEach(function (v) {
                    if (v === false) esNOK = true;
                });
            }
            if (esNOK) nokCount++; else okCount++;

            if (tieneIncidencia) {
                incidencias.push('__INCIDENT__• ' + nombre + ': ' + (cr.incidencia || cr.incidencias));
            }
            if (tieneObs) {
                observaciones.push('__OBS__• ' + nombre + ': ' + (cr.observacion || cr.observaciones));
            }
        });

        var lines = [
            '__SEPARATOR__',
            '__HEADER__═══ RESUMEN DEL REPORTE ═══',
            '__STATS__Total revisados: ' + total + '   ✅ OK: ' + okCount + '   ❌ No OK: ' + nokCount
        ];

        if (incidencias.length > 0) {
            lines.push('__SUBTITLE__Incidencias encontradas:');
            incidencias.forEach(function (l) { lines.push(l); });
        }
        if (observaciones.length > 0) {
            lines.push('__SUBTITLE__Observaciones:');
            observaciones.forEach(function (l) { lines.push(l); });
        }

        return lines;
    }

    function humanizeReport(jsonObj) {
        // Esperamos estructura: { area: {...}, car_reports: [ { car_name, responses: {...}, observacion?, incidencia? }, ... ] }
        var lines = [];
        if (!jsonObj || typeof jsonObj !== 'object') return ['Reporte vacío o inválido.'];

        if (jsonObj.area) {
            var an = jsonObj.area.area_name || jsonObj.area.Nombre_Area || jsonObj.area.name || '';
            var ad = jsonObj.area.area_description || jsonObj.area.Descripcion_Area || jsonObj.area.description || '';
            if (an) lines.push('Sistema: ' + an);
            if (ad) lines.push('Descripción: ' + ad);
            lines.push(''); // separación
        }

        var cars = jsonObj.car_reports || jsonObj.carReports || [];
        if (!Array.isArray(cars) || cars.length === 0) {
            lines.push('No hay C.A.R. reportados.');
            return lines;
        }

        cars.forEach(function (cr, idx) {
            var cname = cr.car_name || cr.name || ('C.A.R. ' + (idx + 1));
            lines.push('--- ' + cname + ' ---');
            if (cr.responses && typeof cr.responses === 'object') {
                Object.keys(cr.responses).forEach(function (label) {
                    var val = cr.responses[label];
                    var display = String(val);
                    if (typeof val === 'boolean') display = (val ? 'Sí' : 'No');
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

        // Bloque de resumen/generalización al final (requerimiento de la reunión)
        var summaryLines = buildSummarySection(cars);
        if (summaryLines.length > 0) {
            lines = lines.concat(summaryLines);
        }

        return lines;
    }

    // Modal control
    var overlay = document.getElementById('rpOverlay');
    var contentEl = document.getElementById('rpContent');
    var closeBtn = document.getElementById('rpClose');
    var titleEl = document.getElementById('rpTitle');

    function openModal(title, lines) {
        contentEl.innerHTML = '';
        titleEl.textContent = title || 'Detalle del reporte';

        lines.forEach(function (l) {
            var p = document.createElement('div');

            if (l === '__SEPARATOR__') {
                // Línea divisora antes del resumen
                p.style.cssText = 'border-top: 2px solid #4a90d9; margin: 16px 0 8px 0;';
            } else if (l.startsWith('__HEADER__')) {
                p.textContent = l.replace('__HEADER__', '');
                p.style.cssText = 'font-weight: bold; font-size: 1em; color: #1a3a5c; letter-spacing: 0.5px; margin-bottom: 6px;';
            } else if (l.startsWith('__STATS__')) {
                p.textContent = l.replace('__STATS__', '');
                p.style.cssText = 'font-weight: 600; background: #f0f6ff; border-radius: 6px; padding: 6px 10px; margin-bottom: 4px;';
            } else if (l.startsWith('__SUBTITLE__')) {
                p.textContent = l.replace('__SUBTITLE__', '');
                p.style.cssText = 'font-weight: 600; margin-top: 8px; text-decoration: underline; color: #333;';
            } else if (l.startsWith('__INCIDENT__')) {
                p.textContent = l.replace('__INCIDENT__', '');
                p.style.cssText = 'color: #c0392b; padding-left: 8px;';
            } else if (l.startsWith('__OBS__')) {
                p.textContent = l.replace('__OBS__', '');
                p.style.cssText = 'color: #27ae60; padding-left: 8px;';
            } else {
                p.textContent = l;
            }

            contentEl.appendChild(p);
        });

        overlay.style.display = 'flex';
        overlay.setAttribute('aria-hidden', 'false');
    }

    function closeModal() {
        overlay.style.display = 'none';
        overlay.setAttribute('aria-hidden', 'true');
        contentEl.innerHTML = '';
    }

    closeBtn.addEventListener('click', closeModal);
    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) closeModal();
    });
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeModal();
    });

    // Attach handlers to buttons (delegation)
    document.addEventListener('click', function (e) {
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
        openModal('Reporte #' + reportId, lines);
    });

})();
