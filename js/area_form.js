// js/area_form.js - registro + función separada de edición
(function(){
  // ---- DOM ----
  const propEditor = document.getElementById('propEditor');
  const typeSettings = document.getElementById('typeSettings');
  const propLabel = document.getElementById('prop_label');
  const addPropertyBtn = document.getElementById('addPropertyBtn');
  const cancelPropertyBtn = document.getElementById('cancelPropertyBtn');
  const currentPropsContainer = document.getElementById('currentPropsContainer');
  const addCarBtn = document.getElementById('addCarBtn');
  const carsList = document.getElementById('carsList');
  const areaJsonInput = document.getElementById('area_json');
  const areaForm = document.getElementById('areaForm');
  const submitBtn = document.getElementById('submitBtn');

  // ---- Estado interno ----
  let editingType = null;
  let tempProperties = []; // propiedades del CAR en edición (draft)
  const cars = [];         // lista interna de CARs para este form
  let isEditing = false;   // si true -> al enviar será edición

  // ---- Helpers ----
  function escapeHtml(str){
    return String(str === undefined || str === null ? '' : str).replace(/[&<>"']/g, function(m){ return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]; });
  }
  function decodeHtmlEntities(str){
    if(!str) return str;
    const t = document.createElement('textarea');
    t.innerHTML = str;
    return t.value;
  }
  function parseAreaData(raw){
    if(!raw) return {};
    if(typeof raw === 'object') return raw;
    try { return JSON.parse(raw); } catch(e){
      try { return JSON.parse(decodeHtmlEntities(raw)); } catch(e2){ console.error('parseAreaData failed', e, e2); return {}; }
    }
  }

  // ---- Renderers: propiedades temporales y lista de cars ----
  function renderTempProps(){
    if(!currentPropsContainer) return;
    if(tempProperties.length === 0){
      currentPropsContainer.innerHTML = '<p class="muted">No hay propiedades agregadas (selecciona un tipo con los botones).</p>';
      return;
    }
    const ul = document.createElement('ul'); ul.className = 'prop-list';
    tempProperties.forEach((p, idx) => {
      const li = document.createElement('li');
      li.innerHTML = `<strong>${escapeHtml(p.label)}</strong> <div class="prop-meta">tipo: ${escapeHtml(p.type || '')}` + (p.type === 'range' ? ` — ${p.min}..${p.max} step ${p.step}` : '') + `</div>`;
      const rm = document.createElement('button'); rm.type='button'; rm.textContent='Eliminar'; rm.style.marginLeft='8px';
      rm.addEventListener('click', ()=>{ tempProperties.splice(idx,1); renderTempProps(); });
      li.appendChild(rm);
      ul.appendChild(li);
    });
    currentPropsContainer.innerHTML = '';
    currentPropsContainer.appendChild(ul);
  }

  function renderCars(){
    if(!carsList) return;
    carsList.innerHTML = '';
    if(!Array.isArray(cars) || cars.length === 0){
      carsList.innerHTML = '<div class="muted">No hay C.A.R agregados.</div>';
      saveJSONToHidden(); // keep hidden in sync
      return;
    }

    cars.forEach((c, i) => {
      const div = document.createElement('div'); div.className = 'car-item';
      const name = escapeHtml(c.name || c.Nombre_CAR || '');
      const propsLabels = (c.properties || []).map(p=>escapeHtml(p.label || p.nombre || '')).join(', ');
      div.innerHTML = `<b>${name}</b><div class="muted">Propiedades: ${propsLabels || 'Sin propiedades'}</div>`;
      const btns = document.createElement('div'); btns.style.marginTop='6px';
      const remove = document.createElement('button'); remove.type='button'; remove.textContent='Eliminar C.A.R'; remove.style.marginRight='6px';
      remove.addEventListener('click', ()=>{ if(confirm('Eliminar C.A.R?')){ cars.splice(i,1); renderCars(); saveJSONToHidden(); } });
      const edit = document.createElement('button'); edit.type='button'; edit.textContent='Editar C.A.R'; edit.className='secondary';
      edit.addEventListener('click', ()=>{ editCarAtIndex(i); });
      btns.appendChild(remove); btns.appendChild(edit);
      div.appendChild(btns);
      carsList.appendChild(div);
    });

    saveJSONToHidden();
  }

  function editCarAtIndex(idx){
    const car = cars[idx];
    if(!car) return;
    const carNameInput = document.getElementById('car_name_input');
    if(carNameInput) carNameInput.value = car.name || car.Nombre_CAR || '';
    // copiar propiedades al draft
    tempProperties.length = 0;
    (car.properties || []).forEach(p => tempProperties.push(JSON.parse(JSON.stringify(p))));
    // quitar el car original (se re-agregará al pulsar Agregar C.A.R)
    cars.splice(idx,1);
    renderTempProps();
    renderCars();
    if(carNameInput) carNameInput.focus();
  }

  function showCarDetails(car){
    let html = '<div style="padding:8px">';
    html += '<h3>' + escapeHtml(car.name) + '</h3>';
    if(!car.properties || car.properties.length === 0) html += '<div class="muted">Sin propiedades</div>';
    else{
      html += '<ul>' + car.properties.map(p=>`<li><strong>${escapeHtml(p.label)}</strong> <span class="prop-meta">(${escapeHtml(p.type)}) ${p.type==='range'?` — ${p.min}..${p.max}`:''}</span></li>`).join('') + '</ul>';
    }
    html += '</div>';
    const win = window.open('','_blank','width=400,height=400');
    win.document.write(html);
  }

  // ---- Property editor behaviour ----
  document.querySelectorAll('.prop-btn').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      editingType = btn.dataset.type;
      openEditor(editingType);
    });
  });

  function openEditor(type){
    if(!propEditor) return;
    propEditor.setAttribute('aria-hidden','false');
    propEditor.style.display = 'block';
    const title = document.getElementById('editorTitle');
    if(title) title.textContent = 'Añadir propiedad — ' + type;
    propLabel.value = '';
    renderTypeSettings(type);
  }

  function closeEditor(){
    if(!propEditor) return;
    propEditor.setAttribute('aria-hidden','true');
    propEditor.style.display='none';
    typeSettings.innerHTML = '';
  }

  cancelPropertyBtn && cancelPropertyBtn.addEventListener('click', closeEditor);

  function renderTypeSettings(type){
    if(!typeSettings) return;
    typeSettings.innerHTML = '';
    if(type === 'range'){
      typeSettings.innerHTML = `
        <label class="field small"><span>Min</span><input id="range_min" type="number" step="any"></label>
        <label class="field small"><span>Max</span><input id="range_max" type="number" step="any"></label>
        <label class="field small"><span>Step</span><input id="range_step" type="number" value="1"></label>
      `;
    } else if(type === 'number'){
      typeSettings.innerHTML = `
        <label class="field small"><span>Min</span><input id="num_min" type="number" step="any"></label>
        <label class="field small"><span>Max</span><input id="num_max" type="number" step="any"></label>
        <label class="field small"><span>Step</span><input id="num_step" type="number" value="1"></label>
      `;
    } else if(type === 'bool'){
      typeSettings.innerHTML = `
        <label class="field small"><span>Valor por defecto</span>
          <select id="bool_default"><option value="true">Ok</option><option value="false">No Ok</option></select>
        </label>
      `;
    } else if(type === 'date'){
      typeSettings.innerHTML = `<label class="field small"><span>Formato (info)</span><div class="muted">YYYY-MM-DD</div></label>`;
    } else {
      typeSettings.innerHTML = `<label class="field small"><span>Placeholder / notas</span><input id="misc_placeholder" type="text"></label>`;
    }
  }

  addPropertyBtn && addPropertyBtn.addEventListener('click', ()=>{
    const label = (propLabel.value || '').trim();
    if(!label){ alert('Dale un nombre a la propiedad'); if(propLabel) propLabel.focus(); return; }
    const prop = { label, type: editingType };
    if(editingType === 'range'){
      prop.min = parseFloat(document.getElementById('range_min').value || 0);
      prop.max = parseFloat(document.getElementById('range_max').value || 100);
      prop.step = parseFloat(document.getElementById('range_step').value || 1);
    } else if(editingType === 'number'){
      prop.min = document.getElementById('num_min').value ? parseFloat(document.getElementById('num_min').value) : null;
      prop.max = document.getElementById('num_max').value ? parseFloat(document.getElementById('num_max').value) : null;
      prop.step = parseFloat(document.getElementById('num_step').value || 1);
    } else if(editingType === 'bool'){
      prop.default = document.getElementById('bool_default').value === 'true';
    } else if(editingType === 'date'){
      // nothing
    } else {
      prop.placeholder = document.getElementById('misc_placeholder').value || '';
    }

    tempProperties.push(prop);
    renderTempProps();
    closeEditor();
  });

  // ---- Add CAR ----
  addCarBtn && addCarBtn.addEventListener('click', ()=>{
    const carName = (document.getElementById('car_name_input') || {}).value.trim();
    if(!carName){ alert('Dale un nombre al C.A.R'); return; }
    if(tempProperties.length === 0){
      if(!confirm('No hay propiedades agregadas al C.A.R. Deseas agregarlo igual?')) return;
    }
    // keep same shape used originally: { name, properties }
    cars.push({ name: carName, properties: JSON.parse(JSON.stringify(tempProperties)) });
    tempProperties.length = 0;
    if(document.getElementById('car_name_input')) document.getElementById('car_name_input').value = '';
    renderTempProps();
    renderCars();
  });

  // ---- Save JSON hidden (usa claves en español: Nombre_Area, Descripcion_Area, Id_Maquila, cars) ----
  function saveJSONToHidden(){
    const name = (document.getElementById('area_name') || {}).value || '';
    const desc = (document.getElementById('area_description') || {}).value || '';
    const maquila = (document.getElementById('maquila_id') || {}).value || '';
    const payload = {
      Nombre_Area: name,
      Descripcion_Area: desc,
      Id_Maquila: maquila,
      cars: cars
    };
    if(areaJsonInput) areaJsonInput.value = JSON.stringify(payload);
    const carCountInput = document.getElementById('car_count_input');
    if(carCountInput) carCountInput.value = Array.isArray(cars) ? cars.length : 0;
  }

  // ---- Submit handler: serializa y marca editar/registrar segun isEditing ----
  areaForm && areaForm.addEventListener('submit', function(e){
    // actualizar hidden JSON y contador
    saveJSONToHidden();

    const editarHidden = document.getElementById('editar_area_input');
    const registrarHidden = document.querySelector('input[name="Registrar_Area"]');
    if(isEditing){
      if(editarHidden) editarHidden.value = '1';
      if(registrarHidden) registrarHidden.value = '0';
    } else {
      if(editarHidden) editarHidden.value = '0';
      if(registrarHidden) registrarHidden.value = '1';
    }
    // allow normal submit
  });

  // ---- expose functions/variables optionally for debug/integration (non-destructive) ----
  window.cars = window.cars || cars;
  window.tempProperties = window.tempProperties || tempProperties;
  window.renderCars = window.renderCars || renderCars;
  window.renderTempProps = window.renderTempProps || renderTempProps;

  // ---- loadAreaForEdit: función separada que solo se ejecuta cuando se llama ----
  // areaData puede ser objeto o JSON string (posiblemente escapado)

    // ----------------------
