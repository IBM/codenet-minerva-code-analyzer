// Depricated after JMS messaging was integrated into WebSockets
var warehouse_sales = new Array();
var wsocket;
var pingTimer;
var pingFunc = function() {
	var json = {};
	json["type"] = "ping";
	wsocket.send(JSON.stringify(json));
};
var fakeTransaction = function() {
	var json = {};
	json["type"] = "neworder";
	json["outWarehouseId"] = Math.floor(Math.random() * 24 + 1);
	json["outDistrictId"] = 0;
	json["outCustomerId"] = 0;
	json["outOrderId"] = 0;
	json["outOrderItemCount"] = 0;
	json["outCustomerLastName"] = "";
	json["outCustomerCredit"] = 0;
	json["outWarehouseTax"] = 0;
	json["outTotal"] = Math.random() * 3000;
	json["outMsg"] = 0;
	json["outOrderEntryDate"] = 0;
	json["outItemList"] = 0;
	wsocket.send(JSON.stringify(json));
};

function init() {
	openWebSocket();
}

function openWebSocket() {
	wsocket = new WebSocket("ws://" + document.location.host + "/WebMonitorTransactionsLite_WebSockets/monitor");
	wsocket.onmessage = function (event) { onMessage(event); };
	wsocket.onopen = function (event) { onOpen(event); };
	wsocket.onclose = function (event) { onClose(event); };
	wsocket.onerror = function (event) { onError(event); };
	pingTimer=setInterval(function() {
		pingFunc();
	}, 1000*5);
	fakeTransactionTimer=setInterval(function() {
		fakeTransaction();
	}, 1000*3);
}

function onOpen(event) {
	console.log('Connection Established!');
}

function onClose(event) {
	closeInterval(pingTimer);
	console.log('Connection Closed!');
}

function onMessage(event) {
	console.log('Message: ' + event.data);
	console.log('Event: ' + event);
}

function onError(event) {
	alert(event.data);
}

function send(msg) {
	wsocket.send(msg);
}

window.addEventListener("load", init, false);