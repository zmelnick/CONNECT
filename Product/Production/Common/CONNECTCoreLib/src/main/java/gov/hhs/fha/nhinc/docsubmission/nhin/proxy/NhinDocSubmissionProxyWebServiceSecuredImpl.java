/*
 * Copyright (c) 2012, United States Government, as represented by the Secretary of Health and Human Services. 
 * All rights reserved. 
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met: 
 *     * Redistributions of source code must retain the above 
 *       copyright notice, this list of conditions and the following disclaimer. 
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the documentation 
 *       and/or other materials provided with the distribution. 
 *     * Neither the name of the United States Government nor the 
 *       names of its contributors may be used to endorse or promote products 
 *       derived from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package gov.hhs.fha.nhinc.docsubmission.nhin.proxy;

import gov.hhs.fha.messaging.ApacheCXFServiceEndpointDecorator;
import gov.hhs.fha.messaging.SAMLServiceEndpointDecorator;
import gov.hhs.fha.messaging.ServiceEndpointBuilder;
import gov.hhs.fha.messaging.WebServiceProxyHelperServiceEndpointBuilder;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import ihe.iti.xdr._2007.DocumentRepositoryXDRPortType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.util.HashMap;

import javax.xml.ws.Service;

import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author dunnek
 */
public class NhinDocSubmissionProxyWebServiceSecuredImpl implements NhinDocSubmissionProxy {
    private Log log = null;
    private static final String WS_ADDRESSING_ACTION_G0 = "urn:ihe:iti:xdr:2007:ProvideAndRegisterDocumentSet-b";
    private static final String WS_ADDRESSING_ACTION_G1 = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
    private WebServiceProxyHelper proxyHelper = null;

    public NhinDocSubmissionProxyWebServiceSecuredImpl() {
        log = createLogger();
        proxyHelper = new WebServiceProxyHelper();
    }

    protected Log createLogger() {
        return LogFactory.getLog(getClass());
    }

   
    public ServiceEndpointBuilder<DocumentRepositoryXDRPortType> getServiceEndpointBuilder(
            NhincConstants.GATEWAY_API_LEVEL apiLevel) {
        switch (apiLevel) {
        case LEVEL_g0:
            return (new NhinDocSubmissionSEIBuilderFactory()).getServiceEndpointBuilder();
        default:
            return (new NhinDocSubmission20SEIBuilderFactory()).getServiceEndpointBuilder();
        }
    }

    public String getAction(NhincConstants.GATEWAY_API_LEVEL apiLevel) {
        String wsAddressingAction;
        switch (apiLevel) {
        case LEVEL_g0:
            wsAddressingAction = WS_ADDRESSING_ACTION_G0;
            break;
        case LEVEL_g1:
            wsAddressingAction = WS_ADDRESSING_ACTION_G1;
            break;
        default:
            wsAddressingAction = null;
        }
        return wsAddressingAction;
    }

    public RegistryResponseType provideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType request,
            AssertionType assertion, NhinTargetSystemType targetSystem, NhincConstants.GATEWAY_API_LEVEL apiLevel) {
        log.debug("Begin provideAndRegisterDocumentSetB");
        RegistryResponseType response = new RegistryResponseType();

        try {
            String url = proxyHelper.getUrlFromTargetSystemByGatewayAPILevel(targetSystem,
                    NhincConstants.NHINC_XDR_SERVICE_NAME, apiLevel);

            ServiceEndpointBuilder<DocumentRepositoryXDRPortType> seiBuilder = new ApacheCXFServiceEndpointDecorator<DocumentRepositoryXDRPortType>(
                    getServiceEndpointBuilder(apiLevel), assertion, url);

            response = seiBuilder.build().documentRepositoryProvideAndRegisterDocumentSetB(request);
          
        } catch (Exception ex) {
            log.error("Error calling documentRepositoryProvideAndRegisterDocumentSetB: " + ex.getMessage(), ex);
        }

        log.debug("End provideAndRegisterDocumentSetB");
        return response;

    }

}
