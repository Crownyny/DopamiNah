(function () {
  var TYPE = 'DOPAMINAH_SYNC_GOALS';

  // Incoming goals from extension (stored as JSON string)
  window.__dopaminahPendingSync = null;

  window.addEventListener('message', function (event) {
    var d = event.data;
    if (d && d.type === TYPE && d.source === 'extension' && Array.isArray(d.goals)) {
      window.__dopaminahPendingSync = JSON.stringify(d.goals);
    }
  });

  // Called by Kotlin to send goals to extension
  window.__dopaminahPostGoals = function (goalsJson) {
    try {
      var goals = JSON.parse(goalsJson);
      window.postMessage({ type: TYPE, goals: goals, source: 'webapp' }, '*');
    } catch (e) {
      console.error('DopamiNah sync post error:', e);
    }
  };

  // Called by Kotlin to poll for incoming goals
  window.__dopaminahPollSync = function () {
    var p = window.__dopaminahPendingSync;
    if (p) {
      window.__dopaminahPendingSync = null;
    }
    return p;
  };

  // --- Parsing helpers for Kotlin/Wasm interop (constant js() strings only) ---

  // Parse goals JSON and store in a global array; returns count
  window.__dopaminahParseGoalsJson = function (json) {
    try {
      window.__dopaminahParsedGoals = JSON.parse(json);
    } catch (e) {
      window.__dopaminahParsedGoals = [];
    }
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
    return g ? (g.timeLimitMinutes || 30) : 30;
  };
})();
