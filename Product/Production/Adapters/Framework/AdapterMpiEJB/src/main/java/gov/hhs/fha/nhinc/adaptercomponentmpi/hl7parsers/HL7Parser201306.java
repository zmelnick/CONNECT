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
package gov.hhs.fha.nhinc.adaptercomponentmpi.hl7parsers;

import gov.hhs.fha.nhinc.properties.PropertyAccessException;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import gov.hhs.fha.nhinc.transform.subdisc.HL7Constants;
import java.math.BigInteger;
import java.util.TimeZone;
import java.util.GregorianCalendar;
//import javax.xml.datatype.XMLGregorianCalendar;
//import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.v3.*;

/**
 * 
 * @author Jon Hoppesch
 */
public class HL7Parser201306 {

    private static Log log = LogFactory.getLog(HL7Parser201306.class);
    private static final String PROPERTY_FILE = "adapter";
    private static final String PROPERTY_NAME = "assigningAuthorityId";

    public static PRPAIN201306UV02 BuildMessageFromMpiPatient(PRPAMT201310UV02Patient patient, PRPAIN201305UV02 query) {
        log.debug("Entering HL7Parser201306.BuildMessageFromMpiPatient method...");

        PRPAIN201306UV02 msg = new PRPAIN201306UV02();

        // Set up message header fields
        msg.setITSVersion("XML_1.0");

        II id = new II();
        try {
            id.setRoot(PropertyAccessor.getInstance().getProperty(PROPERTY_FILE, PROPERTY_NAME));
        } catch (PropertyAccessException e) {
            log.error(
                    "PropertyAccessException - Default Assigning Authority property not defined in adapter.properties",
                    e);
            // CONNECT environment corrupt; return error response
            // return BuildMessageForError(<ERROR_CODE>, query);
        }
        id.setExtension(MessageIdGenerator.GenerateMessageId());
        msg.setId(id);

        // Set up the creation time string
        String timestamp = "";
        try {
            GregorianCalendar today = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

            timestamp = String.valueOf(today.get(GregorianCalendar.YEAR))
                    + String.valueOf(today.get(GregorianCalendar.MONTH) + 1)
                    + String.valueOf(today.get(GregorianCalendar.DAY_OF_MONTH))
                    + String.valueOf(today.get(GregorianCalendar.HOUR_OF_DAY))
                    + String.valueOf(today.get(GregorianCalendar.MINUTE))
                    + String.valueOf(today.get(GregorianCalendar.SECOND));
        } catch (Exception e) {
            log.error("Exception when creating XMLGregorian Date");
            log.error(" message: " + e.getMessage());
        }

        TSExplicit creationTime = new TSExplicit();
        creationTime.setValue(timestamp);
        msg.setCreationTime(creationTime);

        II interactionId = new II();
        interactionId.setRoot("2.16.840.1.113883.1.6");
        interactionId.setExtension("PRPA_IN201306UV02");
        msg.setInteractionId(interactionId);

        CS processingCode = new CS();
        processingCode.setCode("P");
        msg.setProcessingCode(processingCode);

        CS processingModeCode = new CS();
        processingModeCode.setCode("T");
        msg.setProcessingModeCode(processingModeCode);

        CS ackCode = new CS();
        ackCode.setCode("NE");
        msg.setAcceptAckCode(ackCode);

        msg.getAcknowledgement().add(createAck(query));

        // Set the receiver and sender
        msg.getReceiver().add(createReceiver(query.getSender()));
        msg.setSender(createSender(query.getReceiver().get(0)));

        msg.setControlActProcess(createControlActProcess(patient, query));

        log.debug("Exiting HL7Parser201306.BuildMessageFromMpiPatient method...");
        return msg;
    }

    private static PRPAIN201306UV02MFMIMT700711UV01ControlActProcess createControlActProcess(
            PRPAMT201310UV02Patient patient, PRPAIN201305UV02 query) {
        PRPAIN201306UV02MFMIMT700711UV01ControlActProcess controlActProcess = new PRPAIN201306UV02MFMIMT700711UV01ControlActProcess();

        controlActProcess.setMoodCode(XActMoodIntentEvent.EVN);

        CD code = new CD();
        code.setCode("PRPA_TE201306UV");
        code.setCodeSystem("2.16.840.1.113883.1.6");
        controlActProcess.setCode(code);

        controlActProcess.getSubject().add(createSubject(patient, query));

        controlActProcess.setQueryAck(createQueryAck(query));

        // Set original QueryByParameter in response
        controlActProcess.setQueryByParameter(query.getControlActProcess().getQueryByParameter());

        return controlActProcess;
    }

