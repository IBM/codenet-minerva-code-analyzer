/**
 * 
 */
	"use strict";
	
	var xmlHttp = null;
	var customerCreditOutputJSONObject = null;
    
	function initialize(){
		   console.log('In initialize: new XMLHttpRequest() ');   
		   xmlHttp = new XMLHttpRequest(); 
		   getSectionVisible();
	}
	    
    function getCustomerCredit() {
        var warehouseIdGetOperation = document.getElementById("warehouseIdGetOperation");
        var districtIdGetOperation =  document.getElementById("districtIdGetOperation");
        var customerIdGetOperation =  document.getElementById("customerIdGetOperation");
        var url = "/WebCustomerLite_REST_APIs/customer/?warehouseId=" + warehouseIdGetOperation.value + "&districtId=" + districtIdGetOperation.value + "&customerId=" + customerIdGetOperation.value;
        
        xmlHttp.open('GET',url,true);
        xmlHttp.send(null);
        xmlHttp.onreadystatechange = function() {

               if (xmlHttp.readyState == 4) {
                  if ( xmlHttp.status == 200) {                	  
                	
                	   var customerCreditResponse = xmlHttp.response;
                	   console.log('In getCustomerCredit: customerCreditResponse =  ' + customerCreditResponse);
                	   
                	   customerCreditOutputJSONObject = JSON.parse(customerCreditResponse);     	   
                	   
                       console.log('In getCustomerCredit: customerCreditOutputJSONObject.customerWId =  ' + customerCreditOutputJSONObject.customerWId);
                       
               		   $('#getCustomerCreditResponse').html(" ");
               		   $('#getCustomerCreditResponse').append('<p> Warehouse ID    = ' + customerCreditOutputJSONObject.customerWId + '</p>');
               		   $('#getCustomerCreditResponse').append('<p> District ID     = ' + customerCreditOutputJSONObject.customerDId + '</p>');  
               		   $('#getCustomerCreditResponse').append('<p> Customer ID     = ' + customerCreditOutputJSONObject.customerId + '</p>');
               		   $('#getCustomerCreditResponse').append('<p> Customer Name   = ' + customerCreditOutputJSONObject.customerFirst + ' ' + 
               				                                                             customerCreditOutputJSONObject.customerMiddle + ' ' +
               				                                                             customerCreditOutputJSONObject.customerLast + ' ' + '</p>');
               		   $('#getCustomerCreditResponse').append('<p> Customer Credit = ' + customerCreditOutputJSONObject.customerCredit + '</p>');
               		   $('#getCustomerCreditResponse').append('<p> Customer Credit Limit = ' + customerCreditOutputJSONObject.customerCreditLim + '</p>');
					   $('#getCustomerCreditResponse').append('<p> Customer Time = ' + customerCreditOutputJSONObject.customerTime+ '</p>');
               		   
               	 }
                 else{
                	 alert("Error in WebCustomerLite_Rest_Call: customerCredit.js ->" + xmlHttp.status);
                 }
              }
          };
    }
    
    function postCustomerCredit() {
        var warehouseIdPostOperation = document.getElementById("warehouseIdPostOperation");
        var districtIdPostOperation =  document.getElementById("districtIdPostOperation");
        var customerIdPostOperation =  document.getElementById("customerIdPostOperation");
        var customerCreditPostOperation =  document.getElementById("customerCreditPostOperation");
        var customerCreditLimPostOperation =  document.getElementById("customerCreditLimPostOperation");
        
        var url = "/WebCustomerLite_REST_APIs/customer/?warehouseId=" + warehouseIdPostOperation.value + "&districtId=" + districtIdPostOperation.value + "&customerId=" + customerIdPostOperation.value;
         
        var customerCreditJsonObject = {};
        var customerCreditKey = "customerCredit";
        var customerCreditLimitKey = "customerCreditLimit";        
        customerCreditJsonObject[customerCreditKey] = customerCreditPostOperation.value;
        customerCreditJsonObject[customerCreditLimitKey] = customerCreditLimPostOperation.value;
                
        xmlHttp.open('POST',url,true);
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xmlHttp.send(JSON.stringify(customerCreditJsonObject)); 
        xmlHttp.onreadystatechange = function() {

            if (xmlHttp.readyState == 4) {
                if ( xmlHttp.status == 200) {                	  
              	
              	   var customerCreditResponse = xmlHttp.response;
              	   console.log('In getCustomerCredit: customerCreditResponse =  ' + customerCreditResponse);
              	   
              	   customerCreditOutputJSONObject = JSON.parse(customerCreditResponse);     	   
              	   
                     console.log('In getCustomerCredit: customerCreditOutputJSONObject.customerWId =  ' + customerCreditOutputJSONObject.customerWId);
                     
             		   $('#postCustomerCreditResponse').html(" ");
             		   $('#postCustomerCreditResponse').append('<p> Warehouse ID    = ' + customerCreditOutputJSONObject.customerWId + '</p>');
             		   $('#postCustomerCreditResponse').append('<p> District ID     = ' + customerCreditOutputJSONObject.customerDId + '</p>');  
             		   $('#postCustomerCreditResponse').append('<p> Customer ID     = ' + customerCreditOutputJSONObject.customerId + '</p>');
             		   $('#postCustomerCreditResponse').append('<p> Customer Name   = ' + customerCreditOutputJSONObject.customerFirst + ' ' + 
             				                                                             customerCreditOutputJSONObject.customerMiddle + ' ' +
             				                                                             customerCreditOutputJSONObject.customerLast + ' ' + '</p>');
             		   $('#postCustomerCreditResponse').append('<p> Customer Credit = ' + customerCreditOutputJSONObject.customerCredit + '</p>');
             		   $('#postCustomerCreditResponse').append('<p> Customer Credit Limit = ' + customerCreditOutputJSONObject.customerCreditLim + '</p>');
					   $('#postCustomerCreditResponse').append('<p> Customer Time = ' + customerCreditOutputJSONObject.customerTime+ '</p>');
             		   
             	 }
               else{
                	 alert("Error in WebCustomerLite_Rest_Call: customerCredit.js ->" + xmlHttp.status);
               }
            }
        };
    }

	function getSectionVisible() {
		console.log('In getCustomerCredit: get section visible');
		document.getElementById('get').style.display="block";
		document.getElementById('post').style.display="none";
	} 

	function postSectionVisible() {
		console.log('In getCustomerCredit: post section visible');
		document.getElementById('post').style.display="block";
		document.getElementById('get').style.display="none";
	}
