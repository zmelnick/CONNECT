/**
 * 
 */
package gov.hhs.fha.messaging;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

/**
 * @author bhumphrey
 * @param <T>
 * 
 */
public class ApacheCXFServiceEndpointDecorator<T> extends ServiceEndpointDecorator<T> {

    /**
     * @param decoratored
     * @param assertion 
     * @param url 
     */
    public ApacheCXFServiceEndpointDecorator(ServiceEndpointBuilder<T> decoratored, AssertionType assertion, String url) {
        super(new URLServiceEndpointDecorator<T>(new SAMLServiceEndpointDecorator<T>(decoratored, assertion), url));
    }

    @Override
    public T build() {

        T port = super.build();
        Client client = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsCP = TLSClientParametersFactory.getInstance().getTLSClientParameters();
        conduit.setTlsClientParameters(tlsCP);

        return port;
    }

}
