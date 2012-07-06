/**
 * 
 */
package gov.hhs.fha.messaging;

import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;

import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * @author bhumphrey
 *
 */
public class WebServiceProxyHelperServiceEndpointBuilder<T> implements ServiceEndpointBuilder<T> {

    private WebServiceProxyHelper proxyHelper = null;
    private String namespaceURI;
    private String serviceLocalPart;
    private String portLocalPart;
    private String wsdlFile;
 
    @SuppressWarnings("unchecked")
    private Class<T> serviceEndpointClass = (Class<T>)
            ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    
    
    
    
    public WebServiceProxyHelperServiceEndpointBuilder(String wsdlFile, String namespaceURI,
            String serviceLocalPart, String portLocalPart) {
        super();
        this.namespaceURI = namespaceURI;
        this.serviceLocalPart = serviceLocalPart;
        this.portLocalPart = portLocalPart;
        this.wsdlFile = wsdlFile;
        this.proxyHelper = new WebServiceProxyHelper();
    }

    @Override
    public T build() {
        T port = null;
        Service service = getService(wsdlFile);
        port = (T) service.getPort(new QName(namespaceURI, portLocalPart), serviceEndpointClass);
       
        return port;
    }

   
    
    /**
     * Retrieve the service class for this web service.
     * 
     * @return The service class for this web service.
     */
    protected Service getService(String wsdlFile) {
        try {
            return proxyHelper.createService(wsdlFile, namespaceURI, serviceLocalPart);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
