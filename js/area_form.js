(function(){
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

  let editingType = null;
  let tempProperties = []; // properties added to the CAR being built
  const cars = []; // list of CAR objects

  // Handle clicking on prop-type buttons
  document.querySelectorAll('.prop-btn').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      editingType = btn.dataset.type;
      openEditor(editingType);
    })
  })

  function openEditor(type){
    propEditor.setAttribute('aria-hidden','false');
    propEditor.style.display='block';
    document.getElementById('editorTitle').textContent = 'Añadir propiedad — ' + type;
    propLabel.value = '';
    renderTypeSettings(type);
  }

  function closeEditor(){
    propEditor.setAttribute('aria-hidden','true');
    propEditor.style.display='none';
    typeSettings.innerHTML='';
  }

  cancelPropertyBtn.addEventListener('click', closeEditor);

  function renderTypeSettings(type){
    typeSettings.innerHTML='';
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
      typeSettings.innerHTML = `
        <label class="field small"><span>Formato (info)</span><div class="muted">YYYY-MM-DD</div></label>
      `;
    } else {
      typeSettings.innerHTML = `
        <label class="field small"><span>Placeholder / notas</span><input id="misc_placeholder" type="text"></label>
      `;
    }
  }

  addPropertyBtn.addEventListener('click', ()=>{
    const label = propLabel.value.trim();
    if(!label){ alert('Dale un nombre a la propiedad'); propLabel.focus(); return; }
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
      // no extra
    } else {
      prop.placeholder = document.getElementById('misc_placeholder').value || '';
    }

    tempProperties.push(prop);
    renderTempProps();
    closeEditor();
  })

  function renderTempProps(){
    if(tempProperties.length === 0){
      currentPropsContainer.innerHTML = '<p class="muted">No hay propiedades agregadas (selecciona un tipo con los botones).</p>';
      return;
    }
    const ul = document.createElement('ul'); ul.className='prop-list';
    tempProperties.forEach((p, idx)=>{
      const li = document.createElement('li');
      li.innerHTML = `<strong>${escapeHtml(p.label)}</strong> <div class="prop-meta">tipo: ${p.type}` + (p.type==='range' ? ` — ${p.min}..${p.max} step ${p.step}` : '') + `</div>`;
      const rm = document.createElement('button'); rm.type='button'; rm.textContent='Eliminar'; rm.style.marginLeft='8px'; rm.addEventListener('click', ()=>{ tempProperties.splice(idx,1); renderTempProps(); });
      li.appendChild(rm);
      ul.appendChild(li);
    })
    currentPropsContainer.innerHTML='';
    currentPropsContainer.appendChild(ul);
  }

  addCarBtn.addEventListener('click', ()=>{
    const carName = document.getElementById('car_name_input').value.trim();
    if(!carName){ alert('Dale un nombre al C.A.R'); return; }
    if(tempProperties.length === 0){ if(!confirm('No hay propiedades agregadas al C.A.R. Deseas agregarlo igual?')) return; }

    const car = { name: carName, properties: JSON.parse(JSON.stringify(tempProperties)) };
    cars.push(car);
    tempProperties = [];
    document.getElementById('car_name_input').value='';
    renderTempProps();
    renderCars();
  })

  function renderCars(){
    carsList.innerHTML='';
    if(cars.length===0){ carsList.innerHTML='<div class="muted">No hay C.A.R agregados.</div>'; return; }
    cars.forEach((c, i)=>{
      const div = document.createElement('div'); div.className='car-item';
      div.innerHTML = `<b>${escapeHtml(c.name)}</b><div class="muted">Propiedades: ${c.properties.map(p=>escapeHtml(p.label)).join(', ')}</div>`;
      const btns = document.createElement('div'); btns.style.marginTop='6px';
      const remove = document.createElement('button'); remove.type='button'; remove.textContent='Eliminar C.A.R'; remove.style.marginRight='6px'; remove.addEventListener('click', ()=>{ if(confirm('Eliminar C.A.R?')){ cars.splice(i,1); renderCars(); } });
      const edit = document.createElement('button'); edit.type='button'; edit.textContent='Ver detalles'; edit.className='secondary'; edit.addEventListener('click', ()=>{ showCarDetails(c); });
      btns.appendChild(remove); btns.appendChild(edit);
      div.appendChild(btns);
      carsList.appendChild(div);
    })
  }

  function showCarDetails(car){
    let html = '<div style="padding:8px">';
    html += '<h3>' + escapeHtml(car.name) + '</h3>';
    if(car.properties.length===0) html += '<div class="muted">Sin propiedades</div>';
    else{
      html += '<ul>' + car.properties.map(p=>`<li><strong>${escapeHtml(p.label)}</strong> <span class="prop-meta">(${p.type}) ${p.type==='range'?` — ${p.min}..${p.max}`:''}</span></li>`).join('') + '</ul>';
    }
    html += '</div>';
    // simple modal-ish (alert with html not great), we'll open a new small window
    const win = window.open('','_blank','width=400,height=400');
    win.document.write(html);
  }

  areaForm.addEventListener('submit', (e)=>{
    // build JSON and put in hidden input
    const data = {
      area_name: document.getElementById('area_name').value.trim(),
      area_description: document.getElementById('area_description').value.trim(),
      maquila_id: document.getElementById('maquila_id').value,
      cars: cars
    };
    areaJsonInput.value = JSON.stringify(data);
    document.getElementById('car_count_input').value = cars.length;
    // allow submit to server; server will receive area_json in POST
    // for debugging you can uncomment:
    // e.preventDefault(); console.log(data); alert('JSON preparado en hidden input. Mira console.');
  })

  function escapeHtml(str){ return String(str).replace(/[&<>"']/g, function(m){ return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]; }); }

  // initial render
  renderTempProps(); renderCars();
})();