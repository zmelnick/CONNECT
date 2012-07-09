/**
 * 
 */
package gov.hhs.fha.messaging.service.port;

import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;

import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author bhumphrey
 *
 */
public class MetroServicePortBuilder<T> implements ServicePortBuilder<T> {

    private static HashMap<String, Service> cachedServiceMap = new HashMap<String, Service>();
    private static Log log = LogFactory.getLog(MetroServicePortBuilder.class);
    
    private WebServiceProxyHelper proxyHelper = null;
    private String namespaceURI;
    private String serviceLocalPart;
    private String portLocalPart;
    private String wsdlFile;
    private Class<T> serviceEndpointClass; 
     
    public MetroServicePortBuilder(ServicePortDescriptor<T> portDescriptor) {
        super();
        this.namespaceURI = portDescriptor.getNamespaceUri();
        this.serviceLocalPart = portDescriptor.getServiceLocalPart();
        this.portLocalPart = portDescriptor.getPortLocalPart();
        this.wsdlFile = portDescriptor.getWSDLFileName();
        this.serviceEndpointClass = portDescriptor.getPortClass();
    
        this.proxyHelper = new WebServiceProxyHelper();
    }
    
    public T createPort() {
        Service service = getService(wsdlFile);
        T port = (T) service.getPort(new QName(namespaceURI, portLocalPart), serviceEndpointClass);
      
        return port;
    }

    /**
     * Retrieve the service class for this web service.
     * 
     * @return The service class for this web service.
     */
    protected Service getService(String wsdlFile) {
        
        Service cachedService = cachedServiceMap.get(wsdlFile);
        if (cachedService == null) {
            try {
                cachedService = proxyHelper.createService(wsdlFile, namespaceURI, serviceLocalPart);
                cachedServiceMap.put(wsdlFile, cachedService);
            } catch (Throwable t) {
                log.error("Error creating service: " + t.getMessage(), t);
            }
        }
        return cachedService;
    }

}
