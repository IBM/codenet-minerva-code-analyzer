/*
Copyright IBM Corporation 2023, 2024

Licensed under the Apache Public License 2.0, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ibm.northstar.entities;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import org.jgrapht.nio.Attribute;

import java.io.Serializable;
import java.util.Map;

/**
 * The type Abstract graph edge.
 */
public abstract class AbstractGraphEdge implements Serializable {
    /**
     * The Context.
     */
    public final String context;
    /**
     * The Weight.
     */
    public Integer weight = 1;

    /**
     * Instantiates a new Abstract graph edge.
     */
    protected AbstractGraphEdge() {
        this(null);
    }

    /**
     * Instantiates a new Abstract graph edge.
     *
     * @param context the context
     */
    protected AbstractGraphEdge(String context) {
        this.context = context;
    }

    /**
     * Increment weight.
     */
    public void incrementWeight() {
        this.weight += 1;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public String getContext() {
        return this.context;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return this.hashCode();
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public Integer getWeight() {
        return this.weight;
    }

    /**
     * Gets statement position.
     *
     * @param statement the statement
     * @return the statement position
     */
    Integer getStatementPosition(Statement statement) {
        CGNode statementNode = statement.getNode();
        IR statementIR = statementNode.getIR();
        Integer pos = null;
        // TODO: check this assumption: the same source instruction maps to several
        // SSAInstructions,
        // therefore it is sufficient to return the position of the first statement.
        for (SSAInstruction inst : statementNode.getIR().getInstructions()) {
            try {
                pos = statementIR.getMethod().getSourcePosition(inst.iIndex()).getLastLine();
                return pos;
            } catch (InvalidClassFileException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException npe) {
                return -1;
            }
        }
        return pos;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public abstract Map<String, Attribute> getAttributes();
}
