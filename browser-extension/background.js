const ALARM_PERIOD_MINUTES = 0.25;
const STORAGE_KEY_GOALS = 'goals';
const STORAGE_KEY_DOMAIN_TIME = 'domainTime';
const BLOCKED_URL = chrome.runtime.getURL('blocked/blocked.html');

let state = {
  currentDomain: null,
  sessionStartTime: null,
};

chrome.runtime.onInstalled.addListener(async () => {
  await initialize();
  chrome.alarms.create('timeTick', { periodInMinutes: ALARM_PERIOD_MINUTES });
  chrome.alarms.create('dailyReset', { periodInMinutes: 60 });
});

chrome.runtime.onStartup.addListener(async () => {
  await initialize();
  chrome.alarms.create('timeTick', { periodInMinutes: ALARM_PERIOD_MINUTES });
  chrome.alarms.create('dailyReset', { periodInMinutes: 60 });
});

async function initialize() {
  const stored = await chrome.storage.session.get(['currentDomain', 'sessionStartTime']);
  if (stored.currentDomain) state.currentDomain = stored.currentDomain;
  if (stored.sessionStartTime) state.sessionStartTime = stored.sessionStartTime;
  await checkDailyReset();
  await updateBlockRules();
}

chrome.webNavigation.onCommitted.addListener(async (details) => {
  if (details.frameId !== 0) return;
  const domain = extractDomain(details.url);
  if (!domain || domain === state.currentDomain) return;

  await accumulateTime();
  state.currentDomain = domain;
  state.sessionStartTime = Date.now();
  await chrome.storage.session.set({
    currentDomain: domain,
    sessionStartTime: state.sessionStartTime
  });
});

chrome.alarms.onAlarm.addListener(async (alarm) => {
  if (alarm.name === 'timeTick') {
    await accumulateTime();
    await updateBlockRules();
  } else if (alarm.name === 'dailyReset') {
    await checkDailyReset();
  }
});

async function accumulateTime() {
  if (!state.currentDomain || !state.sessionStartTime) return;

  const now = Date.now();
  const elapsedMs = now - state.sessionStartTime;
  const elapsedMinutes = Math.floor(elapsedMs / 60000);
  if (elapsedMinutes < 1) return;

  const data = await chrome.storage.local.get(STORAGE_KEY_DOMAIN_TIME);
  const domainTime = data[STORAGE_KEY_DOMAIN_TIME] || {};
  const today = getTodayDate();
  const domain = state.currentDomain;

  if (!domainTime[domain]) domainTime[domain] = { todayMinutes: 0, date: today };
  if (domainTime[domain].date !== today) {
    domainTime[domain] = { todayMinutes: 0, date: today };
  }

  domainTime[domain].todayMinutes += elapsedMinutes;
  state.sessionStartTime = now;

  await chrome.storage.session.set({ sessionStartTime: now });
  await chrome.storage.local.set({ [STORAGE_KEY_DOMAIN_TIME]: domainTime });
}

async function updateBlockRules() {
  const data = await chrome.storage.local.get([STORAGE_KEY_GOALS, STORAGE_KEY_DOMAIN_TIME]);
  const goals = data[STORAGE_KEY_GOALS] || [];
  const domainTime = data[STORAGE_KEY_DOMAIN_TIME] || {};
  const today = getTodayDate();

  const blockedDomains = [];
  for (const goal of goals) {
    if (!goal.isActive) continue;
    const dt = domainTime[goal.domain];
    const spentMinutes = dt && dt.date === today ? dt.todayMinutes : 0;
    if (goal.timeLimitMinutes === 0 || spentMinutes >= goal.timeLimitMinutes) {
      blockedDomains.push(goal.domain);
    }
  }

  const existingRules = await chrome.declarativeNetRequest.getDynamicRules();
  const existingIds = existingRules.map(r => r.id);
  if (existingIds.length > 0) {
    await chrome.declarativeNetRequest.updateDynamicRules({ removeRuleIds: existingIds });
  }

  if (blockedDomains.length === 0) return;

  const newRules = blockedDomains.map((domain, index) => ({
    id: index + 1,
    priority: 1,
    action: {
      type: 'redirect',
      redirect: { url: `${BLOCKED_URL}?domain=${encodeURIComponent(domain)}` }
    },
    condition: {
      urlFilter: `||${domain}`,
      resourceTypes: ['main_frame']
    }
  }));

  await chrome.declarativeNetRequest.updateDynamicRules({ addRules: newRules });
}

