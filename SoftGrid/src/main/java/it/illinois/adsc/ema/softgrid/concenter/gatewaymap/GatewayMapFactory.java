package it.illinois.adsc.ema.softgrid.concenter.gatewaymap;

import it.illinois.adsc.ema.control.conf.PWModelType;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by prageethmahendra on 26/4/2017.
 */
public class GatewayMapFactory {
    private static GatewayMapFactory ourInstance = new GatewayMapFactory();

    public static GatewayMapFactory getInstance() {
        if (ourInstance == null) {
            ourInstance = new GatewayMapFactory();
        }
        return ourInstance;
    }

    private GatewayMapFactory() {
    }

    public ArrayList<GatewayType> loadGatewayMap() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("it.illinois.adsc.ema.softgrid.concenter.gatewaymap");
            Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
            File mappingFile = new File(".." + File.separator + "SoftGrid" + File.separator + "resources" + File.separator + "GatewayIEDmap.xml");
            if (mappingFile.exists()) {
                Object obj = jaxbMarshaller.unmarshal(mappingFile);
                if (obj != null) {
                    GiedType giedType = (GiedType) ((JAXBElement) obj).getValue();
                    return (ArrayList<GatewayType>) giedType.getGateway();
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        for (GatewayType gatewayType : GatewayMapFactory.getInstance().loadGatewayMap()) {
            System.out.println(gatewayType.getId() + " " + gatewayType.getIED().size());
        }
    }
}
