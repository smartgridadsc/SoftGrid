/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.24 at 04:18:37 PM SGT 
//


package it.illinois.adsc.ema.control.cid;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DOIType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DOIType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SDI" type="{http://www.iec.ch/61850/2003/SCL}SDIType" minOccurs="0"/>
 *         &lt;element name="DAI" type="{http://www.iec.ch/61850/2003/SCL}DAIType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="desc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DOIType", namespace = "http://www.iec.ch/61850/2003/SCL", propOrder = {
    "sdi",
    "dai"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class DOIType {

    @XmlElement(name = "SDI", namespace = "http://www.iec.ch/61850/2003/SCL")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected SDIType sdi;
    @XmlElement(name = "DAI", namespace = "http://www.iec.ch/61850/2003/SCL")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected List<DAIType> dai;
    @XmlAttribute(name = "name")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String name;
    @XmlAttribute(name = "desc")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String desc;

    /**
     * Gets the value of the sdi property.
     * 
     * @return
     *     possible object is
     *     {@link SDIType }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public SDIType getSDI() {
        return sdi;
    }

    /**
     * Sets the value of the sdi property.
     * 
     * @param value
     *     allowed object is
     *     {@link SDIType }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setSDI(SDIType value) {
        this.sdi = value;
    }

    /**
     * Gets the value of the dai property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dai property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDAI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DAIType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public List<DAIType> getDAI() {
        if (dai == null) {
            dai = new ArrayList<DAIType>();
        }
        return this.dai;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-10-24T04:18:37+08:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setDesc(String value) {
        this.desc = value;
    }

}
