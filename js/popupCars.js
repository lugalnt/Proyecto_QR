// popupCars.js
(function () {
  // Insertar estilos (para que no necesites un CSS aparte)
  const css = `
  .cars-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.45);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
  }
  .cars-modal {
    width: min(760px, 92%);
    max-height: 86vh;
    overflow: auto;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 12px 30px rgba(0,0,0,0.25);
    padding: 18px;
    font-family: system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
  }
  .cars-header {
    display:flex;
    justify-content: space-between;
    align-items:center;
    gap:12px;
    margin-bottom: 12px;
  }
  .cars-title { font-size: 1.05rem; font-weight: 600; }
  .cars-close {
    background: transparent;
    border: none;
    font-size: 1.1rem;
    cursor: pointer;
    padding: 6px;
  }
  .cars-list { display: flex; flex-direction: column; gap: 12px; }
  .car-card {
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    padding: 12px;
    background: #fafafa;
  }
  .car-name { font-weight: 700; margin-bottom: 8px; }
  .prop-row { display:flex; gap:10px; flex-wrap:wrap; align-items:center; font-size:0.95rem; }
  .prop-label { font-weight:600; min-width:140px; }
  .prop-meta { color:#555; font-size:0.9rem; }
  .empty-msg { color:#666; padding:12px; text-align:center; }
  `;

  function ensureStyle() {
    if (document.getElementById('cars-popup-styles')) return;
    const s = document.createElement('style');
    s.id = 'cars-popup-styles';
    s.textContent = css;
    document.head.appendChild(s);
  }

  function crearModal() {
    ensureStyle();
    const overlay = document.createElement('div');
    overlay.className = 'cars-overlay';
    overlay.tabIndex = -1; // para manejar focus

    const modal = document.createElement('div');
    modal.className = 'cars-modal';
    modal.setAttribute('role', 'dialog');
    modal.setAttribute('aria-modal', 'true');

    // Header
    const header = document.createElement('div');
    header.className = 'cars-header';
    const title = document.createElement('div');
    title.className = 'cars-title';
    title.textContent = 'CARS';
    const closeBtn = document.createElement('button');
    closeBtn.className = 'cars-close';
    closeBtn.innerHTML = '✕';
    closeBtn.title = 'Cerrar';
    header.appendChild(title);
    header.appendChild(closeBtn);

    const content = document.createElement('div');
    content.className = 'cars-list';

    modal.appendChild(header);
    modal.appendChild(content);
    overlay.appendChild(modal);

    // close handlers
    function cerrar() {
      if (overlay && overlay.parentNode) overlay.parentNode.removeChild(overlay);
      document.removeEventListener('keydown', onKeyDown);
    }
    closeBtn.addEventListener('click', cerrar);
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) cerrar();
    });
    function onKeyDown(e) {
      if (e.key === 'Escape') cerrar();
    }
    document.addEventListener('keydown', onKeyDown);

    // focus
    setTimeout(() => closeBtn.focus(), 50);

    return { overlay, modal, content, cerrar };
  }

  function renderProperties(properties) {
    const container = document.createElement('div');
    container.style.display = 'flex';
    container.style.flexDirection = 'column';
    container.style.gap = '6px';

    if (!Array.isArray(properties) || properties.length === 0) {
      const p = document.createElement('div');
      p.className = 'empty-msg';
      p.textContent = 'Sin propiedades';
      container.appendChild(p);
      return container;
    }

    properties.forEach(prop => {
      const row = document.createElement('div');
      row.className = 'prop-row';

      const label = document.createElement('div');
      label.className = 'prop-label';
      label.textContent = prop.label ?? '(sin label)';

      const meta = document.createElement('div');
      meta.className = 'prop-meta';

      // armar texto según tipo y otras keys
      const tipo = prop.type ?? 'unknown';
      let detalles = `Tipo: ${tipo}`;
      if (prop.placeholder) detalles += ` • placeholder: "${prop.placeholder}"`;
      if (prop.default !== undefined) detalles += ` • default: ${JSON.stringify(prop.default)}`;
      if (prop.min !== undefined || prop.max !== undefined) {
        detalles += ` • rango: ${prop.min ?? '-'} – ${prop.max ?? '-'}`;
      }
      if (prop.step !== undefined) detalles += ` • step: ${prop.step}`;

      meta.textContent = detalles;

      row.appendChild(label);
      row.appendChild(meta);
      container.appendChild(row);
    });

    return container;
  }

  function mostrarCars(areaObj) {
    const { overlay, content, cerrar } = crearModal();

    // titulo adicional si hay nombre de area
    const headerTitle = overlay.querySelector('.cars-title');
    if (areaObj && areaObj.area_name) {
      headerTitle.textContent = `CARS · ${areaObj.area_name}`;
    }

    // si no hay cars o no es array
    const cars = Array.isArray(areaObj && areaObj.cars) ? areaObj.cars : null;
    if (!cars || cars.length === 0) {
      const empty = document.createElement('div');
      empty.className = 'empty-msg';
      empty.textContent = 'No hay CARS para mostrar';
      content.appendChild(empty);
      document.body.appendChild(overlay);
      return { overlay, cerrar };
    }

    cars.forEach((car, idx) => {
      const card = document.createElement('div');
      card.className = 'car-card';

      const name = document.createElement('div');
      name.className = 'car-name';
      name.textContent = car.name ?? `CAR ${idx+1}`;

      const desc = document.createElement('div');
      desc.className = 'prop-meta';
      if (car.description) desc.textContent = car.description;

      card.appendChild(name);
      if (car.description) card.appendChild(desc);

      const propsNode = renderProperties(car.properties);
      card.appendChild(propsNode);

      content.appendChild(card);
    });

    document.body.appendChild(overlay);
    return { overlay, cerrar };
  }

  // Exponer una API simple
  window.CarsPopup = {
    showCars: function(areaObj) {
      try {
        mostrarCars(areaObj);
      } catch (err) {
        console.error('Error mostrando cars:', err);
      }
    },
    attachButton: function(buttonId, areaObj) {
      const btn = document.getElementById(buttonId);
      if (!btn) {
        console.warn('attachButton: no se encontró el botón con id', buttonId);
        return;
      }
      btn.addEventListener('click', () => {
        this.showCars(areaObj);
      });
    }
  };
})();
