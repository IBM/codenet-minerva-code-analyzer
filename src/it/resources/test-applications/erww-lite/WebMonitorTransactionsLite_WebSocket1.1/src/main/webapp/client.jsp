<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta content="charset=ISO-8859-1">
	<title>WebMonitorTransactionsLite_WebSocket1.1</title>

	<script>
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
};

function init() {
	openWebSocket();
}

function getContextPath() {
   return "${pageContext.request.contextPath}";
}

function openWebSocket() {
	wsocket = new WebSocket("ws://" + document.location.host + getContextPath() + "/monitor1.1");
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
}

function onClose(event) {
	window.clearInterval(pingTimer);
}

function onMessage(event) {
	var msg = JSON.parse(event.data);
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
}

function setTextContent(element, text) {
    while (element.firstChild!==null)
        element.removeChild(element.firstChild); // remove all existing content
    element.appendChild(document.createTextNode(text));
}
  
window.addEventListener("load", init, false);
	</script>
	
	<style type="text/css">
		BODY
		{
		background-color: #f8f7cd;
		}
		H1 {
			text-align: center !IMPORTANT;
		}
		
		TH {
			text-align: left !IMPORTANT;
			vertical-align: top !IMPORTANT;
		}
		
		TD {
			text-align: left !IMPORTANT;
			vertical-align: top !IMPORTANT;
		}
		
		TH.result {
			background-color: #999999 !IMPORTANT;
		}
		
		TD.result {
			background-color: #cccccc;
			vertical-align: top !IMPORTANT;
		}
	</style>
</head>
	
<body class="claro" bgcolor="#F8F7CD">
	<H1 align="center">Monitor Transactions using WebSocket 1.1</H1>
	
	<TABLE>
		<TR><TD>
			<TABLE border="8" bgcolor="#CCCCCC" bordercolor="#FFCC99">
				<TR><TD><TH>New Order Transaction Sales</TH></TR>
				
				<SCRIPT type="text/javascript">
					for (var i = 0; i < 25; i++) {
						document.write("<tr><td>Warehouse " + (i+1) + ": </td>");
						document.write("<td id=\"neworder_warehouse" + (i+1) + "\">...</td></tr>");
					}
				</SCRIPT>
			</TABLE>
		</TD>
		
		<TD>
			<TABLE border="8" bgcolor="#CCCCCC" bordercolor="#FFCC99">
				<TR><TD><TH>Payment Transaction Sales</TH></TD></TR>
				
				<SCRIPT type="text/javascript">
					for (var i = 0; i < 25; i++) {
						document.write("<tr><td>Warehouse " + (i+1) + ": </td>");
						document.write("<td id=\"payment_warehouse" + (i+1) + "\">...</td></tr>");
					}
				</SCRIPT>
			</TABLE>
		</TD></TR>
	</TABLE>
	
	<H2>Verify Data with Server</H2>
	<INPUT type="button" value="Data Verification" onclick="sendVerificationRequest();"/>
	<TABLE border="8" bgcolor="#CCCCCC" bordercolor="#FFCC99">
		<TR>
			<TH>Result:</TH>
			<TH class="result">
				<div id="verificationResult"></div>
			</TH>
		</TR>
	</TABLE>

</body>
</html>