package es.pryades.erp.services;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import es.pryades.erp.resources.AttachmentResource;
import es.pryades.erp.resources.InvoiceResource;
import es.pryades.erp.resources.LogoResource;
import es.pryades.erp.resources.PackingResource;
import es.pryades.erp.resources.QuotationResource;
import es.pryades.erp.resources.SignatureResource;

public class ServicesServlet extends Application
{
	private static final Logger LOG = Logger.getLogger( ServicesServlet.class );
	
    public ServicesServlet() 
    {
        super();
	}

    @Override
    public Restlet createInboundRoot() 
    {  
    	Router router = new Router( getContext() );
	       
    	//router.attach( "/test", TestResource.class );

    	router.attach( "/quotation", QuotationResource.class );
    	router.attach( "/attachment", AttachmentResource.class );
    	router.attach( "/invoice", InvoiceResource.class );
    	router.attach( "/packing", PackingResource.class );
    	router.attach( "/signature", SignatureResource.class );
    	router.attach( "/logo", LogoResource.class );
    	
    	LOG.info( "started" );
	    
	    return router;  
    }
}
