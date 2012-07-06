/**
 * 
 */
package gov.hhs.fha.messaging;

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
    public URLServiceEndpointDecorator(ServiceEndpointBuilder<T> decoratored, String url) {
        super(decoratored);
        this.url = url;
    }

    @Override
    public T build() {
        T port =  super.build();
        Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider)port).getRequestContext();
        requestContext.put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        
        return port;
    }
    
    

}
