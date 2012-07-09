/**
 * 
 */
package gov.hhs.fha.messaging.service.decorator;

import gov.hhs.fha.messaging.service.ServiceEndpoint;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;

import java.util.Map;

/**
 * @author bhumphrey
 * @param <T>
 *
 */
public class TimeoutServiceEndpointDecorator<T> extends ServiceEndpointDecorator<T> {

    public static final String KEY_CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    public static final String KEY_REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
    private int connectTimeout;
    private int requestTimeout;
    
    /**
     * @param decoratored
     */
    public TimeoutServiceEndpointDecorator(ServiceEndpoint<T> decoratoredEndpoint) {
        super(decoratoredEndpoint);
        
        WebServiceProxyHelper proxyHelper = new WebServiceProxyHelper();
        this.connectTimeout = proxyHelper.getTimeout();
        this.requestTimeout = proxyHelper.getTimeout();;
    }

    @Override
    public void configure() {
        super.configure();
        Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider)getPort()).getRequestContext();
        requestContext.put(KEY_CONNECT_TIMEOUT, connectTimeout);
        requestContext.put(KEY_REQUEST_TIMEOUT, requestTimeout);
    }
    
}
