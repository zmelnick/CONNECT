/**
 *
 */
package gov.hhs.fha.nhinc.callback.openSAML;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 * @author bhumphrey
 *
 */
abstract public class SAMLAssertionBuilder {

    static final String ID_PREFIX = "_";

    static final String NHIN_NS = "http://www.hhs.gov/healthit/nhin";


    // Authorization Decision Action is always set to Execute
    static final String AUTHZ_DECISION_ACTION_EXECUTE = "Execute";

    // Valid Authorization Decision values
    static final String AUTHZ_DECISION_PERMIT = "Permit";
    private static final String AUTHZ_DECISION_DENY = "Deny";
    private static final String AUTHZ_DECISION_INDETERMINATE = "Indeterminate";
    private static final String[] VALID_AUTHZ_DECISION_ARRAY = {AUTHZ_DECISION_PERMIT, AUTHZ_DECISION_DENY,
            AUTHZ_DECISION_INDETERMINATE};
    private static final List<String> VALID_AUTHZ_DECISION_LIST = Collections.unmodifiableList(Arrays
            .asList(VALID_AUTHZ_DECISION_ARRAY));

    // Valid Name Identification values
    private static final String UNSPECIFIED_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
    private static final String EMAIL_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";
    static final String X509_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName";
    private static final String WINDOWS_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName";
    private static final String KERBEROS_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:kerberos";
    private static final String ENTITY_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:entity";
    private static final String PERSISTENT_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:persistent";
    private static final String TRANSIENT_NAME_ID = "urn:oasis:names:tc:SAML:1.1:nameid-format:transient";

    private static final String[] VALID_NAME_ID_ARRAY = {UNSPECIFIED_NAME_ID, EMAIL_NAME_ID, X509_NAME_ID,
            WINDOWS_NAME_ID, KERBEROS_NAME_ID, ENTITY_NAME_ID, PERSISTENT_NAME_ID, TRANSIENT_NAME_ID};
    private static final List<String> VALID_NAME_LIST = Collections
            .unmodifiableList(Arrays.asList(VALID_NAME_ID_ARRAY));

    static final String AUTHN_SESSION_INDEX = "123456";

    // Valid Context Class references
    private static final String INTERNET_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocol";
    private static final String INTERNET_PASSWORD_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:InternetProtocolPassword";
    private static final String PASSWORD_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:Password";
    private static final String PASSWORD_TRANS_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
    private static final String KERBEROS_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos";
    private static final String PREVIOUS_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession";
    private static final String REMOTE_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:SecureRemotePassword";
    private static final String TLS_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:TLSClient";
    static final String X509_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
    private static final String PGP_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:PGP";
    private static final String SPKI_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:SPKI";
    private static final String DIG_SIGN_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:XMLDSig";
    static final String UNSPECIFIED_AUTHN_CNTX_CLS = "urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified";
    private static final String[] VALID_AUTHN_CNTX_CLS_ARRAY = { INTERNET_AUTHN_CNTX_CLS,
            INTERNET_PASSWORD_AUTHN_CNTX_CLS, PASSWORD_AUTHN_CNTX_CLS, PASSWORD_TRANS_AUTHN_CNTX_CLS,
            KERBEROS_AUTHN_CNTX_CLS, PREVIOUS_AUTHN_CNTX_CLS, REMOTE_AUTHN_CNTX_CLS, TLS_AUTHN_CNTX_CLS,
            X509_AUTHN_CNTX_CLS, PGP_AUTHN_CNTX_CLS, SPKI_AUTHN_CNTX_CLS, DIG_SIGN_AUTHN_CNTX_CLS,
            UNSPECIFIED_AUTHN_CNTX_CLS };
    private static final List<String> VALID_AUTHN_CNTX_CLS_LIST = Collections.unmodifiableList(Arrays
            .asList(VALID_AUTHN_CNTX_CLS_ARRAY));

    static boolean isValidNameidFormat(final String format) {
        return VALID_NAME_LIST.contains(format.trim());
    }

    static boolean isValidAuthnCntxCls(final String value) {
        return VALID_AUTHN_CNTX_CLS_LIST.contains(value.trim());
    }

    static boolean isValidAuthenicationDescision(String decision) {
        return VALID_AUTHZ_DECISION_LIST.contains(decision.trim());

    }

    static boolean isValidIssuerFormat(String format) {
        return VALID_NAME_LIST.contains(format);
    }

    abstract public Element build(CallbackProperties properties) throws Exception;

}
