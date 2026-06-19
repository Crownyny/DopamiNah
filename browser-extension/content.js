const APP_ORIGIN = window.location.origin;
const TYPE = 'DOPAMINAH_SYNC_GOALS';

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'SYNC_FROM_EXTENSION') {
    window.postMessage({
      type: 'DOPAMINAH_SYNC_RESPONSE',
      goals: message.goals || [],
      domainTime: message.domainTime || {},
      source: 'extension'
    }, APP_ORIGIN);
    sendResponse({ success: true });
  }
});

window.addEventListener('message', async (event) => {
  if (event.origin !== APP_ORIGIN) return;
  const d = event.data;
  if (!d || d.source === 'extension') return;

  if (d.type === TYPE) {
    await chrome.runtime.sendMessage({ type: 'SYNC_GOALS', goals: d.goals });
  }

  if (d.type === 'DOPAMINAH_REQUEST_GOALS') {
    const res = await chrome.runtime.sendMessage({ type: 'GET_GOALS' });
    const timeRes = await chrome.runtime.sendMessage({ type: 'GET_DOMAIN_TIME' });
    window.postMessage({
      type: 'DOPAMINAH_SYNC_RESPONSE',
      goals: res.goals || [],
      domainTime: timeRes.domainTime || {},
      source: 'extension'
    }, APP_ORIGIN);
  }
});
