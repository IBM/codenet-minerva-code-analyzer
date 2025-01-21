package com.acme.modres.security;

// import com.sun.net.ssl.SSLContext; // Not availible in Java 17
// import com.sun.net.ssl.TrustManager; // Not availible in Java 17

import java.util.logging.Logger;

public class SSLUtils {
    private static final Logger logger = Logger.getLogger(SSLUtils.class.getName());
    // private SSLContext getContext() throws Exception {
    //     try {
    //         SSLContext sc = SSLContext.getInstance("SSL");
    //         sc.init(null, // we don't need KeyManager
    //             new TrustManager[]{new FakeX509TrustManager()},
    //             new java.security.SecureRandom());
    //         return sc;
    //     } catch (Exception exc) {
    //         throw new Exception("Some error");
    //     }
    // }
}
