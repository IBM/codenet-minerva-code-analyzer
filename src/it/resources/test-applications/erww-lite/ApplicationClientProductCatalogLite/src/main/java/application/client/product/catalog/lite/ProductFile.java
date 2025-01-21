package application.client.product.catalog.lite;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

public class ProductFile implements Serializable {

	private static final long serialVersionUID = 4395401726010124278L;

	public ProductFile(){		
		
	}
	
	public byte[] getProductFile (String filePath){
		
  		File file= null;
		byte[] ProductFile = null;
		InputStream inputStream = null;
		int totalBytesRead = 0;
		
		try {
			file = new File(filePath);
			ProductFile = new byte[(int)file.length()];			
			inputStream = new BufferedInputStream(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try{
		    while (totalBytesRead <  ProductFile.length) {
			    int bytesRemaining = ProductFile.length - totalBytesRead;
		        int bytesRead = inputStream.read(ProductFile, totalBytesRead, bytesRemaining); 
		        if (bytesRead > 0){
		            totalBytesRead = totalBytesRead + bytesRead;
		         }			    
		     }
		}catch (Throwable t){
            t.printStackTrace();
		}	
		
        try {
            if (inputStream != null) {
            	inputStream.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
		
		return ProductFile;
	}
}