// Helper: extraer y normalizar C.A.R.s desde cualquier shape
// ----------------------
function extractCarsFromAreaObj(obj) {
  if (!obj) return [];

  if (Array.isArray(obj)) {
    return obj.map(normalizeCarItem).filter(Boolean);
  }

  const candidatesKeys = ['cars','CARS','Cars','CARS_LIST','carsList','car_list','CARs'];
  for (const k of candidatesKeys) {
    if (k in obj && Array.isArray(obj[k])) {
      return obj[k].map(normalizeCarItem).filter(Boolean);
    }
    if (k in obj && typeof obj[k] === 'string') {
      try {
        const parsed = JSON.parse(obj[k]);
        if (Array.isArray(parsed)) return parsed.map(normalizeCarItem).filter(Boolean);
      } catch(e) {}
    }
  }

  if ('JSON_Area' in obj) {
    let nested = obj.JSON_Area;
    if (typeof nested === 'string') {
      try { nested = JSON.parse(nested); } catch(e) {}
    }
    if (Array.isArray(nested)) return nested.map(normalizeCarItem).filter(Boolean);
    if (nested && (nested.cars || nested.CARS || nested.Cars)) {
      const arr = nested.cars ?? nested.CARS ?? nested.Cars;
      if (Array.isArray(arr)) return arr.map(normalizeCarItem).filter(Boolean);
    }
  }

  for (const key of Object.keys(obj)) {
    const val = obj[key];
    if (Array.isArray(val) && val.length > 0 && typeof val[0] === 'object') {
      const looksLikeCar = val.some(item =>
        ('name' in item) || ('Nombre_CAR' in item) ||
        ('properties' in item) || ('props' in item)
      );
      if (looksLikeCar) return val.map(normalizeCarItem).filter(Boolean);
    }
  }

  return [];
}

  function normalizeCarItem(item) {
    if (!item || (typeof item !== 'object' && typeof item !== 'string')) return null;
    if (typeof item === 'string') {
      try { item = JSON.parse(item); } catch(e) { return null; }
    }
    const props = item.properties ?? item.props ?? item.propiedades ?? item.properties_list ?? [];
    const name = item.name ?? item.Nombre_CAR ?? item.nombre ?? item.title ?? item.label ?? item.Nombre ?? '';
    const normalizedProps = Array.isArray(props) ? props.map(p => {
      if (!p || typeof p !== 'object') return p;
      const label = p.label ?? p.nombre ?? p.Label ?? '';
      const type = p.type ?? p.tipo ?? (p.range ? 'range' : '') ?? '';
      const out = Object.assign({}, p);
      out.label = label;
      out.type = type;
      return out;
    }) : [];
    return { name: name, properties: normalizedProps };
  }

