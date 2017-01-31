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

package it.illinois.adsc.ema.control.conf;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for PWModelType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PWModelType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="proxyNode" type="{}proxyNodeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="controlCenterNode" type="{}controlCenterNodeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PWModelType", propOrder = {
    "proxyNode",
    "controlCenterNode"
})
@XmlRootElement
public class PWModelType {

    protected List<ProxyNodeType> proxyNode;
    @XmlElement(required = true)
    protected ControlCenterNodeType controlCenterNode;

    /**
     * Gets the value of the proxyNode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the proxyNode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProxyNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProxyNodeType }
     * 
     * 
     */
    public List<ProxyNodeType> getProxyNode() {
        if (proxyNode == null) {
            proxyNode = new ArrayList<ProxyNodeType>();
        }
        return this.proxyNode;
    }

    /**
     * Gets the value of the controlCenterNode property.
     * 
     * @return
     *     possible object is
     *     {@link ControlCenterNodeType }
     *     
     */
    public ControlCenterNodeType getControlCenterNode() {
        return controlCenterNode;
    }

    /**
     * Sets the value of the controlCenterNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlCenterNodeType }
     *     
     */
    public void setControlCenterNode(ControlCenterNodeType value) {
        this.controlCenterNode = value;
    }

}
