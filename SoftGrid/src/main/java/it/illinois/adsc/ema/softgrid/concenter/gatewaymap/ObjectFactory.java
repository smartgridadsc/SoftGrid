
package it.illinois.adsc.ema.softgrid.concenter.gatewaymap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.illinois.adsc.ema.softgrid.concenter.gatewaymap package. 
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

    private final static QName _Gied_QNAME = new QName("", "Gied");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.illinois.adsc.ema.softgrid.concenter.gatewaymap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GiedType }
     * 
     */
    public GiedType createGiedType() {
        return new GiedType();
    }

    /**
     * Create an instance of {@link IEDType }
     * 
     */
    public IEDType createIEDType() {
        return new IEDType();
    }

    /**
     * Create an instance of {@link GatewayType }
     * 
     */
    public GatewayType createGatewayType() {
        return new GatewayType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GiedType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Gied")
    public JAXBElement<GiedType> createGied(GiedType value) {
        return new JAXBElement<GiedType>(_Gied_QNAME, GiedType.class, null, value);
    }

}
