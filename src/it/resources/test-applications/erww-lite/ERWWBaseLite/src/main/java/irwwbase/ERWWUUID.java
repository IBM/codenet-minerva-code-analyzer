package irwwbase;

import java.net.InetAddress;
import java.security.SecureRandom;

public class ERWWUUID
{ 
	//
	// This class generates a 32 byte uniqueid String that is to be used for primary key generation that 
	// is guaranteed (sure it is) to be unique in space and time across all systems.  
	// It uses a concatenation of the hexadecimal values for Object Hashcode, IpAddress, 
	// secure random number, and time in milliseconds. 
	//  
	
    private ERWWUUID()
    {
        inetAddressString = null;
        myHashCode = 0;
        aSecureRandomGen = null;
        try
        {
          InetAddress inetAddress = InetAddress.getLocalHost();
          inetAddressString = getStringFromBytes(inetAddress.getAddress());
          myHashCode = System.identityHashCode(this);
          myHashCode = Math.abs(myHashCode);
          aSecureRandomGen = new SecureRandom();
          aSecureRandomGen.nextInt();
        }
        catch(Exception ex)
        {
            System.out.println("ERWWUUID:  Unable to instantiate Singleton.  Exception = " + ex.toString());
        }
    }

    public static ERWWUUID getInstance()
    {
        if(instance == null)
            instance = new ERWWUUID();
        return instance;
    }

   //   sample output 
   //    ERWWUUID = 1f3a6964 | 9b9aaf05 | 7235d718 | c0a8165
   //            hashcode   time       random      ip address
   
    public String getERWWUUID()
    {
    	//
    	// This is the recommended ERWWUUID format. The others are provided for compatability 
    	// only and are to be considered deprecated.  
    	// 
    	//   sample output format : 
    	// 
        //    ERWWUUID = 1f3a6964 | 9b9aaf05 | 7235d718 | c0a8165
        //           hashcode   time       random     ip address
        //
        
        StringBuffer aStringBuffer = new StringBuffer();
        String aSecureRandomNumberinHex = Integer.toHexString(aSecureRandomGen.nextInt());
        aStringBuffer.append(aSecureRandomNumberinHex);
        aStringBuffer.append(Integer.toHexString(myHashCode));
        String timeLowInHex = Integer.toHexString((int)(System.currentTimeMillis() & -1L));
        aStringBuffer.append(timeLowInHex);
        aStringBuffer.append(inetAddressString);
        // System.out.println("ERWWUUID = " + aStringBuffer.toString());
        return aStringBuffer.toString();
    } 
 
    public long getLongERWWUUID()
    { 
    	//
    	// this returns a positive long integer that is not as guaranteed to be unique as
    	// the string version. Hopefully it will suffice for cases where the string won't do!  
    	//  
    	
    	long aLong  = -1; 
    	boolean flag= false;
    	
    	while (flag != true) 
    	{
          StringBuffer aStringBuffer = new StringBuffer(); 
          aStringBuffer.append(aSecureRandomGen.nextInt()); 
          aStringBuffer.append(myHashCode);
          try 
           {
            aLong = Long.parseLong(aStringBuffer.toString()); 
            if (aLong > 0)  
                flag = true; 
           }
           catch (NumberFormatException ex) 
           { 
           	 // loop until we get a valid number  
           } 
    	 };    	 
                 
       return aLong;         
    }  
    
    public int getIntERWWUUID()
    { 
    	//
    	// this returns a positive integer that is not as guaranteed to be unique as
    	// the string version. Hopefully it will suffice for cases where the string won't do!  
    	//  
    	
        int anInt = -1; 
    	  
    	while (anInt < 0)
    	 {    	    	
          anInt = aSecureRandomGen.nextInt(); 
    	 }
            
       return anInt;         
    }  

    private String getStringFromBytes(byte aByteArray[])
    {
        StringBuffer aSB = new StringBuffer(); 
        String aHexString = null;
        for(int i = 0; i < aByteArray.length; i++)
        {
            int uByte = aByteArray[i] >= 0 ? ((int) (aByteArray[i])) : aByteArray[i] + 256; 
            aHexString =  Integer.toHexString(uByte); 
            if (aHexString.length() == 1) 
              { 
               aSB.append("0"); 
               aSB.append(aHexString); 
              } 
            else    
              {          
              aSB.append(Integer.toHexString(uByte));
              } 
        }

        return aSB.toString();
    }

    private static ERWWUUID instance = null;
    private String inetAddressString;
    private int myHashCode;
    private SecureRandom aSecureRandomGen;

}
