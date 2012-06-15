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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;

import gov.hhs.fha.nhinc.callback.openSAML.CertificateManager;
import gov.hhs.fha.nhinc.callback.openSAML.CertificateManagerImpl;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayProvideAndRegisterDocumentSetRequestType;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerCache;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRPortType;
import gov.hhs.fha.nhinc.nhincentityxdr.EntityXDRService;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants.GATEWAY_API_LEVEL;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.aegis.type.mtom.StreamDataSource;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ihe.iti.xdr._2007.DocumentRepositoryXDRPortType;

import javax.activation.DataHandler;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;

/**
 * 
 * @author dunnek
 */
public class NhinDocSubmissionProxyWebServiceSecuredStream implements NhinDocSubmissionProxy {
    private Log log = null;
    private static HashMap<String, Service> cachedServiceMap = new HashMap<String, Service>();
    private static final String NAMESPACE_URI = "urn:ihe:iti:xdr:2007";
    private static final String SERVICE_LOCAL_PART = "DocumentRepositoryXDR_Service";
    private static final String PORT_LOCAL_PART = "DocumentRepositoryXDR_Port_Soap";
    private static final String WSDL_FILE_G0 = "NhinXDR.wsdl";
    private static final String WSDL_FILE_G1 = "NhinXDR20.wsdl";
    private static final String WS_ADDRESSING_ACTION_G0 = "urn:ihe:iti:xdr:2007:ProvideAndRegisterDocumentSet-b";
    private static final String WS_ADDRESSING_ACTION_G1 = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
    private WebServiceProxyHelper proxyHelper = null;

    public NhinDocSubmissionProxyWebServiceSecuredStream() {
        log = createLogger();
        proxyHelper = createWebServiceProxyHelper();
    }

    protected Log createLogger() {
        return LogFactory.getLog(getClass());
    }

    protected WebServiceProxyHelper createWebServiceProxyHelper() {
        return new WebServiceProxyHelper();
    }

    protected void initializeSecurePort(DocumentRepositoryXDRPortType port, String url, String wsAddressingAction,
            AssertionType assertion) {
        proxyHelper.initializeSecurePort((javax.xml.ws.BindingProvider) port, url, NhincConstants.XDR_ACTION,
                wsAddressingAction, assertion);
    }

    /**
     * This method retrieves and initializes the port.
     * 
     * @param url The URL for the web service.
     * @return The port object for the web service.
     */
    protected DocumentRepositoryXDRPortType getPort(String url, AssertionType assertion,
            NhincConstants.GATEWAY_API_LEVEL apiLevel) {
        DocumentRepositoryXDRPortType port = null;
        Service service;
        String wsAddressingAction;
        switch (apiLevel) {
        case LEVEL_g0:
            service = getService(WSDL_FILE_G0);
            wsAddressingAction = WS_ADDRESSING_ACTION_G0;
            break;
        case LEVEL_g1:
            service = getService(WSDL_FILE_G1);
            wsAddressingAction = WS_ADDRESSING_ACTION_G1;
            break;
        default:
            service = null;
            wsAddressingAction = null;
        }

        if (service != null) {
            log.debug("Obtained service - creating port.");

            //port = service.getPort(new QName(NAMESPACE_URI, PORT_LOCAL_PART), DocumentRepositoryXDRPortType.class);
            port = getCXFPort(assertion);
            
            initializeSecurePort(port, url, wsAddressingAction, assertion);
        } else {
            log.error("Unable to obtain service - no port created.");
        }
        return port;
    }

    /**
     * Retrieve the service class for this web service.
     * 
     * @return The service class for this web service.
     */
    protected Service getService(String wsdl) {
        Service cachedService = cachedServiceMap.get(wsdl);
        if (cachedService == null) {
            try {
                cachedService = proxyHelper.createService(wsdl, NAMESPACE_URI, SERVICE_LOCAL_PART);
                cachedServiceMap.put(wsdl, cachedService);
            } catch (Throwable t) {
                log.error("Error creating service: " + t.getMessage(), t);
            }
        }
        return cachedService;
    }

    public RegistryResponseType provideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType request,
            AssertionType assertion, NhinTargetSystemType targetSystem, NhincConstants.GATEWAY_API_LEVEL apiLevel) {
        log.debug("Begin provideAndRegisterDocumentSetB");
        RegistryResponseType response = new RegistryResponseType();

        try {
              String url = proxyHelper.getUrlFromTargetSystemByGatewayAPILevel(targetSystem,
                    NhincConstants.NHINC_XDR_SERVICE_NAME, apiLevel);
            DocumentRepositoryXDRPortType port = getPort(url, assertion, apiLevel);
                        
            response = sendBigFile(port);
            
//            response = port.documentRepositoryProvideAndRegisterDocumentSetB(request);
//            if (request == null) {
//                log.error("Message was null");
//            } else if (port == null) {
//                log.error("port was null");
//            } else {
//                response = (RegistryResponseType) proxyHelper.invokePort(port, DocumentRepositoryXDRPortType.class,
//                        "documentRepositoryProvideAndRegisterDocumentSetB", request);
//            }
        } catch (Exception ex) {
            log.error("Error calling documentRepositoryProvideAndRegisterDocumentSetB: " + ex.getMessage(), ex);
        }

        log.debug("End provideAndRegisterDocumentSetB");
        return response;

    }

