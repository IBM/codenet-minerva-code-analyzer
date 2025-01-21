
   "use strict";

	var CRLF = "\r\n";
	
    function postCatalogVideo() {
    	console.log('Entered postCatalogVideo');
    	
    	var productVideo1Name = document.getElementById("productVideo1Name").value;
    	console.log('In postCatalogVideo: productVideo1Name = ' + productVideo1Name);
    	
		var productVideo1File = document.getElementById('productVideo1').files[0];
		var productVideo1FileSize = document.getElementById('productVideo1').files[0].size;  	
		console.log('In postCatalogVideo: productVideo1FileSize = ' + productVideo1FileSize);
		
		this.setProtocol();
		
		jQuery.post("/WebCatalogVideoLite/uploadVideo", function(response) { 
			console.log('Entered jQuery.post( /WebCatalogVideoLite/uploadVideo, function(response)');
		})	
    }	

	function setProtocol() {
		console.log('In setProtocol');
		
        var host = "localhost";
        var port = "9080";
        var socket = io("http://localhost");
		//var socket = new Socket(host, port); 
        var input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        var contextRoot = "/WebCatalogVideoLite/uploadVideo";
        var valid = true;       

        output.write("POST " + contextRoot + "/test HTTP/1.1" + CRLF);
        output.write("User-Agent: Java/1.6.0_33" + CRLF);
        output.write("Host: " + host + ":" + port + CRLF);

        output.write("Upgrade: uploadVideo" + CRLF);
        output.write("Connection: Upgrade" + CRLF);
        output.write("Content-type: application/x-www-form-urlencoded" + CRLF);
        output.write(CRLF);
        output.flush();
        
        var line = null;
        // consume http headers
        var containsUpgrade = false;
        while ((line = input.readLine()) != null) {
            console.log(line);
            containsUpgrade = containsUpgrade || (line.toLowerCase().startsWith("upgrade"));
            if ("".equals(line)) {
               break;
            }
         }
         valid = valid && containsUpgrade;

         console.log("Video Filename");
         output.write(productVideo1Name + "\n");
         output.flush();

         line = input.readLine();
         console.log(line);

         console.log("Video File");
         output.write(productVideo1File + "\n");
         output.flush();

         line = input.readLine();
         console.log(line);

         output.write("EXIT\n");
         output.flush();  
    } 
        	
	function clearInput(element){
		element.value="";
	}
	
	function updateMessage(message) {
		console.log('updateMessage --> ' + message);
		$('#statusMessageVideo').css('color', 'black');
		$('#statusMessageVideo').html(message);
	}
	
	function alertMessage(message) {
		console.log('alertMessage --> ', message);
		$('#statusMessageVideo').css('color', 'red');
		$('#statusMessageVideo').html(message);
	}	
	
	$('#btnConnectVideo').on('click', function() {
		console.log('** CatalogVideoInputForm.html');								  
	})
