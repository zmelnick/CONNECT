/**
 * 
 */
package gov.hhs.fha.messaging.service.decorator.cxf;

import gov.hhs.fha.messaging.service.ServiceEndpoint;
import gov.hhs.fha.messaging.service.decorator.ServiceEndpointDecorator;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * @author bhumphrey
 * @param <T>
 * 
 */
public class TLSClientServiceEndpointDecorator<T> extends ServiceEndpointDecorator<T> {

    /**
     * @param decoratored
     * @param assertion 
     * @param url 
     */
    public TLSClientServiceEndpointDecorator(ServiceEndpoint<T> decoratoredEndpoint) {
        super(decoratoredEndpoint);
    }

    @Override
    public void configure() {

        super.configure();
        Client client = ClientProxy.getClient(getPort());
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsCP = TLSClientParametersFactory.getInstance().getTLSClientParameters();
        conduit.setTlsClientParameters(tlsCP);
    }

}