//    private RegistryResponseType sendBigFile() throws Exception {
//        
//        QName SERVICE_NAME = new QName("urn:gov:hhs:fha:nhinc:nhincentityxdr", "EntityXDR_Service");
//        
//        URL wsdlURL = new File("/Users/akong/Workspace/connect/CONNECT/Product/Production/Common/Interfaces/src/wsdl/EntityXDR.wsdl").toURI().toURL();
//
//        EntityXDRService ss = new EntityXDRService(wsdlURL, SERVICE_NAME);
//        EntityXDRPortType port = ss.getEntityXDRPort(new MTOMFeature(true));
//
//        BindingProvider provider = (BindingProvider) port;
//        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
//                "http://localhost:8080/Gateway/DocumentSubmission/2_0/EntityService/EntityDocSubmissionUnsecured");
//                
//        Client client = ClientProxy.getClient(port);
//        HTTPConduit http = (HTTPConduit) client.getConduit();
//
//        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
//        httpClientPolicy.setConnectionTimeout(36000);
//        httpClientPolicy.setAllowChunking(true);
//        httpClientPolicy.setReceiveTimeout(32000);
//
//        http.setClient(httpClientPolicy);
//        
//        System.out.println("Invoking provideAndRegisterDocumentSetB...");
//        RespondingGatewayProvideAndRegisterDocumentSetRequestType request = new RespondingGatewayProvideAndRegisterDocumentSetRequestType();
//       
//        File file = new File("/Users/akong/payloads/1G.zip");
//        FileInputStream fis = new FileInputStream(file);
//        StreamDataSource sds = new StreamDataSource("application/octect-stream", fis);
//        
//        DataHandler dh = new DataHandler(sds);
//        
//        ProvideAndRegisterDocumentSetRequestType body = new ProvideAndRegisterDocumentSetRequestType();
//        ProvideAndRegisterDocumentSetRequestType.Document doc = new ProvideAndRegisterDocumentSetRequestType.Document();
//        doc.setId("123");
//        doc.setValue(dh);
//        body.getDocument().add(doc);
//        
//        request.setProvideAndRegisterDocumentSetRequest(body);
//
//        RegistryResponseType response = port.provideAndRegisterDocumentSetB(request);
//                
//        return response;
//    }
    
    private RegistryResponseType sendBigFile(DocumentRepositoryXDRPortType port) throws Exception {
         
        File file = new File("/Users/akong/payloads/1G.zip");
        FileInputStream fis = new FileInputStream(file);
        StreamDataSource sds = new StreamDataSource("application/octect-stream", fis);

        DataHandler dh = new DataHandler(sds);

        ProvideAndRegisterDocumentSetRequestType body = new ProvideAndRegisterDocumentSetRequestType();
        ProvideAndRegisterDocumentSetRequestType.Document doc = new ProvideAndRegisterDocumentSetRequestType.Document();
        doc.setId("123");
        doc.setValue(dh);
        body.getDocument().add(doc);

        RegistryResponseType response = port.documentRepositoryProvideAndRegisterDocumentSetB(body);
        
        return response;
    }
    
    
    private DocumentRepositoryXDRPortType getCXFPort(AssertionType assertion) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "DocumentSubmission_20-client-beans.xml" });
        DocumentRepositoryXDRPortType port = (DocumentRepositoryXDRPortType)context.getBean("documentSubmissionPortType");

        HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(port).getConduit();
        TLSClientParameters tlsCP = new TLSClientParameters();
        tlsCP.setDisableCNCheck(true);
        
        CertificateManager cm = CertificateManagerImpl.getInstance();
        
        try {
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            String password = System.getProperty("javax.net.ssl.keyStorePassword");
            keyFactory.init(cm.getKeyStore(), password.toCharArray()); 
            KeyManager[] km = keyFactory.getKeyManagers(); 
            tlsCP.setKeyManagers(km); 
            
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); 
            trustFactory.init(cm.getTrustStore()); 
            TrustManager[] tm = trustFactory.getTrustManagers(); 
            tlsCP.setTrustManagers(tm); 
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        httpConduit.setTlsClientParameters(tlsCP);
        
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(36000);
        httpClientPolicy.setAllowChunking(true);
        httpClientPolicy.setReceiveTimeout(32000);

        httpConduit.setClient(httpClientPolicy);

        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put("assertion", assertion);
        
        return port;
    }
    
    
}
