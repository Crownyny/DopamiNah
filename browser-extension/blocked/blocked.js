const params = new URLSearchParams(window.location.search);
const domain = params.get('domain') || 'desconocido';
document.getElementById('domain-name').textContent = domain;

document.getElementById('unblock-btn').addEventListener('click', async () => {
  await chrome.runtime.sendMessage({ type: 'UNBLOCK_DOMAIN', domain });
  alert(`"${domain}" ha sido desbloqueado. La meta ha sido desactivada.`);
  window.location.href = 'https://' + domain;
});
