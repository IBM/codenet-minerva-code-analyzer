/* WebSocket JavaScript Client for ERWW
 * 
 */

var warehouse_sales = new Array();
var neworder_totals = new Array();
for (var i=0; i < 25; i++) {
	warehouse_sales.push({x: i, y: 0, text: "Warehouse " + (i+1).toString()});
	neworder_totals.push({x: i, y: 0, text: "Warehouse " + (i+1).toString()});
}

var message_count = 0;
var wsocket;
var pingTimer;
var pingFunc = function() {
	var json = {};
	json["type"] = "ping";
	
	var msg = JSON.stringify(json);
	wsocket.send(msg);
	/*var byteMsg = new ArrayBuffer(msg.length*2);
	var byteView = new Uint8Array(byteMsg);
	for (var i=0; i<byteMsg.byteLength/2; i++){
		byteView[i] = msg.charCodeAt(i);
	}
	wsocket.send(byteMsg.buffer);*/
};

var dojoPieChart;

function init() {
	openWebSocket();
}

function openWebSocket() {
	var contextPath = "${pageContext.request.contextPath}"
	wsocket = new WebSocket("ws://" + document.location.host + contextPath + "/monitor");
	wsocket.binaryType = "arraybuffer";
	wsocket.onmessage = function (event) { onMessage(event); };
	wsocket.onopen = function (event) { onOpen(event); };
	wsocket.onclose = function (event) { onClose(event); };
	wsocket.onerror = function (event) { onError(event); };
	pingTimer=window.setInterval(function() {
		pingFunc();
	}, 1000*5);
}

function onOpen(event) {
	//console.log('Connection Established!');
}

function onClose(event) {
	window.clearInterval(pingTimer);
	//console.log('Connection Closed!');
}

function onMessage(event) {
	//console.log('Message: ' + event.data);
	
	var msg = JSON.parse(event.data);
	
	//console.log(msg);
	switch (msg.type) {
		case "init":
			messageCaseInit(msg);
			break;
			
		case "payment":
			messageCasePayment(msg);
			break;
			
		case "neworder":
			messageCaseNewOrder(msg);
			break;
		
		case "verify_request":
			messageCaseVerifyRequest(msg);
			break;
			
		case "verify_result":
			messageCaseVerifyResult(msg);
			break;
			
		default:
			break;
	}
	
	//console.log('Event: ' + event);
	
}

function messageCaseInit(msg) {
	message_count = msg.message_count;
	for (var i = 0; i < msg.warehouse_sales.length; i++) {
		// Initialize payment transaction variables and document elements
		warehouse_sales[i]['y'] = msg.warehouse_sales[i];
		var payment_warehouse_div_id = "payment_warehouse" + (i+1);
		var element = document.getElementById(payment_warehouse_div_id);
		var changeText = warehouse_sales[i]['y'].toString().match(/^\d+(?:\.\d{0,2})?/);
		setTextContent(element, changeText);
		
		// Initialize new order transaction variables and document elements
		neworder_totals[i]['y'] = msg.neworders[i];
		var neworder_warehouse_div_id = "neworder_warehouse" + (i+1);
		var element = document.getElementById(neworder_warehouse_div_id);
		var changeText = neworder_totals[i]['y'].toString().match(/^\d+(?:\.\d{0,2})?/);
		setTextContent(element, changeText);
	}
	dojoChartUpdate();
}

function messageCasePayment(msg) {
	message_count = message_count + 1;
	var id = msg.warehouse_id;
	var sale = msg.warehouse_sale;
	warehouse_sales[id-1]['y'] = warehouse_sales[id-1]['y'] + sale;
	var payment_warehouse_div_id = "payment_warehouse" + id;
	var element = document.getElementById(payment_warehouse_div_id)
	var changeText = warehouse_sales[id-1]['y'].toString().match(/^\d+(?:\.\d{0,2})?/);
	setTextContent(element, changeText);
	dojoChartUpdate();
}

function messageCaseNewOrder(msg) {
	message_count = message_count + 1;
	var id = msg.outWarehouseId;
	var price = msg.outTotal;
	neworder_totals[id-1]['y'] = neworder_totals[id-1]['y'] + price;
	var neworder_warehouse_div_id = "neworder_warehouse" + id;
	var element = document.getElementById(neworder_warehouse_div_id);
	var changeText = neworder_totals[id-1]['y'].toString().match(/^\d+(?:\.\d{0,2})?/);
	setTextContent(element, changeText);
	dojoChartUpdate();
}

function messageCaseVerifyRequest(msg) {
	var json = {};
	json["type"] = "verify_data";
	
	var array_warehouse_sales = new Array();
	var array_neworder_totals = new Array();

	for (var i=0; i < warehouse_sales.length; i++) {
		array_warehouse_sales.push(warehouse_sales[i]['y']);
	}
	
	for (var i=0; i < neworder_totals.length; i++) {
		array_neworder_totals.push(neworder_totals[i]['y']);
	}
	//console.log("warehouse_sales : " + array_warehouse_sales.toString());
	//console.log("neworder_totals : " + array_neworder_totals.toString());
	
	json["warehouse_sales"] = array_warehouse_sales;
	json["neworders"] = array_neworder_totals;
	json["message_count"] = message_count;
	
	// Sending as a "Text" message
	wsocket.send(JSON.stringify(json));
}

function messageCaseVerifyResult(msg) {
	var result = msg.result;
	var element = document.getElementById("verificationResult");
	var changeText = result;
	setTextContent(element, changeText);
}

function onError(event) {
	alert(event.data);
}

function send(msg) {
	wsocket.send(msg);
}

function sendVerificationRequest() {
	var json = {};
	json["type"] = "verify_begin";
	
	var msg = JSON.stringify(json);
	wsocket.send(msg);
	/*var byteMsg = new ArrayBuffer(msg.length*2);
	var byteView = new Uint8Array(byteMsg);
	for (var i=0; i<byteMsg.byteLength/2; i++){
		byteView[i] = msg.charCodeAt(i);
	}
	wsocket.send(byteView.buffer);*/
}

function setTextContent(element, text) {
    while (element.firstChild!==null)
        element.removeChild(element.firstChild); // remove all existing content
    element.appendChild(document.createTextNode(text));
}

function dojoChartUpdate(){
    pieChart.updateSeries("Payment", warehouse_sales).render();
    lineChart.updateSeries("Warehouse Sales", warehouse_sales).render();
};
  
window.addEventListener("load", init, false);