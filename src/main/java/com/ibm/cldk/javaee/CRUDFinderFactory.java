package com.ibm.cldk.javaee;

import com.ibm.cldk.javaee.utils.interfaces.AbstractCRUDFinder;
import com.ibm.cldk.javaee.jakarta.JPACRUDFinder;
import com.ibm.cldk.javaee.jdbc.JDBCCRUDFinder;
import com.ibm.cldk.javaee.spring.SpringCRUDFinder;
import org.apache.commons.lang3.NotImplementedException;

import java.util.stream.Stream;

public class CRUDFinderFactory {
    public static AbstractCRUDFinder getCRUDFinder(String framework) {
        switch (framework.toLowerCase()) {
            case "jpa":
            case "jakarta":
                return new JPACRUDFinder();
            case "spring":
            case "springboot":
                return new SpringCRUDFinder();
            case "jdbc":
                return new JDBCCRUDFinder();
            case "camel":
                throw new NotImplementedException("Camel CRUD finder not implemented yet");
            case "struts":
                throw new NotImplementedException("Struts CRUD finder not implemented yet");
            default:
                throw new IllegalArgumentException("Unknown framework: " + framework);
        }
    }

    public static Stream<AbstractCRUDFinder> getCRUDFinders() {
        return Stream.of(new JPACRUDFinder(), new SpringCRUDFinder(), new JDBCCRUDFinder());
    }
}