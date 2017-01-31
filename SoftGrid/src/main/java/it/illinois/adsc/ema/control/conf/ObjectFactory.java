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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.illinois.adsc.ema.control.conf package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PWModel_QNAME = new QName("", "PWModel");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.illinois.adsc.ema.control.conf
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PWModelType }
     * 
     */
    public PWModelType createPWModelType() {
        return new PWModelType();
    }

    /**
     * Create an instance of {@link ProxyNodeType }
     * 
     */
    public ProxyNodeType createProxyNodeType() {
        return new ProxyNodeType();
    }

    /**
     * Create an instance of {@link DataType }
     * 
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link ControlCenterNodeType }
     * 
     */
    public ControlCenterNodeType createControlCenterNodeType() {
        return new ControlCenterNodeType();
    }

    /**
     * Create an instance of {@link IedNodeType }
     * 
     */
    public IedNodeType createIedNodeType() {
        return new IedNodeType();
    }

    /**
     * Create an instance of {@link KeyType }
     * 
     */
    public KeyType createKeyType() {
        return new KeyType();
    }

    /**
     * Create an instance of {@link ParametersType }
     * 
     */
    public ParametersType createParametersType() {
        return new ParametersType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PWModelType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "PWModel")
    public JAXBElement<PWModelType> createPWModel(PWModelType value) {
        return new JAXBElement<PWModelType>(_PWModel_QNAME, PWModelType.class, null, value);
    }

}
