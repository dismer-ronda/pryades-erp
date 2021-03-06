package es.pryades.erp.services;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import es.pryades.erp.resources.AttachmentResource;
import es.pryades.erp.resources.ImportCompaniesResource;
import es.pryades.erp.resources.InvoiceResource;
import es.pryades.erp.resources.InvoicesPdfResource;
import es.pryades.erp.resources.LabelsResource;
import es.pryades.erp.resources.LoginResource;
import es.pryades.erp.resources.LogoResource;
import es.pryades.erp.resources.PackingObjectResource;
import es.pryades.erp.resources.PackingResource;
import es.pryades.erp.resources.PurchaseInvoiceResource;
import es.pryades.erp.resources.PurchasePaymentResource;
import es.pryades.erp.resources.PurchaseQuotationResource;
import es.pryades.erp.resources.PurchasesResource;
import es.pryades.erp.resources.QrResource;
import es.pryades.erp.resources.QuotationResource;
import es.pryades.erp.resources.ShipmentAttachmentResource;
import es.pryades.erp.resources.ShipmentBoxResource;
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
    	router.attach( "/import-companies", ImportCompaniesResource.class );

    	router.attach( "/login", LoginResource.class );
    	router.attach( "/quotation", QuotationResource.class );
    	router.attach( "/attachment", AttachmentResource.class );
    	router.attach( "/invoice", InvoiceResource.class );
    	router.attach( "/packing", PackingResource.class );
    	router.attach( "/signature", SignatureResource.class );
    	router.attach( "/logo", LogoResource.class );
    	router.attach( "/qr", QrResource.class );
    	router.attach( "/box", ShipmentBoxResource.class );
    	router.attach( "/packing_object", PackingObjectResource.class );
    	router.attach( "/labels", LabelsResource.class );
    	router.attach( "/invoices", InvoicesPdfResource.class );
    	router.attach( "/purchase-invoice", PurchaseInvoiceResource.class );
    	router.attach( "/purchase-quotation", PurchaseQuotationResource.class );
    	router.attach( "/purchase-payment", PurchasePaymentResource.class );
    	router.attach( "/purchases", PurchasesResource.class );
    	router.attach( "/shipment-attachment", ShipmentAttachmentResource.class );
    	
    	LOG.info( "started" );
	    
	    return router;  
    }
}