// ---------- Inserta esto antes de window.loadAreaForEdit ----------

// Normaliza y devuelve un array de items con la forma { name, properties }
function normalizeIncomingCars(maybe) {
  if (!maybe) return [];
  // si vino string intentar parsear
  if (typeof maybe === 'string') {
    try { maybe = JSON.parse(maybe); } catch(e) {
      try { const t = document.createElement('textarea'); t.innerHTML = maybe; maybe = JSON.parse(t.value); } catch(e2) { return []; }
    }
  }
  // si ya es array
  if (Array.isArray(maybe)) {
    return maybe.map(normalizeOne).filter(Boolean);
  }
  // si es objeto y tiene claves conocidas
  const keys = ['cars','CARS','Cars','carsList','car_list','JSON_Area'];
  for (const k of keys) {
    if (k in maybe) {
      const v = maybe[k];
      if (Array.isArray(v)) return v.map(normalizeOne).filter(Boolean);
      if (typeof v === 'string') {
        try { const parsed = JSON.parse(v); if (Array.isArray(parsed)) return parsed.map(normalizeOne).filter(Boolean); } catch(e) {}
      }
      if (typeof v === 'object' && v !== null) {
        // si JSON_Area fue guardado como objeto con .cars interno
        const inner = v.cars ?? v.CARS ?? v.Cars;
        if (Array.isArray(inner)) return inner.map(normalizeOne).filter(Boolean);
      }
    }
  }
  // heurística: buscar primer array dentro del objeto que parezca lista de cars
  for (const k of Object.keys(maybe)) {
    const val = maybe[k];
    if (Array.isArray(val) && val.length && typeof val[0] === 'object') {
      const looksLikeCar = val.some(it => it && (it.name || it.Nombre_CAR || it.properties || it.props));
      if (looksLikeCar) return val.map(normalizeOne).filter(Boolean);
    }
  }
  return [];
}

