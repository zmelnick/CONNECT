/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010(Year date of delivery) United States Government, as represented by the Secretary of Health and Human Services.  All rights reserved.
 *
 */
package gov.hhs.fha.nhinc.docretrieve._30.nhin;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.Addressing;

/**
 * 
 * @author Neil Webb
 */
@WebService(serviceName = "RespondingGateway_Retrieve_Service", portName = "RespondingGateway_Retrieve_Port_Soap", endpointInterface = "ihe.iti.xds_b._2007.RespondingGatewayRetrievePortType", targetNamespace = "urn:ihe:iti:xds-b:2007", wsdlLocation = "WEB-INF/wsdl/DocRetrieve/NhinDocRetrieve_g1.wsdl")
@BindingType(value = "http://www.w3.org/2003/05/soap/bindings/HTTP/")
@Addressing(enabled = true)
public class DocRetrieve {
    @Resource
    private WebServiceContext context;

    /**
     * The web service implementation for Document Retrieve.
     * @param body the message of the request
     * @return the document set of the retrieve
     */
    public ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType respondingGatewayCrossGatewayRetrieve(
            ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType body) {
        return (new DocRetrieveImpl().respondingGatewayCrossGatewayRetrieve(body, context));
    }

}
