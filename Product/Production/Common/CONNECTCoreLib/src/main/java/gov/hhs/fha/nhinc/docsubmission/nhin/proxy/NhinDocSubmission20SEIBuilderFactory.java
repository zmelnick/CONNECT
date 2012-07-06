/**
 * 
 */
package gov.hhs.fha.nhinc.docsubmission.nhin.proxy;

/**
 * @author bhumphrey
 *
 */
public class NhinDocSubmission20SEIBuilderFactory extends AbstractSEIBuilderFactory {

    @Override
    protected String getWSDLFileName() {
        return "NhinXDR20.wsdl";
    }

}
