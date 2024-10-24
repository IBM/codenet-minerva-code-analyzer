package com.ibm.cldk.entities;

import org.jgrapht.nio.Attribute;

import java.io.Serializable;
import java.util.Map;


public abstract class AbstractGraphVertex implements Serializable {

        public abstract Map<String, Attribute> getAttributes();

        @Override
        public boolean equals(Object obj) {
                return super.equals(obj);
        }

        @Override
        public int hashCode() {
                return super.hashCode();
        }
}
