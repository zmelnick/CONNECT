/**
 * 
 */
package gov.hhs.fha.nhinc.docsubmission.nhin.proxy;

/**
 * @author bhumphrey
 *
 */
public class NhinDocSubmissionSEIBuilderFactory extends AbstractSEIBuilderFactory {

    
    
    @Override
    protected String getWSDLFileName() {
        return  "NhinXDR.wsdl";
    }

}
