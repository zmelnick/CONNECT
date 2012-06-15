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

package gov.hhs.fha.nhinc.docsubmission.entity;

import gov.hhs.fha.nhinc.nhincentityxdr.*;

import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayProvideAndRegisterDocumentSetRequestType;

import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.cxf.aegis.type.mtom.StreamDataSource;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

/**
 * @author akong
 * 
 */
public class EntityDocSubmissionStreamTest {

    private static final QName SERVICE_NAME = new QName("urn:gov:hhs:fha:nhinc:nhincentityxdr", "EntityXDR_Service");

    @Test
    public void test() {
        
    }
    
    @Test
    @Ignore
    public void testStreaming() throws java.lang.Exception {
        
        //URL wsdlURL = EntityXDRService.WSDL_LOCATION;
        URL wsdlURL = new File("/Users/akong/Workspace/connect/CONNECT/Product/Production/Common/Interfaces/src/wsdl/EntityXDR.wsdl").toURI().toURL();

        EntityXDRService ss = new EntityXDRService(wsdlURL, SERVICE_NAME);
        EntityXDRPortType port = ss.getEntityXDRPort(new MTOMFeature(true));

        BindingProvider provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://localhost:8080/Gateway/DocumentSubmission/2_0/EntityService/EntityDocSubmissionUnsecured");
                
        Client client = ClientProxy.getClient(port);
        HTTPConduit http = (HTTPConduit) client.getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(36000);
        httpClientPolicy.setAllowChunking(true);
        httpClientPolicy.setReceiveTimeout(32000);

        http.setClient(httpClientPolicy);
        
        System.out.println("Invoking provideAndRegisterDocumentSetB...");
        RespondingGatewayProvideAndRegisterDocumentSetRequestType request = new RespondingGatewayProvideAndRegisterDocumentSetRequestType();
       
        File file = new File("/Users/akong/payloads/1G.zip");
        FileInputStream fis = new FileInputStream(file);
        StreamDataSource sds = new StreamDataSource("application/octect-stream", fis);
        
        DataHandler dh = new DataHandler(sds);
        
        ProvideAndRegisterDocumentSetRequestType body = new ProvideAndRegisterDocumentSetRequestType();
        ProvideAndRegisterDocumentSetRequestType.Document doc = new ProvideAndRegisterDocumentSetRequestType.Document();
        doc.setId("123");
        doc.setValue(dh);
        body.getDocument().add(doc);
        
        request.setProvideAndRegisterDocumentSetRequest(body);

        RegistryResponseType response = port.provideAndRegisterDocumentSetB(request);
        
        System.out.println("provideAndRegisterDocumentSetB.result=" + response.getStatus());
    }

}