function normalizeOne(item) {
  if (!item) return null;
  if (typeof item === 'string') {
    try { item = JSON.parse(item); } catch(e) { return null; }
  }
  const props = item.properties ?? item.props ?? item.propiedades ?? [];
  const name = item.name ?? item.Nombre_CAR ?? item.nombre ?? item.title ?? item.label ?? '';
  const normalizedProps = Array.isArray(props) ? props.map(p => {
    if (!p || typeof p !== 'object') return p;
    // mapear keys comunes
    const label = p.label ?? p.nombre ?? p.Label ?? '';
    const type = p.type ?? p.tipo ?? '';
    const out = Object.assign({}, p);
    out.label = label;
    out.type = type;
    return out;
  }) : [];
  return { name: name, properties: normalizedProps };
}

// Crea un aviso y botón en #carsList para importar los C.A.R.s previos
function showImportExistingNotice(incomingArray) {
  const container = document.getElementById('carsList');
  if (!container) return;
  // evitar duplicados
  if (document.getElementById('import-existing-cars-notice')) return;

  const notice = document.createElement('div');
  notice.id = 'import-existing-cars-notice';
  notice.className = 'import-notice';
  notice.innerHTML = `
    <div style="padding:8px;border:1px dashed #ccc;border-radius:6px;margin-bottom:8px;">
      <strong>Se encontraron ${incomingArray.length} C.A.R.s previos.</strong>
      <div class="muted" style="margin-top:6px;">Haz clic para importarlos al formulario y poder editarlos/eliminarlos.</div>
      <div style="margin-top:8px;">
        <button type="button" id="importExistingCarsBtn">Importar C.A.R.s previos</button>
        <button type="button" id="ignoreExistingCarsBtn" style="margin-left:8px;">Ignorar</button>
      </div>
    </div>
  `;
  // insertar al principio para que sea visible
  container.insertBefore(notice, container.firstChild);

  document.getElementById('importExistingCarsBtn').addEventListener('click', function(){
    // añadir cada uno exactamente como lo haría addCarBtn (misma shape)
    incomingArray.forEach(it => {
      // asegura la misma estructura que tu código usa: { name, properties }
      cars.push({
        name: it.name ?? it.Nombre_CAR ?? it.nombre ?? it.label ?? '',
        properties: Array.isArray(it.properties) ? JSON.parse(JSON.stringify(it.properties)) : (Array.isArray(it.props) ? JSON.parse(JSON.stringify(it.props)) : [])
      });
    });
    // eliminar aviso
    const n = document.getElementById('import-existing-cars-notice');
    if (n && n.parentNode) n.parentNode.removeChild(n);
    // re-render
    renderTempProps && renderTempProps();
    renderCars && renderCars();
    // sincronizar hidden
    if (typeof saveJSONToHidden === 'function') saveJSONToHidden();
  });

  document.getElementById('ignoreExistingCarsBtn').addEventListener('click', function(){
    const n = document.getElementById('import-existing-cars-notice');
    if (n && n.parentNode) n.parentNode.removeChild(n);
  });
}

