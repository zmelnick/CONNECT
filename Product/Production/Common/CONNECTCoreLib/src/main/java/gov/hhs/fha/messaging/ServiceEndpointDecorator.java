/**
 * 
 */
package gov.hhs.fha.messaging;

/**
 * @author bhumphrey
 *
 */
public class ServiceEndpointDecorator<T> implements ServiceEndpointBuilder<T> {

 
    private ServiceEndpointBuilder<T> decoratored;
    
    public ServiceEndpointDecorator(ServiceEndpointBuilder<T> decoratored) {
        this.decoratored = decoratored;
    }
    
    
    @Override
    public T build() {
        return decoratored.build();
    }

}
