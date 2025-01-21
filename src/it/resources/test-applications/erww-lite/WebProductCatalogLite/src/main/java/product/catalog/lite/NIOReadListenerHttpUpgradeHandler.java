package product.catalog.lite;

import java.io.IOException;
import java.util.Queue;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.WebConnection;

public class NIOReadListenerHttpUpgradeHandler implements HttpUpgradeHandler {

	private static String classname = "NIOReadListenerHttpUpgradeHandler";
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(WebConnection webConnection) {
		try{		
			ServletInputStream input = webConnection.getInputStream();
			ServletOutputStream output = webConnection.getOutputStream();
			ReadListenerImpl readListener = new ReadListenerImpl(input, output, webConnection);
			input.setReadListener(readListener); //associate the ReadListener with the InputStream
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
    protected static void setWriteListener(WebConnection webConnection, String productName, String productFileName, Queue<byte[]> productDataQueue, int productByteArrayLength, ServletOutputStream servletOutputStream) throws IOException {
   	        try {
            WriteListenerImpl writeListener = new WriteListenerImpl(webConnection, productName, productFileName, productDataQueue, productByteArrayLength, servletOutputStream); 
            servletOutputStream.setWriteListener(writeListener);
        } catch (Throwable t) {
			System.out.println("<<< Error: In " + classname + ": Error setting the WriteListener: " + t.getMessage());
			t.printStackTrace();
        }
    }
}
