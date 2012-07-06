/**
 * 
 */
package gov.hhs.fha.messaging;

import gov.hhs.fha.nhinc.callback.openSAML.CertificateManager;
import gov.hhs.fha.nhinc.callback.openSAML.CertificateManagerImpl;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.cxf.configuration.jsse.TLSClientParameters;

/**
 * @author bhumphrey
 *
 */
public class TLSClientParametersFactory {
    
    static private TLSClientParametersFactory INSTANCE = null;
    private KeyManagerFactory keyFactory;
    private TrustManagerFactory trustFactory; 
    
    private TLSClientParametersFactory() {
        this(CertificateManagerImpl.getInstance(), System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
    }
    
    TLSClientParametersFactory(CertificateManager cm, char[] password) {
        try {
            keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            
            keyFactory.init(cm.getKeyStore(), password);
            
            trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustFactory.init(cm.getTrustStore());
        } catch (UnrecoverableKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
       
    }
    
    public static TLSClientParametersFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TLSClientParametersFactory();
        }
        return INSTANCE;
    }
    
    public TLSClientParameters getTLSClientParameters() {
        TLSClientParameters tlsCP = new TLSClientParameters();
        tlsCP.setDisableCNCheck(true);
        tlsCP.setKeyManagers(keyFactory.getKeyManagers()); 
        tlsCP.setTrustManagers(trustFactory.getTrustManagers());
        return tlsCP;
    }

}
