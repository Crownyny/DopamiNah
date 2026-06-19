(function () {
  var TYPE = 'DOPAMINAH_SYNC_GOALS';
  var LS_KEY = 'dopaminah_goals';

  window.__dopaminahPendingSync = null;

  window.addEventListener('message', function (event) {
    var d = event.data;
    if (!d) return;

    if (d.type === TYPE && d.source === 'extension' && Array.isArray(d.goals)) {
      var json = JSON.stringify(d.goals);
      window.__dopaminahPendingSync = json;
      try { window.localStorage.setItem(LS_KEY, json); } catch (_) {}
    }

    if (d.type === 'DOPAMINAH_SYNC_RESPONSE' && d.source === 'extension') {
      var goalsJson = JSON.stringify(d.goals);
      window.__dopaminahPendingSync = goalsJson;
      try {
        window.localStorage.setItem(LS_KEY, goalsJson);
        if (d.domainTime) {
          window.localStorage.setItem('dopaminah_domain_time', JSON.stringify(d.domainTime));
        }
      } catch (_) {}
    }
  });

  window.__dopaminahPostGoals = function (goalsJson) {
    try {
      var goals = JSON.parse(goalsJson);
      window.postMessage({ type: TYPE, goals: goals, source: 'webapp' }, '*');
    } catch (e) {
      console.error('DopamiNah sync post error:', e);
    }
  };

  window.__dopaminahPollSync = function () {
    var p = window.__dopaminahPendingSync;
    if (p) { window.__dopaminahPendingSync = null; }
    return p;
  };

  window.__dopaminahRequestFullSync = function () {
    window.postMessage({ type: 'DOPAMINAH_REQUEST_GOALS', source: 'webapp' }, '*');
  };

  window.__dopaminahLoadCachedGoals = function () {
    try { return window.localStorage.getItem(LS_KEY); } catch (_) { return null; }
  };

  window.__dopaminahSaveGoals = function (json) {
    try { window.localStorage.setItem(LS_KEY, json); } catch (_) {}
  };

  window.__dopaminahParseGoalsJson = function (json) {
    try { window.__dopaminahParsedGoals = JSON.parse(json); } catch (e) { window.__dopaminahParsedGoals = []; }
    return window.__dopaminahParsedGoals.length;
  };

  window.__dopaminahGoalCount = function () {
    return (window.__dopaminahParsedGoals || []).length;
  };

  window.__dopaminahGoalDomain = function (i) {
    var g = (window.__dopaminahParsedGoals || [])[i];
    return g ? (g.domain || '') : null;
  };

  window.__dopaminahGoalDisplayUrl = function (i) {
    var g = (window.__dopaminahParsedGoals || [])[i];
    return g ? (g.displayUrl || g.domain || '') : null;
  };

  window.__dopaminahGoalMinutes = function (i) {
    var g = (window.__dopaminahParsedGoals || [])[i];
    return g ? (g.timeLimitMinutes !== undefined ? g.timeLimitMinutes : 30) : 30;
  };

  window.__dopaminahLoadCachedDomainTime = function () {
    try { return window.localStorage.getItem('dopaminah_domain_time') || ''; } catch (_) { return ''; }
  };

  window.__dopaminahGetDomainTimeFromJson = function (json, domain) {
    try {
      var dt = JSON.parse(json);
      var entry = dt[domain];
      if (!entry) return -1;
      return entry.todayMs !== undefined ? Math.floor(entry.todayMs / 60000) : entry.todayMinutes || 0;
    } catch (_) { return -1; }
  };

  window.__dopaminahDomainTimeCount = function (json) {
    try { return Object.keys(JSON.parse(json)).length; } catch (_) { return 0; }
  };

  window.__dopaminahDomainTimeDomain = function (json, i) {
    try {
      var keys = Object.keys(JSON.parse(json));
      return keys[i] || '';
    } catch (_) { return ''; }
  };

  window.__dopaminahDomainTimeMinutes = function (json, i) {
    try {
      var keys = Object.keys(JSON.parse(json));
      var domain = keys[i];
      var entry = JSON.parse(json)[domain];
      return entry ? (entry.todayMs !== undefined ? Math.floor(entry.todayMs / 60000) : (entry.todayMinutes || 0)) : 0;
    } catch (_) { return 0; }
  };
})();
