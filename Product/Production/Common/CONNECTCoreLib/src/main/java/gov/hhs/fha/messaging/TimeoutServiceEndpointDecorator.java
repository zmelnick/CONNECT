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
public class TimeoutServiceEndpointDecorator<T> extends ServiceEndpointDecorator<T> {

    public static final String KEY_CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    public static final String KEY_REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
    private String connectTimeout;
    private String requestTimeout;
    
    /**
     * @param decoratored
     */
    public TimeoutServiceEndpointDecorator(ServiceEndpointBuilder<T> decoratored, String connectTimeout, String requestTimeout) {
        super(decoratored);
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
    }

    @Override
    public T build() {
        T port =  super.build();
        Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider)port).getRequestContext();
        requestContext.put(KEY_CONNECT_TIMEOUT, connectTimeout);
        requestContext.put(KEY_REQUEST_TIMEOUT, requestTimeout);

        
        return port;
    }
    
    

}
