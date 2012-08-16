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
package gov.hhs.fha.nhinc.adapter.commondatalayer.mappers.constants;

import java.util.Properties;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author A22387
 */
public class AdapterCommonDataLayerConstants {
    private static Log log = LogFactory.getLog(AdapterCommonDataLayerConstants.class);

    public static final String ADAPTER_PROPERTIES_FILENAME = "adapter_common_datalayer";

    // static data switches
    public static final String ALLERGIES_TEST;
    public static final String PROBLEMS_TEST;
    public static final String PATIENT_INFO_TEST;
    public static final String MEDICATIONS_TEST;

    // Added by FHA for successful build
    public static final String EMULATOR_ALLERGIES_TAG = "ALLERGIES";
    public static final String EMULATOR_MEDS_TAG = "MEDS";
    public static final String EMULATOR_PATIENT_INFO_TAG = "PATIENT_INFO";
    public static final String EMULATOR_PROBLEMS_TAG = "PROBLEMS";
    public static final String EMULATOR_ALLERGIES_RESPONSE_TYPE = "CareRecordQUPCIN043200UV01Response";
    public static final String EMULATOR_MEDS_RESPONSE_TYPE = "CareRecordQUPCIN043200UV01Response";
    public static final String EMULATOR_PATIENT_INFO_RESPONSE_TYPE = "PatientDemographicsPRPAMT201303UV02Response";
    public static final String EMULATOR_PROBLEMS_RESPONSE_TYPE = "CareRecordQUPCIN043200UV01Response";
    public static final String EMULATOR_DATA_LOCATION;
    public static final String EMULATOR_FIND_PATIENTS_TAG = "FIND_PATIENTS";
    public static final String EMULATOR_FIND_PATIENTS_RESPONSE_TYPE = "FindPatientsPRPAMT201310UVResponse";
    public static final String EMULATOR_NO_LAST_NAME_LABEL = "UnknownLastName";
    public static final String EMULATOR_NO_FIRST_NAME_LABEL = "UnknownFirstName";
    public static final String EMULATOR_NO_GENDER_LABEL = "UnknowGender";
    public static final String EMULATOR_NO_DOB_LABEL = "UnknowDOB";
    public static final String EMULATOR_NO_PATIENT_ID_LABEL = "UnknownPatientID";

    static {

        String sEMULATOR_DATA_LOCATION = null;

        // static data switches
        String sALLERGIES_TEST = null;
        String sPROBLEMS_TEST = null;
        String sPATIENT_INFO_TEST = null;
        String sMEDICATIONS_TEST = null;

        try {

            PropertyAccessor propertyAccessor = PropertyAccessor.getInstance();
            
            // static data switches
            sALLERGIES_TEST = propertyAccessor.getProperty(ADAPTER_PROPERTIES_FILENAME, "allergies_test");
            sPROBLEMS_TEST = propertyAccessor.getProperty(ADAPTER_PROPERTIES_FILENAME, "problems_test");
            sMEDICATIONS_TEST = propertyAccessor.getProperty(ADAPTER_PROPERTIES_FILENAME, "medications_test");
            sPATIENT_INFO_TEST = propertyAccessor.getProperty(ADAPTER_PROPERTIES_FILENAME, "patient_info_test");

            sEMULATOR_DATA_LOCATION = propertyAccessor.getProperty(ADAPTER_PROPERTIES_FILENAME,
                    "emulator_data_location");
        } catch (Exception e) {
            e.printStackTrace();
        }

        EMULATOR_DATA_LOCATION = sEMULATOR_DATA_LOCATION;
        log.debug("EMULATOR_DATA_LOCATION " + EMULATOR_DATA_LOCATION);
        // static data tests
        ALLERGIES_TEST = sALLERGIES_TEST;
        PROBLEMS_TEST = sPROBLEMS_TEST;
        MEDICATIONS_TEST = sMEDICATIONS_TEST;
        PATIENT_INFO_TEST = sPATIENT_INFO_TEST;
    }
}