  
	"use strict";
	
   /*************************************************
   * Define Global Variables
   *************************************************/

	var rtcommEndpointProvider = new rtcomm.EndpointProvider();  // Define the RtcommEndpointProvider
	
	rtcommEndpointProvider.on('presence_updated', function(event_object) {
	    console.log('onPresenceUpdated where event_object = ',event_object);
	    updatePresence(event_object.presenceData[0].flatten());
	});
	
    // Change the loglevel - default is 'INFO', possibilities are:
    // 'MESSAGES' --> Just log sent/received messages in console.
    // 'DEBUG' -- > log most everything
    // 'TRACE' --> log everything
    rtcommEndpointProvider.setLogLevel('DEBUG');
    var messageServerHostVariable = "";
    var messageServerPortVariable = null; 
    var rtcommTopicPathVariable = ""; 
    var rtcommEndpoint = null;  
 
    // UI State
    var registered = false;
    var connected = false;
    var endpointFlag = 'null';
    var currentScrollPosition = 0;
    
	window.onload = function() {
		console.log('ONLOAD! ' + regOnload + ' userid: ' + userid);
		
		jQuery.get( "/WebConfigurationVariablesLite_REST_APIs/configurationVariable", function(response) { 			
			 for (var i = 0, len = response.length; i < len; ++i) {
			     var configurationVariables = response[i];    
			     	messageServerHostVariable =	configurationVariables.mqttHost;	     
				    console.log('configurationVariables.mqttHost = ' + configurationVariables.mqttHost);
				    
				    messageServerPortVariable =	configurationVariables.mqttPort;
				    console.log('configurationVariables.mqttPort = ' + configurationVariables.mqttPort);
				    
				    rtcommTopicPathVariable = configurationVariables.rtcommTopicPath;
				    console.log('configurationVariables.rtcommTopicPath = ' + configurationVariables.rtcommTopicPath);			     
			 }	
		})
		
		$('#displayNameString1').html('<span class="black"> Welcome! Please Register</span>');
		if (regOnload && userid) {
			console.log('Calling doRegister!');
			doRegister(userid);
		}
	}

    // Define the configuration for the rtcommEndpointProvider.  
    // It MUST match the rtcomm configuration used on the liberty server in server.xml.  
    var epConfig = {
	  server: messageServerHostVariable,
	  port: messageServerPortVariable,
      managementTopicName : "management",
      appContext: "default",
      rtcommTopicPath: ("/" + rtcommTopicPathVariable + "/"),
      createEndpoint : true,
      monitorPresence: true,
      presence: {topic: 'erwwLiteRoom1'} 
    };

    var urlConfig = getUrlParams();
    console.log('** CustomerCareInputForm.html ** Config passed in via URL:', urlConfig);
  
    // Pull information from the urlConfig
    var regOnload = (urlConfig.regOnload === 'true')? true : false; // Immediately register onLoad with userid passed. 
    var userid = urlConfig.userid || null;
    var autoAnswer = (urlConfig.autoAnswer === 'true') ? true : false; // Inbound call will be automatically answered.  
    var trickleICE = (urlConfig.trickleICE === 'false') ? false : true;
    var BROADCAST = (urlConfig.broadcast === 'false') ? false : true;   
    
    rtcommEndpointProvider.on('reset', function(event_object) {
        updateMessage("Connection was reset - reason: " + event_object.reason);
    });  
  
	/*****************************************************************
	 * Rtcomm Functions
	 *****************************************************************/

	// Assign the callbacks.  Since JavaScript is 100% asynchronous, callback events are used to handle results of method execution.
	// This happens prior to the doRegister above and defines the default callbacks to use for all RtcommEndpoints created by the rtcommEndpointProvider.
	rtcommEndpointProvider.setRtcommEndpointConfig({
				broadcast : { 
					audio : BROADCAST,
					video : BROADCAST
				},				
				ringbacktone : 'resources/ringbacktone.wav', // Played when outbound call occurs.				
				ringtone : 'resources/ringtone.wav', // Played when inbound call occurs.
				
				'webrtc:connected' : function(event_object) { // Fired when WebRTC is connected. 
					uiConnect("Connected to " + event_object.endpoint.getRemoteEndpointID());
				},
				'webrtc:disconnected' : function(event_object) { // Fired when WebRTC is disconnected. 					
					document.querySelector('#remoteView').src = undefined; // Reset the UI.
					uiDisconnect("Disconnected from " + event_object.endpoint.getRemoteEndpointID());
					try {
						event_object.endpoint.disconnect();
					} catch (e) {
						console.error(e);
					}
				},	
				'webrtc:failed' : function(event_object) { // Fired when WebRTC failed. 					
					alertMessage('WebRTC failed - reason: ' + event_object.reason);
				},	
			    'chat:message': function(event_object) {  // A chat message was received from the other person. 
             		var chat = {
			  				time : (new Date().toLocaleString()),
			  				name : event_object.message.from,
			  				message : event_object.message.message
				  	};	
             		$('#chatContainer').append('<p><b>' + chat.name + ': </b><small> ' + chat.time + '</small></p>');
	                $('#chatContainer').append('<p>' + chat.message + '</p>');
	                $('#chatContainer').append('<hr align="left">'); 
	                
	                console.log('In chat:message: scroll to bottom; number of px from the top'); 
	                $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
	                currentScrollPosition = $("#chatContainer").scrollTop();
	                console.log('In chat:message: after currentScrollPosition: ' + currentScrollPosition); 
	            },
				'chat:connected' : function(event_object) { // Fired when Chat is connected. 
					uiConnect("Connected to " + event_object.endpoint.getRemoteEndpointID());
				},
				'chat:disconnected' : function(event_object) { // Fired when Chat is disconnected. 					
					uiDisconnect("Disconnected from " + event_object.endpoint.getRemoteEndpointID());
					try {
						event_object.endpoint.disconnect();
					} catch (e) {
						console.error(e);
					}
				},
			    'session:ringing': function(event_object) { // An outbound call is starting and the target user has been reached.
				    // This should not be necessary, we should always be the caller for ringing.
				    if (event_object.object && event_object.object.pcSigState) {
				       if (event_object.object.pcSigState === 'have-local-offer') {
				    	   // We are the CALLER.
				           // This could be tracked through the UI too.
				           updateMessage('Calling ' + event_object.object.remoteID);
				       } else {
				           updateMessage('Inbound call from  ' + event_object.endpoint.getRemoteEndpointID());
				       }
				    }
				},					
				'session:alerting' : function(event_object) { // An inbound call was received.
					console.log('** CustomerCare ** Alerting event_object: ', event_object);
					if (!autoAnswer) { 	// Use the global value to autoAnswer if configured.
						$('#answerDialog .modal-body').html("Inbound call from " + event_object.endpoint.getRemoteEndpointID());
						$('#answerDialog').modal({backdrop:'static', show:true})		
					} else {
						status = 'Automatically Answering...';
						event_object.endpoint.accept();
					}
				},				
				'session:rejected' : function(event_object) { // The session was rejected, display a message, cleanup.
					updateMessage("Session was rejected - reason: " + event_object.reason);
				},
				'session:stopped' : function(event_object) { // The session stopped, display a message, cleanup.
					alertMessage("Session stopped - reason: " + event_object.reason);
				},	
				'session:failed' : function(event_object) { // Establishing the session failed, display a message, cleanup.
					alertMessage("Connection failed - reason: " + event_object.reason);
				},	
				'session:refer' : function(conn) { // An inbound Refer was received.
					$('#answerDialog .modal-body').html("[3PCC] Initiate call to " + conn.toEndpointID);
					$('#answerDialog').modal({backdrop:'static', show:true})
				},
			});
	
	// doRegister()
	//  This function does the initialization of the rtcommEndpointProvider using epConfig.   
	//  This config is using getRtcommEndpoint = true so that the initialization returns a rtcommEndpoint object.  
	//  This can be decoupled getRtcommEndpoint() can be called individually.
	function doRegister(userid) {
		updateMessage('Registering... ' + userid);
		epConfig.userid = userid;
		epConfig.server = messageServerHostVariable;
		epConfig.port = messageServerPortVariable;
		epConfig.rtcommTopicPath = ("/" + rtcommTopicPathVariable + "/");

		// Call init() on the Endpoint Provider.
		rtcommEndpointProvider.init(epConfig,
						function(object) {  //onSuccess
							console.log('** WebRTC Register ** init was successful, rtcommEndpoint: ', object);
							updateMessage('Registered'); // Update the UI with message. 
							uiRegister(userid);	
							rtcommEndpoint = object.endpoint; // Configure the rtcommEndpoint.
						},
						function(error) { //onFailure
							console.error('Initialization and registration failed: ', error);
							uiUnregister();
							alertMessage('Initialization and registration failed!');
						});	

		return true;
	};

	/*****************************************************************
	 * UI Related functionality
	 *
	 * This section handles manipulating the UI via jQuery/bootstrap
	 * in reaction to doRegister and the endpointCallbacks.
	 ****************************************************************/
			
	function setEndpointFlag(value) {
		endpointFlag = value;
		console.log('In setEndpointFlag where endpointFlag = ' + value);
	}
	
	function updatePresence(presenceData) {
		console.log('updatePresence', presenceData);
		$('#presenceRegisterUsers').html(" ");
		$('#presenceChatUsers').html(" ");
		$('#presenceAudioUsers').html(" ");
		$('#presenceVideoUsers').html(" ");
		for (var i=0; i<presenceData.length; i++) {
			var node = presenceData[i];
			console.log('updatePresence where node.name = ', node.name);
			$('#presenceRegisterUsers').append('<p>' + node.name + '</p>');
			$('#presenceChatUsers').append('<p>' + node.name + '</p>');
			$('#presenceAudioUsers').append('<p>' + node.name + '</p>');
			$('#presenceVideoUsers').append('<p>' + node.name + '</p>');
		};
	};

	function uiConnect(message) {
		console.log('Entering uiConnect: ' + message);
		$('#btnConnectChat').text('Disconnect');
		$('#btnConnectAudio').text('Disconnect');
		$('#btnConnectVideo').text('Disconnect');
		$('#btnChatSend').prop('disabled', false);
		updateMessage(message || 'Unknown');
		connected = true;
	};

	function uiDisconnect(message) {
		console.log('Entering uiDisconnect: ' + message);
		$('#btnConnectChat').text('Connect');
		$('#btnConnectAudio').text('Connect');
		$('#btnConnectVideo').text('Connect');
		$('#btnChatSend').prop('disabled', true);
		updateMessage(message || 'Unknown');
		connected = false;
	};

	function uiRegister(id) {
		var userid = id || 'unknown';
		$('#displayNameString1').html(
				'<span class="black"> Welcome, ' + userid + ' ! </span> <br/>');
		$('#displayNameString2').html(
				'<span class="black" style="font-weight: bold;"> Welcome, ' + userid + ' ! </span> <br/>' +
				'<span class="black"> Now that you are registered, please select "Message with Us", "Talk to Us" or "Video Chat with Us" </span>');
		updateMessage('Registered');
		$('#btnConnectChat').prop('disabled', false);
		$('#btnConnectAudio').prop('disabled', false);
		$('#btnConnectVideo').prop('disabled', false);
		$('#btnRegister').text('Unregister');
		registered = true;
	};

	function uiUnregister() {
		$('#displayNameString1').html('<span class="black">Welcome! Please Register</span>');
		$('#displayNameString2').html('<span class="black">Please Register</span>');
		updateMessage('Please Register');
		$('#btnConnectChat').prop('disabled', true);
		$('#btnConnectAudio').prop('disabled', true);
		$('#btnConnectVideo').prop('disabled', true);	
		$('#btnRegister').text('Register');
		registered = false;
	}	

	function sendMessage(chatMessage) {
		console.log('Entering sendMessage where chatMessage = ' + chatMessage);		
		rtcommEndpoint.chat.send(chatMessage); 
						
		console.log('In sendMessage where chatMessage = ' + chatMessage);
  		var chat = {
  				time : (new Date().toLocaleString()),
  				name : rtcommEndpoint.getUserID(),
  				message : chatMessage 
  		};	
        $('#chatContainer').append('<p><b>' + chat.name + ': </b><small> ' + chat.time + '</small></p>');
        $('#chatContainer').append('<p>' + chat.message + '</p>');
        $('#chatContainer').append('<hr align="left">'); 
        
		console.log('In sendMessage: scroll to bottom; number of px from the top');        
        $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
        currentScrollPosition = $("#chatContainer").scrollTop();
        console.log('In sendMessage: after currentScrollPosition: ' + currentScrollPosition);
        
        console.log('In sendMessage: reset chatMessage input text');
        $('#chatMessage').val('');  
    }	

	function clearInput(element){
		element.value="";
	}
	
	function updateMessage(message) {
		console.log('updateMessage --> ' + message);
		$('#statusMessageRegister').css('color', 'black');
		$('#statusMessageRegister').html(message);
		$('#statusMessageChat').css('color', 'black');
		$('#statusMessageChat').html(message);
		$('#statusMessageAudio').css('color', 'black');
		$('#statusMessageAudio').html(message);
		$('#statusMessageVideo').css('color', 'black');
		$('#statusMessageVideo').html(message);
	}
	
	function alertMessage(message) {
		console.log('alertMessage --> ', message);
		$('#statusMessageRegister').css('color', 'red');
		$('#statusMessageRegister').html(message);
		$('#statusMessageChat').css('color', 'red');
		$('#statusMessageChat').html(message);
		$('#statusMessageAudio').css('color', 'red');
		$('#statusMessageAudio').html(message);
		$('#statusMessageVideo').css('color', 'red');
		$('#statusMessageVideo').html(message);
	}
	
	function displayConfig() {
		var configHTML = "";
		for (key in epConfig) {
			if (epConfig.hasOwnProperty(key)) {
				configHTML = configHTML + keyPairToHtml(key, epConfig[key]);
			}
		}
		$('#config').html(configHTML);
	}

	function keyPairToHtml(key, value) {
		var template = '<b>label</b>:value<br/>';
		var str = template.replace(/label/i, key);
		return str.replace(/value/i, value);
	}

	function getUrlParams() {
		var url = decodeURIComponent(document.URL);
		var params = [];
		if (url.indexOf('?') > 0) {
			params = url.slice(url.indexOf('?') + 1).split('&');
		}
		var paramhash = {}; // param to hash.
		params.forEach(function(param) {
			var kv = param.split('=');
			paramhash[kv[0]] = kv[1];
		})
		console.log(paramhash);
		return paramhash;
	}
	
	function updateEndpointProviderConfig(messageServerHostInput, messageServerPortInput) {
		console.log('Entering updateEndpointProviderConfig where messageServerHostInput = ' + messageServerHostInput + ' and messageServerPortInput = ' + messageServerPortInput);
		messageServerHost = messageServerHostInput;
		messageServerPort = messageServerPortInput;
    }
	
	$('#btnRegister').on('click', function() {
		if (registered) {
			rtcommEndpointProvider.destroy();
			uiUnregister();
		} else {
			updateMessage('Registering');
			$('#registerModal').modal('show');
		}
	});
	
	$('#regGo').on('click', function() {
		var userid = $("#userid").val();
		if (userid === "") {
			$('#regAlert').show();
			return false;
		} else {
			doRegister(userid);
			$('#regAlert').hide();
		}
	});
	
	$('#btnConnectChat').on('click', function() {
		console.log('Entering btnConnectChat: ' + connected, rtcommEndpoint);		
		if (connected) {
			console.log('In btnConnectChat: connected ');
			rtcommEndpoint.disconnect();
			console.log('In btnConnectChat: after rtcommEndpoint.disconnect() ');
			uiDisconnect("Disconnected");
			console.log('In btnConnectChat: after uiDisconnect(Disconnected) ');
		} else {
			$('#connectModal').modal('show');
		}
	});
	
	$("#chatMessage").keyup(function(event){  //send the chat message when hit the enter key
	    if(event.keyCode == 13){
	        $("#btnChatSend").click();
	    }
	});
	
	$('#btnConnectAudio').on('click', function() {
		console.log('Entering btnConnectAudio: ' + connected, rtcommEndpoint);	
		rtcommEndpoint.webrtc.setLocalMedia({ // Set the media webrtc will use, but do not ENABLE AV yet.
			enable : false,
		});
		rtcommEndpoint.webrtc.setBroadcast({audio:true, video: false});
		if (connected) {
			rtcommEndpoint.disconnect();
			uiDisconnect("Disconnected");
		} else {
			$('#connectModal').modal('show');
		}
	});
	
	$('#btnConnectVideo').on('click', function() {
		console.log('Entering btnConnectVideo: ' + connected, rtcommEndpoint);		
		rtcommEndpoint.webrtc.setLocalMedia({ // Set the media webrtc will use, but do not ENABLE AV yet.
			enable : false,
			mediaOut : document.querySelector('#selfView'),
			mediaIn : document.querySelector('#remoteView'),
		});
		console.log('** CustomerCareInputForm.html ** Enabling Webrtc...');								  
		rtcommEndpoint.webrtc.setBroadcast({audio:true, video: true});
		if (connected) {
			rtcommEndpoint.disconnect();
			uiDisconnect("Disconnected");
		} else {
			$('#connectModal').modal('show');
		}
	});
	
	$("#connectGo").on('click', function(event) {
		var remoteid = $("#remoteId").val();
		if (remoteid === "") {
			$('#connAlert').show();
			return false;
		} else {
			updateMessage('Connecting to ' + remoteid);
			// This is necessary if you want to enable/connect in same motion. 
			if (endpointFlag == 'chat'){
				console.log('In connectGo: endpointFlag == chat ');
				rtcommEndpoint.chat.enable();
				console.log('In connectGo: after rtcommEndpoint.chat.enable() ');
				rtcommEndpoint.connect(remoteid);
				console.log('In connectGo: after rtcommEndpoint.connect(remoteid) ');
				uiConnect("Connected");
			} else {	
				console.log('In connectGo: endpointFlag is equal to audioOnly or videoChat ');
				rtcommEndpoint.webrtc.enable({'trickleICE': trickleICE}, function(success, message) {
					console.log('Enable Completed: '+ success);
					if (success) {
						rtcommEndpoint.connect(remoteid);
					} else {
						console.error('enable failed: '+ message)
					}
		        });
			}
			$("#connectModal").modal("hide");
			$('#connAlert').hide();
		}
		return false;
	});

	$('#answerYes').on('click', function() {
		if (endpointFlag == 'chat'){
			console.log('In answerYes: endpointFlag = chat');
			rtcommEndpoint.chat.enable();
			console.log('In answerYes: after rtcommEndpoint.chat.enable() ');
			rtcommEndpoint.webrtc.disable();
			console.log('In answerYes: after rtcommEndpoint.webrtc.disable() ');
			uiConnect("Connected");
		} else if (endpointFlag == 'audioOnly') {
		   console.log('In answerYes: endpointFlag = audioOnly');
		   rtcommEndpoint.webrtc.setLocalMedia({ // Set the media webrtc will use, but do not ENABLE AV yet.
				enable : false,
			});
		   rtcommEndpoint.webrtc.setBroadcast({audio:true, video: false});
		} else if (endpointFlag == 'videoChat') {		   
		   console.log('In answerYes: endpointFlag = videoChat');
		   rtcommEndpoint.webrtc.setLocalMedia({ // Set the media webrtc will use, but do not ENABLE AV yet.
				enable : false,
				mediaOut : document.querySelector('#selfView'),
				mediaIn : document.querySelector('#remoteView'),
			});
		   console.log('** CustomerCareInputForm.html ** Set Broadcast...');
		   rtcommEndpoint.webrtc.setBroadcast({audio:true, video: true});
		}
		$("#answerDialog").modal("hide");
		rtcommEndpoint.accept();
	});
	
	$('#answerNo').on('click', function() {
		$("#answerDialog").modal("hide");
		state = 'disconnected';
		rtcommEndpoint.reject();
	});

	$('#btnReset').on('click', function() {
				rtcommEndpoint && rtcommEndpoint.reset();
				rtcommEndpoint = null;
				rtcommEndpointProvider && rtcommEndpointProvider.destroy();
				rtcommEndpointProvider = null;
				updateMessage('<span class="black" Please Register</span>');
				$('#btnRegister').hasClass('active') && $('#btnRegister').button('toggle');
	});

	$('#btnToggleBroadcast').on('click', function() {
		if (connected) {
			rtcommEndpoint.webrtc.pauseBroadcast();
		}
	});	