    private static MFMIMT700711UV01QueryAck createQueryAck(PRPAIN201305UV02 query) {
        MFMIMT700711UV01QueryAck result = new MFMIMT700711UV01QueryAck();

        if (query.getControlActProcess() != null && query.getControlActProcess().getQueryByParameter() != null
                && query.getControlActProcess().getQueryByParameter().getValue() != null
                && query.getControlActProcess().getQueryByParameter().getValue().getQueryId() != null) {
            result.setQueryId(query.getControlActProcess().getQueryByParameter().getValue().getQueryId());
        }

        CS respCode = new CS();
        respCode.setCode("OK");
        result.setQueryResponseCode(respCode);

        // INT totalQuanity = new INT();
        // totalQuanity.setValue(BigInteger.valueOf(1));
        // result.setResultTotalQuantity(totalQuanity);

        // INT currQuanity = new INT();
        // currQuanity.setValue(BigInteger.valueOf(1));
        // result.setResultCurrentQuantity(currQuanity);

        // INT remainQuanity = new INT();
        // remainQuanity.setValue(BigInteger.valueOf(0));
        // result.setResultRemainingQuantity(remainQuanity);

        return result;
    }

    private static PRPAIN201306UV02MFMIMT700711UV01Subject1 createSubject(PRPAMT201310UV02Patient patient,
            PRPAIN201305UV02 query) {
        PRPAIN201306UV02MFMIMT700711UV01Subject1 subject = new PRPAIN201306UV02MFMIMT700711UV01Subject1();

        subject.getTypeCode().add("SUBJ");

        subject.setRegistrationEvent(createRegEvent(patient, query));

        return subject;
    }

    private static PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent createRegEvent(PRPAMT201310UV02Patient patient,
            PRPAIN201305UV02 query) {
        PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent regEvent = new PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent();

        II id = new II();
        id.getNullFlavor().add("NA");
        regEvent.getId().add(id);

        CS statusCode = new CS();
        statusCode.setCode("active");

        regEvent.setStatusCode(statusCode);

        regEvent.setSubject1(createSubject1(patient, query));

        regEvent.setCustodian(createCustodian(patient));

        return regEvent;
    }

    private static MFMIMT700711UV01Custodian createCustodian(PRPAMT201310UV02Patient patient) {
        MFMIMT700711UV01Custodian result = new MFMIMT700711UV01Custodian();

        result.setAssignedEntity(createAssignEntity(patient));

        return result;
    }

    private static COCTMT090003UV01AssignedEntity createAssignEntity(PRPAMT201310UV02Patient patient) {
        COCTMT090003UV01AssignedEntity assignedEntity = new COCTMT090003UV01AssignedEntity();
        assignedEntity.setClassCode(HL7Constants.ASSIGNED_DEVICE_CLASS_CODE);
        II id = new II();
        id.setRoot(patient.getId().get(0).getRoot());
        assignedEntity.getId().add(id);
        CE ce = new CE();
        ce.setCode("NotHealthDataLocator");
        ce.setCodeSystem("1.3.6.1.4.1.19376.1.2.27.2");
        assignedEntity.setCode(ce);

        return assignedEntity;
    }

    private static PRPAIN201306UV02MFMIMT700711UV01Subject2 createSubject1(PRPAMT201310UV02Patient patient,
            PRPAIN201305UV02 query) {
        PRPAIN201306UV02MFMIMT700711UV01Subject2 subject = new PRPAIN201306UV02MFMIMT700711UV01Subject2();

        // Add in patient
        subject.setPatient(patient);

        return subject;
    }

    private static MCCIMT000300UV01Acknowledgement createAck(PRPAIN201305UV02 query) {
        MCCIMT000300UV01Acknowledgement ack = new MCCIMT000300UV01Acknowledgement();
        ack.setTypeId(query.getInteractionId());

        CS typeCode = new CS();
        typeCode.setCode("AA");

        ack.setTypeCode(typeCode);

        return ack;
    }

    private static MCCIMT000300UV01Receiver createReceiver(MCCIMT000100UV01Sender querySender) {
        MCCIMT000300UV01Receiver receiver = new MCCIMT000300UV01Receiver();

        receiver.setTypeCode(CommunicationFunctionType.RCV);

        MCCIMT000300UV01Device device = new MCCIMT000300UV01Device();
        device.setDeterminerCode(HL7Constants.RECEIVER_DETERMINER_CODE);
        device.setClassCode(EntityClassDevice.DEV);

        if (querySender.getDevice() != null && querySender.getDevice().getId() != null
                && querySender.getDevice().getId().size() > 0 && querySender.getDevice().getId().get(0) != null) {
            device.getId().add(querySender.getDevice().getId().get(0));
        }

        receiver.setDevice(device);

        return receiver;
    }

    private static MCCIMT000300UV01Sender createSender(MCCIMT000100UV01Receiver queryReceiver) {
        MCCIMT000300UV01Sender sender = new MCCIMT000300UV01Sender();

        sender.setTypeCode(CommunicationFunctionType.SND);

        MCCIMT000300UV01Device device = new MCCIMT000300UV01Device();
        device.setDeterminerCode(HL7Constants.RECEIVER_DETERMINER_CODE);
        device.setClassCode(EntityClassDevice.DEV);

        if (queryReceiver.getDevice() != null && queryReceiver.getDevice().getId() != null
                && queryReceiver.getDevice().getId().size() > 0 && queryReceiver.getDevice().getId().get(0) != null) {
            device.getId().add(queryReceiver.getDevice().getId().get(0));
        }

        sender.setDevice(device);

        return sender;
    }

}
