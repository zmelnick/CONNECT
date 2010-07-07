/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.adapter.patientdiscovery.async.response;

import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.saml.extraction.SamlTokenExtractor;
import gov.hhs.fha.nhinc.transform.subdisc.HL7AckTransforms;
import javax.xml.ws.WebServiceContext;
import org.hl7.v3.II;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.RespondingGatewayPRPAIN201306UV02RequestType;
import org.hl7.v3.RespondingGatewayPRPAIN201306UV02SecuredRequestType;

/**
 *
 * @author JHOPPESC
 */
public class AdapterPatientDiscoverySecuredAsyncRespImpl {

    public MCCIIN000002UV01 processPatientDiscoveryAsyncResp(RespondingGatewayPRPAIN201306UV02SecuredRequestType request, WebServiceContext context) {
        MCCIIN000002UV01 ack = new MCCIIN000002UV01();
        RespondingGatewayPRPAIN201306UV02RequestType unsecureRequest = new RespondingGatewayPRPAIN201306UV02RequestType();
        unsecureRequest.setNhinTargetCommunities(request.getNhinTargetCommunities());
        unsecureRequest.setPRPAIN201306UV02(request.getPRPAIN201306UV02());
        unsecureRequest.setAssertion(SamlTokenExtractor.GetAssertion(context));

        ack = processPatientDiscoveryAsyncReq(unsecureRequest);

        return ack;
    }

    public MCCIIN000002UV01 processPatientDiscoveryAsyncReq(RespondingGatewayPRPAIN201306UV02RequestType request) {
        MCCIIN000002UV01 ack = new MCCIIN000002UV01();
        String msgText = "Success";

        ack = createAck(request.getPRPAIN201306UV02(), msgText);

        return ack;
    }

    private MCCIIN000002UV01 createAck(PRPAIN201306UV02 request, String ackMsgText) {
        MCCIIN000002UV01 ack = new MCCIIN000002UV01();
        II msgId = new II();
        String senderOID = null;
        String receiverOID = null;

        if (request != null) {
            // Extract the message id
            if (request.getId() != null) {
                msgId = request.getId();
            }

            // Set the sender OID to the receiver OID from the original message
            if (NullChecker.isNotNullish(request.getReceiver()) &&
                    request.getReceiver().get(0) != null &&
                    request.getReceiver().get(0).getDevice() != null &&
                    request.getReceiver().get(0).getDevice().getAsAgent() != null &&
                    request.getReceiver().get(0).getDevice().getAsAgent().getValue() != null &&
                    request.getReceiver().get(0).getDevice().getAsAgent().getValue().getRepresentedOrganization() != null &&
                    request.getReceiver().get(0).getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue() != null &&
                    NullChecker.isNotNullish(request.getReceiver().get(0).getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId()) &&
                    request.getReceiver().get(0).getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId().get(0) != null &&
                    NullChecker.isNotNullish(request.getReceiver().get(0).getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId().get(0).getRoot())) {
                senderOID = request.getReceiver().get(0).getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId().get(0).getRoot();
            }

            // Set the receiver OID to the sender OID from the original message
            if (request.getSender() != null &&
                    request.getSender().getDevice() != null &&
                    request.getSender().getDevice().getAsAgent() != null &&
                    request.getSender().getDevice().getAsAgent().getValue() != null &&
                    request.getSender().getDevice().getAsAgent().getValue().getRepresentedOrganization() != null &&
                    request.getSender().getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue() != null &&
                    NullChecker.isNotNullish(request.getSender().getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId()) &&
                    request.getSender().getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId().get(0) != null &&
                    NullChecker.isNotNullish(request.getSender().getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId().get(0).getRoot())) {
                receiverOID = request.getSender().getDevice().getAsAgent().getValue().getRepresentedOrganization().getValue().getId().get(0).getRoot();
            }

            // Create the ack message
            ack = HL7AckTransforms.createAckMessage(null, msgId, ackMsgText, senderOID, receiverOID);
        }

        return ack;
    }

}