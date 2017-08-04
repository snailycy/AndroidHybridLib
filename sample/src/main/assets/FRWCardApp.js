(function() {
	if (window.FRWCardApp) {
		return;
	}

	var messageHandlers = {};
	var uniqueId = 1;

	function sendToNative(methodName, params, callBackClosureDict) {
		var callbackClosureId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
		if (callBackClosureDict) {
			messageHandlers[callbackClosureId] = callBackClosureDict;
		};
		CardAppNative.messageSend(methodName,callbackClosureId,JSON.stringify(params));
	}

	function callBackFromNative(callbackClosureId, type, paramsString) {

		if (callbackClosureId) {
			var callBackClosureDict = messageHandlers[callbackClosureId];
			var closure = callBackClosureDict[type];
			delete messageHandlers[callbackClosureId];
			if (closure) {
				var dict = JSON.parse(paramsString);
				closure(dict);
			}
			else{
				CardAppNative.hybrid(callbackClosureId);
			};
		};
	}

	function getLocation(params) {
		var successClosure = params["success"];
		var cancelClosure = params["cancel"];
		var failerClosure = params["failer"];

		sendToNative("getLocation",null,{
			"success":successClosure,
			"cancel":cancelClosure,
			"fail":failerClosure
		});
	}

	var CardApp = window.FRWCardApp = {
		sendToNative: sendToNative,
		callBackFromNative: callBackFromNative,
		getLocation: getLocation,
	};

})();