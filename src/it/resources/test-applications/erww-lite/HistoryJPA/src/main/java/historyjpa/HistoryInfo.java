//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-06/12/2007 09:21 PM(Raja)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.10.03 at 11:30:22 AM PDT 
//


package historyjpa;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 * <p>Java class for HistoryInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HistoryInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HistoryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HistoryDate" type="{}HistoryDateType"/>
 *         &lt;element name="HistoryAmount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HistoryInfo", propOrder = {
    "historyName",
    "historyDate",
    "historyAmount"
})
public class HistoryInfo implements  Serializable{

	private static final long serialVersionUID= 2716055690746597905L;
    @XmlElement(name = "HistoryName", required = true)
    protected String historyName;
    @XmlElement(name = "HistoryDate", required = true)
    protected HistoryDateType historyDate;
    @XmlElement(name = "HistoryAmount")
    protected float historyAmount;

    /**
     * Gets the value of the historyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHistoryName() {
        return historyName;
    }

    /**
     * Sets the value of the historyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHistoryName(String value) {
        this.historyName = value;
    }

    /**
     * Gets the value of the historyDate property.
     * 
     * @return
     *     possible object is
     *     {@link HistoryDateType }
     *     
     */
    public HistoryDateType getHistoryDate() {
        return historyDate;
    }

    /**
     * Sets the value of the historyDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link HistoryDateType }
     *     
     */
    public void setHistoryDate(HistoryDateType value) {
        this.historyDate = value;
    }

    /**
     * Gets the value of the historyAmount property.
     * 
     */
    public float getHistoryAmount() {
        return historyAmount;
    }

    /**
     * Sets the value of the historyAmount property.
     * 
     */
    public void setHistoryAmount(float value) {
        this.historyAmount = value;
    }

}
