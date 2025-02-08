package com.ibm.cldk.analysis;

import com.ibm.cldk.analysis.interfaces.AbstractCRUDFinder;
import com.ibm.cldk.analysis.interfaces.AbstractEntrypointFinder;
import com.ibm.cldk.analysis.jakarta.JPACRUDFinder;
import com.ibm.cldk.analysis.jakarta.JakartaEntrypointFinder;
import com.ibm.cldk.analysis.jdbc.JDBCCRUDFinder;
import com.ibm.cldk.analysis.spring.SpringCRUDFinder;
import com.ibm.cldk.analysis.struts.StrutsEntrypointFinder;
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
