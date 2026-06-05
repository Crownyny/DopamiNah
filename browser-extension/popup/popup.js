let selectedMinutes = 0;
const MAX_RETRIES = 3;

document.addEventListener('DOMContentLoaded', async () => {
  await loadGoalsWithRetry();

  document.getElementById('add-goal-btn').addEventListener('click', () => {
    const form = document.getElementById('add-goal-form');
    form.classList.toggle('hidden');
    if (!form.classList.contains('hidden')) {
      document.getElementById('goal-url').focus();
    }
  });

  document.getElementById('cancel-goal-btn').addEventListener('click', hideForm);

  document.getElementById('save-goal-btn').addEventListener('click', saveGoal);

  document.querySelectorAll('.preset-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      selectedMinutes = parseInt(btn.dataset.minutes);
    });
  });

  document.getElementById('custom-minutes').addEventListener('input', function () {
    if (this.value) {
      document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
      selectedMinutes = parseInt(this.value);
    }
  });

  document.getElementById('sync-btn').addEventListener('click', syncWithWebApp);
});

async function sendMessageWithRetry(msg, retries = MAX_RETRIES) {
  for (let i = 0; i < retries; i++) {
    try {
      return await chrome.runtime.sendMessage(msg);
    } catch (e) {
      if (i === retries - 1) throw e;
      await new Promise(r => setTimeout(r, 300 * (i + 1)));
    }
  }
}

async function loadGoalsWithRetry() {
  try {
    await loadGoals();
  } catch {
    setTimeout(loadGoalsWithRetry, 500);
  }
}

function extractDomain(url) {
  try {
    const u = new URL(url);
    return u.hostname.replace(/^www\./, '').toLowerCase();
  } catch {
    return url.replace(/^www\./, '').toLowerCase();
  }
}

async function updateCurrentTabInfo(goals, domainTime) {
  try {
    const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
    if (!tabs || tabs.length === 0) return;
    const activeTab = tabs[0];
    if (!activeTab.url) return;

    const domain = extractDomain(activeTab.url);
    if (!domain || activeTab.url.startsWith('chrome://') || activeTab.url.startsWith('about:') || activeTab.url.startsWith('chrome-extension://')) {
      document.getElementById('current-tab-section').classList.add('hidden');
      return;
    }

    document.getElementById('current-tab-section').classList.remove('hidden');
    document.getElementById('current-domain').textContent = domain;

    const today = new Date().toISOString().split('T')[0];
    const dt = domainTime[domain];
    const spentMinutes = dt && dt.date === today ? dt.todayMinutes : 0;

    document.getElementById('current-time-spent').textContent = `${spentMinutes} min`;

    const goal = goals.find(g => g.domain === domain);
    const statusEl = document.getElementById('current-status');
    const actionBtn = document.getElementById('quick-action-btn');

    // Clean previous listeners by replacing button with clone
    const newActionBtn = actionBtn.cloneNode(true);
    actionBtn.parentNode.replaceChild(newActionBtn, actionBtn);

    if (goal) {
      const isBlocked = goal.timeLimitMinutes === 0 || (goal.isActive && spentMinutes >= goal.timeLimitMinutes);
      if (goal.isActive) {
        statusEl.textContent = isBlocked ? 'Límite excedido' : 'Con límite activo';
        statusEl.style.color = isBlocked ? 'var(--danger)' : 'var(--success)';
      } else {
        statusEl.textContent = 'Límite inactivo';
        statusEl.style.color = 'var(--text-secondary)';
      }

      newActionBtn.textContent = goal.isActive ? 'Desactivar Límite' : 'Activar Límite';
      newActionBtn.className = 'btn btn-secondary btn-small';
      newActionBtn.addEventListener('click', async () => {
        await sendMessageWithRetry({ type: 'TOGGLE_GOAL', id: goal.id });
        await loadGoalsWithRetry();
      });
    } else {
      statusEl.textContent = 'Sin límite';
      statusEl.style.color = 'var(--text-secondary)';
      newActionBtn.textContent = 'Añadir Límite';
      newActionBtn.className = 'btn btn-primary btn-small';
      newActionBtn.addEventListener('click', () => {
        document.getElementById('add-goal-form').classList.remove('hidden');
        document.getElementById('goal-url').value = domain;
        document.getElementById('goal-url').focus();
      });
    }
  } catch (e) {
    console.error('Error updating current tab info:', e);
  }
}

