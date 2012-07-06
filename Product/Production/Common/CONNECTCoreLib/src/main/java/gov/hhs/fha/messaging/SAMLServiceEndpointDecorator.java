/**
 * 
 */
package gov.hhs.fha.messaging;

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
    public SAMLServiceEndpointDecorator(ServiceEndpointBuilder<T> decoratored, AssertionType assertion) {
        super(decoratored);
        this.assertion = assertion;
    }

    @Override
    public T build() {
        T port = super.build();
        Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider)port).getRequestContext();
        requestContext.put("assertion", assertion);
        
        return port;
    }

    
    
}
