/**
 * 
 */
package gov.hhs.fha.nhinc.docsubmission.nhin.proxy;

import gov.hhs.fha.messaging.ServiceEndpointBuilder;
import gov.hhs.fha.messaging.SeviceEndpointBuilderFactory;
import gov.hhs.fha.messaging.WebServiceProxyHelperServiceEndpointBuilder;
import ihe.iti.xdr._2007.DocumentRepositoryXDRPortType;

/**
 * @author bhumphrey
 * 
 */
public abstract class AbstractSEIBuilderFactory implements SeviceEndpointBuilderFactory<DocumentRepositoryXDRPortType> {

    private static final String NAMESPACE_URI = "urn:ihe:iti:xdr:2007";
    private static final String SERVICE_LOCAL_PART = "DocumentRepositoryXDR_Service";
    private static final String PORT_LOCAL_PART = "DocumentRepositoryXDR_Port_Soap";
   
    @Override
    public ServiceEndpointBuilder<DocumentRepositoryXDRPortType> getServiceEndpointBuilder() {

        ServiceEndpointBuilder<DocumentRepositoryXDRPortType> wsphEndpointBuilder = new WebServiceProxyHelperServiceEndpointBuilder<DocumentRepositoryXDRPortType>(
                getWSDLFileName(), NAMESPACE_URI, SERVICE_LOCAL_PART, PORT_LOCAL_PART);
        return wsphEndpointBuilder;
    }

    /**
     * @return
     */
    abstract protected String getWSDLFileName();

    

}