// ---------- Fin inserción ----------


  window.loadAreaForEdit = function(areaData, idArea){
    try {
      const obj = parseAreaData(areaData);

      // rellenar campos simples
      const nameEl = document.getElementById('area_name');
      const descEl = document.getElementById('area_description');
      const maquilaEl = document.getElementById('maquila_id');
      const areaIdInput = document.getElementById('area_id_input');
      const editarInput = document.getElementById('editar_area_input');

      if(nameEl) nameEl.value = obj.Nombre_Area ?? obj.area_name ?? obj.Nombre_Area ?? obj.Nombre ?? '';
      if(descEl) descEl.value = obj.Descripcion_Area ?? obj.area_description ?? obj.Descripcion_Area ?? obj.Descripcion ?? '';
      if(maquilaEl && (obj.Id_Maquila || obj.Id_Maquila === 0 || obj.Id_Maquila == '0' || obj.maquila_id)){
        maquilaEl.value = String(obj.Id_Maquila ?? obj.maquila_id ?? '');
        maquilaEl.dispatchEvent(new Event('change', { bubbles: true }));
      }
      if(areaIdInput) areaIdInput.value = idArea ?? obj.Id_Area ?? obj.id ?? '';

      // normalizar incoming cars (acepta varias formas)
// nueva: usar helper robusto
      let incoming = extractCarsFromAreaObj(obj);


      // map to internal shape { name, properties }
      cars.length = 0;
      incoming.forEach(it => {
        const props = it.properties ?? it.props ?? it.propiedades ?? [];
        const name = it.name ?? it.Nombre_CAR ?? it.nombre ?? it.title ?? it.label ?? '';
        cars.push({ name: name, properties: Array.isArray(props) ? JSON.parse(JSON.stringify(props)) : [] });
      });

      // clear draft props
      tempProperties.length = 0;
      renderTempProps();
      renderCars();

      // mark editing mode
      isEditing = true;
      if(editarInput) editarInput.value = '1';
      if(submitBtn) submitBtn.textContent = 'Actualizar Area';

      // update hidden JSON and counter
      saveJSONToHidden();

      // scroll to form
      if(areaForm && typeof areaForm.scrollIntoView === 'function') areaForm.scrollIntoView({ behavior: 'smooth' });

      console.log('loadAreaForEdit: formulario rellenado (Id_Area=' + (areaIdInput ? areaIdInput.value : '(sin id)') + ')');
    } catch(err){
      console.error('loadAreaForEdit error:', err);
      alert('No se pudo cargar el área para edición. Revisa la consola.');
    }
  };

  // ---- Opcional: delegate clicks para botones .editarAreaBtn en la tabla (no hace nada hasta que click) ----
  // Esto evita tocar index; solo responde cuando el usuario clickea un botón con esa clase.
  document.addEventListener('click', function(e){
    const btn = e.target.closest('button');
    if(!btn) return;
    if(btn.classList.contains('editarAreaBtn')){
      e.preventDefault();
      const raw = btn.getAttribute('data-area');
      const id = btn.getAttribute('data-id') || null;
      // llamar a la función de edición (no hace nada si no se llama)
      if(typeof window.loadAreaForEdit === 'function') window.loadAreaForEdit(raw, id);
    }
    // keep the original "mostrarCarsBtn" behavior intact
    if(btn.classList.contains('mostrarCarsBtn')){
      e.preventDefault();
      const raw = btn.getAttribute('data-area');
      const parsed = parseAreaData(raw);
      if(window.CarsPopup && typeof window.CarsPopup.showCars === 'function'){
        window.CarsPopup.showCars(parsed);
      } else {
        console.warn('CarsPopup no disponible para mostrar C.A.R.s');
      }
    }
  });

  


  // ---- inicial render ----
  renderTempProps(); renderCars();

})(); // end IIFE
