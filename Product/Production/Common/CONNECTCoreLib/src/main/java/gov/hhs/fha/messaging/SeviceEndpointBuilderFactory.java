/**
 * 
 */
package gov.hhs.fha.messaging;

/**
 * @author bhumphrey
 *
 */
public interface SeviceEndpointBuilderFactory<T> {
    
    public ServiceEndpointBuilder<T> getServiceEndpointBuilder();

}