async function unblockDomain(domain) {
  const existingRules = await chrome.declarativeNetRequest.getDynamicRules();
  const toRemove = existingRules
    .filter(r => r.condition.urlFilter === `||${domain}`)
    .map(r => r.id);
  if (toRemove.length > 0) {
    await chrome.declarativeNetRequest.updateDynamicRules({ removeRuleIds: toRemove });
  }

  const data = await chrome.storage.local.get(STORAGE_KEY_GOALS);
  const goals = data[STORAGE_KEY_GOALS] || [];
  const goal = goals.find(g => g.domain === domain);
  if (goal) {
    goal.isActive = false;
    await chrome.storage.local.set({ [STORAGE_KEY_GOALS]: goals });
  }
}

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  switch (message.type) {
    case 'GET_GOALS':
      chrome.storage.local.get(STORAGE_KEY_GOALS).then(data => {
        sendResponse({ goals: data[STORAGE_KEY_GOALS] || [] });
      });
      return true;
    case 'ADD_GOAL':
      handleAddGoal(message.goal, sendResponse);
      return true;
    case 'DELETE_GOAL':
      handleDeleteGoal(message.id, sendResponse);
      return true;
    case 'TOGGLE_GOAL':
      handleToggleGoal(message.id, sendResponse);
      return true;
    case 'GET_DOMAIN_TIME':
      chrome.storage.local.get(STORAGE_KEY_DOMAIN_TIME).then(data => {
        sendResponse({ domainTime: data[STORAGE_KEY_DOMAIN_TIME] || {} });
      });
      return true;
    case 'UNBLOCK_DOMAIN':
      unblockDomain(message.domain).then(() => sendResponse({ success: true }));
      return true;
    case 'SYNC_GOALS':
      handleSyncGoals(message.goals, sendResponse);
      return true;
  }
});

async function handleAddGoal(goalData, sendResponse) {
  const data = await chrome.storage.local.get(STORAGE_KEY_GOALS);
  const goals = data[STORAGE_KEY_GOALS] || [];
  const domain = extractDomain(goalData.url) || goalData.url.toLowerCase();

  if (goals.some(g => g.domain === domain)) {
    sendResponse({ success: false, error: 'Ya existe una meta para este dominio' });
    return;
  }

  const newGoal = {
    id: `goal_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`,
    domain,
    displayUrl: goalData.url,
    timeLimitMinutes: goalData.timeLimitMinutes,
    isActive: true,
    createdAt: Date.now()
  };
  goals.push(newGoal);
  await chrome.storage.local.set({ [STORAGE_KEY_GOALS]: goals });
  await updateBlockRules();
  sendResponse({ success: true, goal: newGoal });
}

async function handleDeleteGoal(id, sendResponse) {
  const data = await chrome.storage.local.get(STORAGE_KEY_GOALS);
  const goals = (data[STORAGE_KEY_GOALS] || []).filter(g => g.id !== id);
  await chrome.storage.local.set({ [STORAGE_KEY_GOALS]: goals });
  await updateBlockRules();
  sendResponse({ success: true });
}

async function handleToggleGoal(id, sendResponse) {
  const data = await chrome.storage.local.get(STORAGE_KEY_GOALS);
  const goals = data[STORAGE_KEY_GOALS] || [];
  const goal = goals.find(g => g.id === id);
  if (goal) {
    goal.isActive = !goal.isActive;
    await chrome.storage.local.set({ [STORAGE_KEY_GOALS]: goals });
    await updateBlockRules();
  }
  sendResponse({ success: true });
}

async function handleSyncGoals(externalGoals, sendResponse) {
  const data = await chrome.storage.local.get(STORAGE_KEY_GOALS);
  const existing = data[STORAGE_KEY_GOALS] || [];
  const goalMap = new Map();
  for (const g of existing) goalMap.set(g.domain, g);
  for (const g of externalGoals) {
    if (!goalMap.has(g.domain)) {
      goalMap.set(g.domain, {
        id: `synced_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`,
        domain: g.domain,
        displayUrl: g.displayUrl || g.domain,
        timeLimitMinutes: g.timeLimitMinutes,
        isActive: g.isActive !== false,
        createdAt: Date.now(),
        synced: true
      });
    }
  }
  await chrome.storage.local.set({ [STORAGE_KEY_GOALS]: Array.from(goalMap.values()) });
  await updateBlockRules();
  sendResponse({ success: true });
}

async function checkDailyReset() {
  const today = getTodayDate();
  const data = await chrome.storage.local.get(STORAGE_KEY_DOMAIN_TIME);
  const domainTime = data[STORAGE_KEY_DOMAIN_TIME] || {};
  let changed = false;
  for (const domain of Object.keys(domainTime)) {
    if (domainTime[domain].date !== today) {
      domainTime[domain] = { todayMinutes: 0, date: today };
      changed = true;
    }
  }
  if (changed) {
    await chrome.storage.local.set({ [STORAGE_KEY_DOMAIN_TIME]: domainTime });
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

function getTodayDate() {
  return new Date().toISOString().split('T')[0];
}
