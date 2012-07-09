/**
 * 
 */
package gov.hhs.fha.messaging.service.decorator;

import gov.hhs.fha.messaging.service.ServiceEndpoint;


import java.util.Map;

/**
 * @author bhumphrey
 * @param <T>
 *
 */
public class URLServiceEndpointDecorator<T> extends ServiceEndpointDecorator<T> {

    private String url;
    
    /**
     * @param decoratored
     */
    public URLServiceEndpointDecorator(ServiceEndpoint<T> decoratoredEndpoint, String url) {
        super(decoratoredEndpoint);
        this.url = url;
    }

    @Override
    public void configure() {
        super.configure();
        Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider) getPort()).getRequestContext();
        requestContext.put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
    }
}