async function loadGoals() {
  const response = await sendMessageWithRetry({ type: 'GET_GOALS' });
  const timeResponse = await sendMessageWithRetry({ type: 'GET_DOMAIN_TIME' });
  const goals = response.goals || [];
  const domainTime = timeResponse.domainTime || {};
  const today = new Date().toISOString().split('T')[0];

  // Update active tab panel state
  await updateCurrentTabInfo(goals, domainTime);

  const list = document.getElementById('goals-list');
  const empty = document.getElementById('empty-state');

  list.innerHTML = '';

  if (goals.length === 0) {
    empty.classList.remove('hidden');
    return;
  }

  empty.classList.add('hidden');

  for (const goal of goals) {
    const dt = domainTime[goal.domain];
    const spentMinutes = dt && dt.date === today ? dt.todayMinutes : 0;
    const progress = goal.timeLimitMinutes > 0 ? (spentMinutes / goal.timeLimitMinutes) : 0;
    const isBlocked = goal.timeLimitMinutes === 0 || (goal.isActive && spentMinutes >= goal.timeLimitMinutes);
    const remaining = goal.timeLimitMinutes > 0 ? Math.max(0, goal.timeLimitMinutes - spentMinutes) : 0;

    const card = document.createElement('div');
    card.className = `goal-card card${isBlocked && goal.isActive ? ' blocked' : ''}`;

    const progressClass = isBlocked ? 'danger' : progress > 0.85 ? 'warning' : '';

    card.innerHTML = `
      <div class="goal-header">
        <span class="goal-domain">${goal.domain}</span>
        <span class="goal-status ${goal.isActive ? (isBlocked ? 'blocked-status' : 'active') : 'inactive'}">
          ${goal.isActive ? (isBlocked ? 'Bloqueado' : 'Activo') : 'Inactivo'}
        </span>
      </div>
      <div class="goal-time">
        ${goal.timeLimitMinutes === 0
          ? 'Bloqueo inmediato — sin tiempo'
          : `${spentMinutes} min de ${goal.timeLimitMinutes} min (${remaining} min restantes)`}
      </div>
      <div class="progress-bar">
        <div class="progress-fill ${progressClass}" style="width: ${Math.min(progress * 100, 100)}%"></div>
      </div>
      <div class="goal-actions">
        <button class="btn btn-secondary btn-small toggle-btn" data-id="${goal.id}">${goal.isActive ? 'Desactivar' : 'Activar'}</button>
        <button class="btn btn-danger-ghost btn-small delete-btn" data-id="${goal.id}">Eliminar</button>
      </div>
    `;

    list.appendChild(card);
  }

  document.querySelectorAll('.toggle-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
      await sendMessageWithRetry({ type: 'TOGGLE_GOAL', id: btn.dataset.id });
      await loadGoalsWithRetry();
    });
  });

  document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
      await sendMessageWithRetry({ type: 'DELETE_GOAL', id: btn.dataset.id });
      await loadGoalsWithRetry();
    });
  });
}

async function saveGoal() {
  const urlInput = document.getElementById('goal-url');
  const url = urlInput.value.trim();
  if (!url) {
    urlInput.focus();
    return;
  }

  const response = await sendMessageWithRetry({
    type: 'ADD_GOAL',
    goal: { url, timeLimitMinutes: selectedMinutes }
  });

  if (response.success) {
    urlInput.value = '';
    selectedMinutes = 0;
    document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
    document.querySelector('[data-minutes="0"]').classList.add('active');
    hideForm();
    await loadGoalsWithRetry();
  } else {
    alert(response.error || 'Error al crear la meta');
  }
}

function hideForm() {
  document.getElementById('add-goal-form').classList.add('hidden');
}

async function syncWithWebApp() {
  const response = await sendMessageWithRetry({ type: 'GET_GOALS' });
  const goals = response.goals || [];

  if (goals.length === 0) {
    alert('No hay metas para sincronizar. Crea algunas primero.');
    return;
  }

  try {
    const tabs = await chrome.tabs.query({ url: '*://*/*' });
    const dopaminahTab = tabs.find(t =>
      t.url && (t.url.includes('dopaminah') || t.url.includes('localhost'))
    );

    if (dopaminahTab) {
      await chrome.tabs.sendMessage(dopaminahTab.id, {
        type: 'SYNC_FROM_EXTENSION',
        goals: goals.map(g => ({
          domain: g.domain,
          displayUrl: g.displayUrl,
          timeLimitMinutes: g.timeLimitMinutes,
          isActive: g.isActive
        }))
      });
      alert('Metas sincronizadas con DopamiNah web');
    } else {
      alert('No se encontró la pestaña de DopamiNah web. Ábrela e intenta de nuevo.');
    }
  } catch {
    alert('Error al sincronizar. Asegúrate de tener DopamiNah abierto.');
  }
}
