(function() {
	if (window.FRWCardApp) {
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

	function getQrCode(params) {
		var needResult = params["needResult"];// 默认为0，扫描结果由客户端处理，1则直接返回扫描结果
		var successClosure = params["success"];
		var cancelClosure = params["cancel"];
		var failerClosure = params["failer"];

		sendToNative("getQrCode",
			{},
			{
				"needResult":needResult || 0,
				"success":successClosure,
				"cancel":cancelClosure,
				"fail":failerClosure
			});
	}


	function chooseImage(params) {
        /*
         {
         count: 1, // 默认9
         sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
         sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
         success: function (res) {
         var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
         }
         "cancel":function(res) {
         //用户取消
         }

         }
         */
		var successClosure = params["success"];
		var cancelClosure = params["cancel"];
		var failerClosure = params["failer"];

		sendToNative("chooseImage",
			{
				count:1,
				sizeType:['original', 'compressed'],
				sourceType: ['album']
			},
			{
				"success":successClosure,
				"cancel":cancelClosure,
				"fail":failerClosure
			});
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
	function getPageImageDetailList(params) {
		var successClosure = params["success"];
		sendToNative("getPageImageDetailList",null,{
			"success":successClosure
		});
	}

	function share(params) {
		var icon = params["icon"];
		var title = params["title"];
		var desc = params["desc"];
		var link = params["link"];
		var promotionCustomerId = params["promotionCustomerId"];

		var successClosure = params["success"];
		var cancelClosure = params["cancel"];
		var failerClosure = params["failer"];
		sendToNative("share",
			{
				icon: icon,
				title: title,
				desc: desc,
				link: link,
				promotionCustomerId: promotionCustomerId
			},
			{
				"success":successClosure,
				"cancel":cancelClosure,
				"fail":failerClosure
			});
	}

	function getNetworkType(params) {
		var successClosure = params["success"];
		var cancelClosure = params["cancel"];
		var failerClosure = params["failer"];
		sendToNative("getNetworkType",null,
			{
				"success":successClosure,
				"cancel":cancelClosure,
				"fail":failerClosure
			});
	}

	function payByWechat(params, callbacks) {
		sendToNative("payByWechat",
			{
				appId: params.appId,
				partnerId: params.partnerId,
				prePayId: params.prePayId,
				packageStr: params.package,
				nonceStr: params.nonceStr,
				timeStamp: params.timeStamp,
				paySign: params.paySign
			},
			{
				"success": callbacks.success,
				"cancel": callbacks.cancel,
				"fail": callbacks.failer
			}
			
		);
	}



	// Cache 相关
	function setDiskCache(htmlPath,dataKey,dataValue) {
		sendToNative("setDiskCache",
			{
				"html":htmlPath,
				"key":cacheKey,
				"value":dataValue
			});
	}
	function getDiskCache(htmlPath,dataKey) {
		return syncSendToNative("getDiskCache",
			{
				"html":htmlPath,
				"key":cacheKey
			});
	}
	function setMemoryCache(cacheKey, cacheObj) {
		sendToNative("setMemoryCache",
			{
				"key":cacheKey,
				"value":cacheObj
			});
	}
	function getMemoryCache(cacheKey) {
		return syncSendToNative("getMemoryCache",
			{
				"key":cacheKey
			});
	}

	var CardApp = window.FRWCardApp = {
		syncSendToNative		: syncSendToNative,
		sendToNative 			: sendToNative,
		callBackFromNative		: callBackFromNative,
		chooseImage				: chooseImage,
		getQrCode				: getQrCode,
		getLocation				: getLocation,
		getPageImageDetailList 	: getPageImageDetailList,
		share					: share,
		getNetworkType			: getNetworkType,
		payByWechat				: payByWechat,
		setDiskCache			: setDiskCache,
		getDiskCache			: getDiskCache,
		setMemoryCache			: setMemoryCache,
		getMemoryCache			: getMemoryCache
	};

})();
