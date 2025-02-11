package com.ibm.cldk.javaee;

import com.ibm.cldk.javaee.utils.interfaces.AbstractCRUDFinder;
import com.ibm.cldk.javaee.utils.interfaces.AbstractEntrypointFinder;
import com.ibm.cldk.javaee.jakarta.JPACRUDFinder;
import com.ibm.cldk.javaee.jakarta.JakartaEntrypointFinder;
import com.ibm.cldk.javaee.jdbc.JDBCCRUDFinder;
import com.ibm.cldk.javaee.spring.SpringCRUDFinder;
import com.ibm.cldk.javaee.struts.StrutsEntrypointFinder;
import org.apache.commons.lang3.NotImplementedException;

import java.util.stream.Stream;

public class EntrypointsFinderFactory {
    public static AbstractEntrypointFinder getEntrypointFinder(String framework) {
        switch (framework.toLowerCase()) {
            case "jakarta":
                return new JakartaEntrypointFinder();
            case "spring":
                return new StrutsEntrypointFinder();
            case "camel":
                throw new NotImplementedException("Camel CRUD finder not implemented yet");
            case "struts":
                throw new NotImplementedException("Struts CRUD finder not implemented yet");
            default:
                throw new IllegalArgumentException("Unknown framework: " + framework);
        }
    }

    public static Stream<AbstractCRUDFinder> getEntrypointFinders() {
        return Stream.of(new JPACRUDFinder(), new SpringCRUDFinder(), new JDBCCRUDFinder());
    }
}
