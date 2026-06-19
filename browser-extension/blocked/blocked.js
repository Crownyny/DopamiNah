const params = new URLSearchParams(window.location.search);
const domain = params.get('domain') || 'desconocido';
const reason = params.get('reason') || 'timeout';
const cardImm = document.getElementById('card-immediate');
const cardTo = document.getElementById('card-timeout');
const btnImm = document.getElementById('unblock-btn-imm');
const btnTo = document.getElementById('unblock-btn-to');
const toast = document.getElementById('toast');
const toastMsg = document.getElementById('toast-message');
const ring = document.getElementById('progress-ring');
const circumference = 2 * Math.PI * 45;

if (reason === 'immediate') {
  document.body.className = 'mode-immediate';
  cardImm.classList.remove('hidden');
  cardTo.classList.add('hidden');
  document.getElementById('domain-text-imm').textContent = domain;
} else {
  document.body.className = 'mode-timeout';
  cardTo.classList.remove('hidden');
  cardImm.classList.add('hidden');
  document.getElementById('domain-text-to').textContent = domain;
  loadStats();
}

async function loadStats() {
  try {
    const [timeRes, goalRes] = await Promise.all([
      chrome.runtime.sendMessage({ type: 'GET_DOMAIN_TIME' }),
      chrome.runtime.sendMessage({ type: 'GET_GOALS' })
    ]);

    const dt = timeRes?.domainTime?.[domain];
    const goal = goalRes?.goals?.find(g => g.domain === domain);
    if (!dt || !goal) return;

    const spent = dt.todayMs !== undefined ? Math.floor(dt.todayMs / 60000) : (dt.todayMinutes || 0);
    const limit = goal.timeLimitMinutes;

    document.getElementById('time-spent').textContent = formatTime(spent);
    document.getElementById('time-limit').textContent = limit === 0 ? 'Ilimitado' : formatTime(limit);
    document.getElementById('limit-text').textContent = limit === 0 ? 'tiempo' : formatTime(limit);

    let ratio = limit > 0 ? Math.min(spent / limit, 1) : 1;
    const offset = circumference * (1 - ratio);
    ring.style.setProperty('--offset-to', offset + 'px');
    ring.style.strokeDashoffset = offset;

    document.getElementById('stats-container').style.display = 'grid';
  } catch (_) {}
}

function formatTime(m) {
  if (m < 60) return `${m} min`;
  const h = Math.floor(m / 60);
  const r = m % 60;
  return r > 0 ? `${h}h ${r}m` : `${h}h`;
}

function setupUnblock(btn) {
  btn.addEventListener('click', async () => {
    btn.disabled = true;
    btn.innerHTML = `
      <svg class="btn-icon" viewBox="0 0 24 24" style="animation:spin 0.8s linear infinite">
        <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" fill="none" stroke-width="2"/>
      </svg>
      <style>@keyframes spin{to{transform:rotate(360deg)}}</style>
      Desbloqueando…`;

    try {
      await chrome.runtime.sendMessage({ type: 'UNBLOCK_DOMAIN', domain });

      toastMsg.textContent = `\u00ab${domain}\u00bb desbloqueado.`;
      toast.classList.add('show');
      setTimeout(() => toast.classList.remove('show'), 3000);

      setTimeout(() => { window.location.href = 'https://' + domain; }, 700);
    } catch (_) {
      btn.disabled = false;
      btn.innerHTML = btn === btnImm
        ? `<svg class="btn-icon" viewBox="0 0 24 24"><rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0"/></svg>\nDesbloquear por hoy`
        : `<svg class="btn-icon" viewBox="0 0 24 24"><rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0"/></svg>\nDesbloquear por hoy`;
      toast.classList.add('toast-red');
      toastMsg.textContent = 'Error al desbloquear. Intenta de nuevo.';
      toast.classList.add('show');
      setTimeout(() => { toast.classList.remove('show'); toast.classList.remove('toast-red'); }, 3000);
    }
  });
}

setupUnblock(btnImm);
setupUnblock(btnTo);
