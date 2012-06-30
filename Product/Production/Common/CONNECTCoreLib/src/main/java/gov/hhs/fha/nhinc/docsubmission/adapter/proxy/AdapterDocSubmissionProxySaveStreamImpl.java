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
package gov.hhs.fha.nhinc.docsubmission.adapter.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.activation.DataHandler;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docsubmission.adapter.component.XDRHelper;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author akong
 */
public class AdapterDocSubmissionProxySaveStreamImpl implements AdapterDocSubmissionProxy {
    private static Log log = LogFactory.getLog(AdapterDocSubmissionProxySaveStreamImpl.class);

    public RegistryResponseType provideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType msg,
            AssertionType assertion) {
        
        saveFileToDisk(msg);
        
        XDRHelper helper = new XDRHelper();
        return helper.createPositiveAck();        
    }

    private void saveFileToDisk(ProvideAndRegisterDocumentSetRequestType request) {
        DataHandler dh = request.getDocument().get(0).getValue();
        try {
            String fileLocation = PropertyAccessor.getInstance().getProperty("streamingDemoSaveLocation");
            
            InputStream is = dh.getInputStream();
            
            // write the inputStream to a FileOutputStream
            FileOutputStream out = new FileOutputStream(new File(fileLocation));
         
            int read = 0;
            byte[] bytes = new byte[1024];
         
            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
         
            is.close();
            out.flush();
            out.close();
            
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        
    }
    
}
