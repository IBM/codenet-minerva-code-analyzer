package application.client.product.catalog.lite;

import java.io.*;
import java.net.Socket;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class ProductFileUploadClient extends JPanel {

	private static final long serialVersionUID = -4970368737442767145L;
    private static final String CRLF = "\r\n";
	static private final String line = "\n";
	protected Action InputAction;
	private JTextArea jTextArea = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JTextField productField = null;
	private JTextField productNameField = null;
	private JTextField hostField=null;
	private JTextField portField=null;
	private JButton submitButton = null;
	private File selectedFile = null;	
	private InputStream inputStream = null;
 
    public ProductFileUploadClient() {
        super(new BorderLayout());
		initialize();
    }
    	
	private void initialize() {
		
        //jScrollPane 
        jTextArea = new JTextArea(10,6);
        jTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);        
                 
		//jPanel1 
		jPanel1 = new JPanel();
		jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.PAGE_AXIS));
        jPanel1.setBackground(new Color(255,248,220)); 
        
    	JTextField hostLabel = new JTextField();
    	hostLabel.setBounds(new Rectangle(20, 10));
    	hostLabel.setEditable(false);
    	hostLabel.setText("Enter Host Name:"); 
        jPanel1.add(hostLabel);
        
        hostField = new JTextField(22); 
        hostField.setBounds(new Rectangle(20, 10));
        hostField.setText("localhost");
		hostField.setEditable(true);
        jPanel1.add(hostField); 
        
    	JTextField portLabel = new JTextField();
    	portLabel.setBounds(new Rectangle(20, 10));
    	portLabel.setEditable(false);
    	portLabel.setText("Enter Port:"); 
        jPanel1.add(portLabel);
        
        portField = new JTextField(22); 
        portField.setBounds(new Rectangle(20, 10));
        portField.setText("9080");
		portField.setEditable(true);
        jPanel1.add(portField);
        
    	JTextField productNameLabel = new JTextField();
    	productNameLabel.setBounds(new Rectangle(20, 10));
    	productNameLabel.setEditable(false);
    	Font font = productNameLabel.getFont();
    	productNameLabel.setFont(font.deriveFont(Font.BOLD,15));
    	productNameLabel.setText("Enter Product Name for the ERWW-Lite Catalog:"); 
        jPanel1.add(productNameLabel);
        
        productNameField = new JTextField(22); 
		productNameField.setBounds(new Rectangle(25, 0, 10, 10));
		productNameField.setText("Type Product Name here");
		productNameField.setEditable(true);
        jPanel1.add(productNameField);   
        
        JLabel blankLine = new JLabel("<html><br></html>");
        jPanel1.add(blankLine);

        //jPanel2
		jPanel2 = new JPanel();
        jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.PAGE_AXIS));
        jPanel2.setBackground(new Color(255,248,220));
        
        jPanel2.add(getBrowseButtonProductFile()); 
        jPanel2.add(blankLine);
        
        InputAction = new InputAction("Submit");
		submitButton = new JButton(InputAction);
		submitButton.setEnabled(false);
        jPanel2.add(submitButton);       
        
		productField = new JTextField();
		productField.setBounds(new Rectangle(10, 10));
		productField.setEditable(false);
		productField.setBackground(new Color(255,248,220));
		productField.setText("Product Displays Here");         
		
        //jPanel3
		jPanel3 = new JPanel();
        jPanel3.setBackground(new Color(255,248,220));
        jPanel3.add(new JLabel(new ImageIcon("resources/images/Product_Image_Display_Here.png")));
                        
        add(jPanel1, BorderLayout.NORTH);
        add(jPanel2, BorderLayout.WEST);
        add(jPanel3, BorderLayout.CENTER);
        add(jScrollPane, BorderLayout.SOUTH);
    }
    
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndDisplayWindow();
            }
        });
    }
    
    /**
     * Create and display the Window.  
     * For thread safety, this method should be invoked from the event dispatch thread.
     */
    private static void createAndDisplayWindow() {
        //Create and set up the window.
        JFrame jFrame = new JFrame("ERWW_Lite Catalog Product: Servlet 3.1 Upgrade/Request");
 
        //Add content to the window.
        jFrame.add(new ProductFileUploadClient());
 
        //Display the window.
        Insets insets = jFrame.getInsets();
        jFrame.setSize(625 + insets.left + insets.right, 500 + insets.top + insets.bottom);
        jFrame.getContentPane().setBackground(new Color(255,248,220));  
        jFrame.setVisible(true);
        
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
	private JButton getBrowseButtonProductFile() {
		
		JButton browseButton = null;	
		
		if (browseButton == null) {
			browseButton = new JButton();
			browseButton.setBounds(new Rectangle(10, 10, 100, 10));
			browseButton.setText("Browse to a Product File Image");
			browseButton.addActionListener(new java.awt.event.ActionListener() {
			
			public void actionPerformed(java.awt.event.ActionEvent e) {					
							
				JFileChooser fileChooser = new JFileChooser();
				
				try{
					int returnValue = fileChooser.showOpenDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						selectedFile = fileChooser.getSelectedFile();
						submitButton.setEnabled(true);	
					}else{
		        		selectedFile = null;
		        		submitButton.setEnabled(false);
					}
				} catch (Throwable t){
					jTextArea.append("Failed to load!");	
					t.printStackTrace();
					return;
				}	
				jTextArea.append("Click on the Submit Button to upload and view the Product" + line);
				jTextArea.append("Selected File: " + selectedFile.getAbsolutePath()+ line);
				}
			});
		}
		return browseButton;
	}
	
    public class InputAction extends AbstractAction {

		private static final long serialVersionUID = -8855547556803921024L;
		public InputAction(String text) {
            super(text);
        }
        public void actionPerformed(ActionEvent actionEvent) {
        	jTextArea.append("Entered InputAction"+ line);
	 		String contextRoot = "/WebProductCatalogLite/uploadProduct";
	 		String host = hostField.getText();
	 		String port = portField.getText();
	 		String productName = null;
	 		String productFileName = null;
	 		String productFilePath = null;
            byte [] productFile = null;	 		
	 		Socket socket = null;
            OutputStream outputStream = null;
            BufferedWriter output = null;
            BufferedReader input = null;            
            ObjectOutputStream objectOutputStream = null;
            Image image = null;
            Image imageOriginal = null;
            File imageFile = null;
            ByteArrayOutputStream byteArrayInputStreamOriginal = null;
            ByteArrayOutputStream byteArrayInputStreamReturned = null;
            
            try { 
                socket = new Socket(host, Integer.parseInt(port));
                outputStream = socket.getOutputStream();
                output = new BufferedWriter(new OutputStreamWriter(outputStream));
    		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: getting socket, outputStream or output" + line);
    		}
            
            try { 
            	output.write("POST " + contextRoot + " HTTP/1.1" + CRLF);
            	output.write("User-Agent: Java/1.6.0_33" + CRLF);
            	output.write("Host: " + host + ":" + port + CRLF);
            	output.write("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2" + CRLF);
            	output.write("Upgrade: NIOReadListener" + CRLF);
            	output.write("Connection: Upgrade" + CRLF);
            	output.write("Content-type: application/x-www-form-urlencoded" + CRLF);
            	output.write(CRLF);
            	output.flush(); 
    		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: writing header" + line);
    		} 
            
        	try{ 
        		objectOutputStream = new ObjectOutputStream(outputStream);
        	} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: new up objectOutputStream" + line);
        	}
    			
      		try{
    			productName = productNameField.getText();
    			objectOutputStream.writeObject(productName);
    			objectOutputStream.flush();
    			jTextArea.append("ProductName = "+ productName + line);
    		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: writing productName" + line);
    		}
    		
    		try{
    			productFileName = selectedFile.getName();
    			objectOutputStream.writeObject(productFileName);
    			objectOutputStream.flush();
      			jTextArea.append("ProductFileName = "+ selectedFile.getName() + line);
    		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: writing productFileName" + line);
    		}
    		
    		try {
    			productFilePath = selectedFile.getAbsolutePath();
    		    File file = new File(productFilePath);
    		    byte[] productFileByteArray = new byte[(int) file.length()];
    		    int productByteArrayLength = productFileByteArray.length;
    			objectOutputStream.writeObject(productByteArrayLength);
    		    objectOutputStream.flush();
    			jTextArea.append("ProductFileByteArray.length = " + productByteArrayLength + " : used for verification" + line);
      		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: writing productByteArrayLength" + line);
    		} 
            
    		try {    			
    			productFile = new ProductFile().getProductFile(productFilePath);
    			jTextArea.append("selectedFile.getAbsolutePath() = "+ productFilePath + line);
    			objectOutputStream.writeObject(productFile);
    		    objectOutputStream.flush();
    		    jTextArea.append("ProductFile written to ObjectOutputStream "+ line);
    		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: writing productFile" + line);
    		}  
    		
    		try {    			
    			objectOutputStream.writeObject(-1);
    		    objectOutputStream.flush();
    		    jTextArea.append("End of ObjectOutputStream "+ line);
    		} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: writing -1" + line);
    		}
        		
        	try {
        		inputStream = socket.getInputStream(); 
        		jTextArea.append("Got inputStream from socket "+ line);
          	} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: getting inputStream" + line);
        	} 
        	
        	try {
           		if (inputStream.read() == -1){
        			jTextArea.append("ProductCatalogLite Status: InputStream is empty:  Failed" + line);         			
        		} else {        		
        			jTextArea.append("inputStream.available(); "+ inputStream.available() + line);    
        			
        			String inputLine = "";
        			input = new BufferedReader(new InputStreamReader(inputStream)); 
        			jTextArea.append("After new BufferedReader(new InputStreamReader(inputStream)) "+ line);    	
        			
                    while ((inputLine = input.readLine()) != null) {
                       jTextArea.append("Within while loop: inputLine: " + inputLine + line);
                       if (inputLine.trim().contains("101")) { 
                    	   jTextArea.append("inputLine: " + inputLine + line);
                       }
                       if (inputLine.trim().equals("")){
                    	   jTextArea.append("inputLine.trim().equals(null) " + line);  
                           break;
                       }
                    } 
                    jTextArea.append("After while loop: inputLine: " + inputLine + line);            			
        		}                      
        	} catch (Exception e) {
                 e.printStackTrace();
                 jTextArea.append("ProductCatalogLite Status: Failed: getting/displaying image" + line);
        	}        	
               	
        	try{ 
        		image = ImageIO.read(inputStream);  
        		jTextArea.append("image: "+ image + line); 
        			        			
            	jPanel3.removeAll();  
            	jPanel3.add(new JLabel(productName));
            	if (image != null){
            		jTextArea.append("image != null" + line);
            		jPanel3.add(new JLabel(new ImageIcon(image)));
            	}
            	jPanel3.repaint();
            	jPanel3.validate();
            	submitButton.setEnabled(false);  
            	
            	String imageType = productFileName.substring(Math.max(0, productFileName.length() - 3));
            	jTextArea.append("Image Type (jpg or png): " + imageType + line); 
            	
        		//Original image converted to image type using ImageIO.read
        	    imageFile = new File(productFilePath);
        	    imageOriginal = ImageIO.read(imageFile);    
            	
        	    //Original image converted to a byteArray to get the byteArray length
            	byteArrayInputStreamOriginal = new ByteArrayOutputStream();
            	ImageIO.write((RenderedImage)imageOriginal, imageType, byteArrayInputStreamOriginal);
             	jTextArea.append("Original image: byte array length: "+ byteArrayInputStreamOriginal.toByteArray().length + line);
            		
             	//Image returned from Server
            	byteArrayInputStreamReturned = new ByteArrayOutputStream();
            	ImageIO.write((RenderedImage)image, imageType, byteArrayInputStreamReturned);
             	jTextArea.append("Image Returned from server: byte array length: "+ byteArrayInputStreamReturned.toByteArray().length + line);
         		
             	//Verification
             	if (byteArrayInputStreamOriginal.toByteArray().length == byteArrayInputStreamReturned.toByteArray().length){          		        			
            		jTextArea.append("ProductCatalogLite Status: Succeed" + line);  
            	}else{
            		jTextArea.append("ProductCatalogLite Status: Failed: Byte Array Lengths of the Orginal and Returned Images do not match." + line);
            	}
          	} catch (Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: getting/displaying image" + line);
        	}         	       	
        	
            try {
                if (outputStream != null) {
                  	outputStream.close();
                }
            }catch(Exception e) {
                 e.printStackTrace();
                 jTextArea.append("ProductCatalogLite Status: Failed: closing outputStream" + line);
            }  

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }catch(Exception e) {
                 e.printStackTrace();
                 jTextArea.append("ProductCatalogLite Status: Failed: closing inputStream" + line);
            }   
            
            try {
                if (output != null) {
                  	output.close();
                }
            }catch(Exception e) {
                 e.printStackTrace();
                 jTextArea.append("ProductCatalogLite Status: Failed: closing output" + line);
            }  

            try {
                if (socket != null) {
                    socket.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
                jTextArea.append("ProductCatalogLite Status: Failed: closing socket" + line);
            }  
        }
   }   	
}