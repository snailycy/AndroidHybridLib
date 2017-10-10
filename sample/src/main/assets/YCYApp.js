(function() {
	if (window.YCYApp) {
		return;
	}

	var messageHandlers = {};
	var uniqueId = 1;

	//同步调用Native
	function syncSendToNative(methodName, params) {
		var returnValue = CardAppNative.syndMessageSend(methodName,JSON.stringify(params));
		return returnValue;
	}

	//异步调用Native
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
		var gpsType = params["type"];
		var successClosure = params["success"];
		var cancelClosure = params["cancel"];
		var failerClosure = params["failer"];

		sendToNative("getLocation",gpsType,{
			"success":successClosure,
			"cancel":cancelClosure,
			"fail":failerClosure
		});
	}

	function getMemoryCache(cacheKey) {
		return syncSendToNative("getMemoryCache",
			{
				"key":cacheKey
			});
	}

	var CardApp = window.YCYApp = {
		syncSendToNative		: syncSendToNative,
		sendToNative 			: sendToNative,
		callBackFromNative		: callBackFromNative,
		getLocation				: getLocation,
		getMemoryCache			: getMemoryCache
	};

})();
