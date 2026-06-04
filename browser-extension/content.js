// Content script for DopamiNah web app integration
// Injected into the DopamiNah web app page to enable two-way sync

const APP_ORIGIN = window.location.origin;

// Listen for sync requests from the extension popup
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'SYNC_FROM_EXTENSION') {
    // Forward goals to the DopamiNah web app via postMessage
    window.postMessage({
      type: 'DOPAMINAH_SYNC_GOALS',
      goals: message.goals,
      source: 'extension'
    }, APP_ORIGIN);
    sendResponse({ success: true });
  }
});

// Listen for sync requests from the DopamiNah web app
window.addEventListener('message', async (event) => {
  if (event.origin !== APP_ORIGIN) return;
  if (event.data?.type !== 'DOPAMINAH_SYNC_GOALS') return;
  if (event.data?.source === 'extension') return; // avoid echo

  // Forward to extension background
  await chrome.runtime.sendMessage({
    type: 'SYNC_GOALS',
    goals: event.data.goals
  });
});

console.log('DopamiNah extension: content script loaded');
