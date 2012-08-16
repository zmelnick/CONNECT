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
package gov.hhs.fha.nhinc.adaptercomponentmpi;

import gov.hhs.fha.nhinc.adapter.commondatalayer.CommonDataLayerService;
import gov.hhs.fha.nhinc.adapter.commondatalayer.CommonDataLayerPortType;
import gov.hhs.fha.nhinc.adaptercomponentmpi.hl7parsers.HL7Parser201305;
import gov.hhs.fha.nhinc.adaptercomponentmpi.hl7parsers.HL7Parser201306;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerCache;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.v3.*;

/**
 *
 * @author Jon Hoppesch
 */
public class PatientChecker {

    private static Log log = LogFactory.getLog(PatientChecker.class);

    private static final String GATEWAY_PROPERTY_FILE = "gateway";
    private static final String HOME_COMMUNITY_ID_PROPERTY = "localHomeCommunityId";
    private static final String SERVICE_NAME_COMMON_DATA_LAYER_SERVICE = "adaptercommondatalayerservice";

    public static PRPAIN201306UV02 FindPatient(PRPAIN201305UV02 query) {
        log.debug("Entering PatientChecker.FindPatient method...");
        // declare request and response
        PRPAIN201306UV02 result = null;
        FindPatientsPRPAIN201305UV02RequestType request = null;
        FindPatientsPRPAMT201310UV02ResponseType findPatientsResponse = null;

        if (HL7Parser201305.ExtractHL7QueryParamsFromMessage(query) == null) {
            log.error("no query parameters were supplied");
        } else {
            // Get the Home community ID for this box...
            // ------------------------------------------
            String sHomeCommunityId = "";
            String sEndpointURL = "";
            try {
                sHomeCommunityId = PropertyAccessor.getInstance().getProperty(GATEWAY_PROPERTY_FILE, HOME_COMMUNITY_ID_PROPERTY);
            } catch (Exception e) {
                log.error("Failed to read " + HOME_COMMUNITY_ID_PROPERTY + " property from the "
                        + GATEWAY_PROPERTY_FILE + ".properties  file.  Error: " + e.getMessage(), e);
            }

            // Get the endpoint URL for the common data layer service
            // ------------------------------------------
            CommonDataLayerService service = new CommonDataLayerService();
            CommonDataLayerPortType port = service.getCommonDataLayerPort();

            if ((sHomeCommunityId != null) && (sHomeCommunityId.length() > 0)) {
                try {
                    sEndpointURL = ConnectionManagerCache.getInstance().getDefaultEndpointURLByServiceName(sHomeCommunityId,
                            SERVICE_NAME_COMMON_DATA_LAYER_SERVICE);
                } catch (Exception e) {
                    log.error("Failed to retrieve endpoint URL for service:" + SERVICE_NAME_COMMON_DATA_LAYER_SERVICE
                            + " from connection manager.  Error: " + e.getMessage(), e);
                }
            }

            if ((sEndpointURL != null) && (sEndpointURL.length() > 0)) {
                ((javax.xml.ws.BindingProvider) port).getRequestContext().put(
                        javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, sEndpointURL);
            } else {
                // Just a way to cover ourselves for the time being... - assume
                // port 8080
                // -------------------------------------------------------------------------
                ((javax.xml.ws.BindingProvider) port).getRequestContext().put(
                        javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                        "http://localhost:8080/CommonDataLayerService/AdapterCommonDataLayer");

                log.warn("Did not find endpoint URL for service: " + SERVICE_NAME_COMMON_DATA_LAYER_SERVICE + " and "
                        + "Home Community: " + sHomeCommunityId + ".  Using default URL: "
                        + "'http://localhost:8080/CommonDataLayerService/AdapterCommonDataLayer'");
            }

            // message
            PRPAIN201305UV02MCCIMT000100UV01Message patientMessage = new org.hl7.v3.PRPAIN201305UV02MCCIMT000100UV01Message();
            patientMessage.setControlActProcess(query.getControlActProcess());

            // request and query
            log.info("perform patient lookup via common data layer service");
            request = new org.hl7.v3.FindPatientsPRPAIN201305UV02RequestType();
            request.setQuery(patientMessage);
            findPatientsResponse = port.findPatients(request);

            if (findPatientsResponse != null) {

                if ((findPatientsResponse.getSubject() == null) || findPatientsResponse.getSubject().isEmpty()
                        || (findPatientsResponse.getSubject().size() == 0)) {
                    log.info("patient not found in MPI");
                    result = null;
                } else if (findPatientsResponse.getSubject().size() > 1) {
                    log.info("multiple patients found in MPI [findPatientsResponse.getSubject().size()="
                            + findPatientsResponse.getSubject().size() + "]");
                    result = null;
                } else {
                    log.info("single patient found in MPI");
                    PRPAMT201310UV02Patient searchResultPatient = findPatientsResponse.getSubject().get(0);

                    result = HL7Parser201306.BuildMessageFromMpiPatient(searchResultPatient, query);
                }
            }
        }

        log.debug("Exiting PatientChecker.FindPatient method...");
        return result;
    }
}
