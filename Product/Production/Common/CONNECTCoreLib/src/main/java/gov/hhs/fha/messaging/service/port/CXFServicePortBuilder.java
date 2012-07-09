/**
 * 
 */
package gov.hhs.fha.messaging.service.port;


import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * @author akong
 *
 */
public class CXFServicePortBuilder<T> implements ServicePortBuilder<T> {

    private Class<T> serviceEndpointClass; 
     
    public CXFServicePortBuilder(ServicePortDescriptor<T> portDescriptor) {
        super();
        this.serviceEndpointClass = portDescriptor.getPortClass();
    }
    
    @SuppressWarnings("unchecked")
    public T createPort() {
               
        JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
        clientFactory.setServiceClass(serviceEndpointClass);
        clientFactory.setBindingId("http://www.w3.org/2003/05/soap/bindings/HTTP/");
                
        return (T) clientFactory.create();
    }
}
