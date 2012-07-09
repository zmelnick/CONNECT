/**
 * 
 */
package gov.hhs.fha.messaging.service.decorator;

import gov.hhs.fha.messaging.service.ServiceEndpoint;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;

import java.util.Map;

/**
 * @author bhumphrey
 * @param <T>
 *
 */
public class SAMLServiceEndpointDecorator<T> extends ServiceEndpointDecorator<T> {

    private AssertionType assertion;
    
    /**
     * @param decoratored
     */
    public SAMLServiceEndpointDecorator(ServiceEndpoint<T> decoratoredEndpoint, AssertionType assertion) {
        super(decoratoredEndpoint);
        this.assertion = assertion;
    }

    @Override
    public void configure() {
        super.configure();
        Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider) getPort()).getRequestContext();
        requestContext.put("assertion", assertion);
    }
        
}
