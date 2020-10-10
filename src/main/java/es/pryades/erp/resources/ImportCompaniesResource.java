package es.pryades.erp.resources;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Company;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.services.ReturnFactory;

public class ImportCompaniesResource extends ServerResource 
{
    private static final Logger LOG = Logger.getLogger( ImportCompaniesResource.class );

	public ImportCompaniesResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true  );
    }
    
    @Post("text")
    public void doPost( Representation entity ) throws Exception 
    {
    	InputStream is = entity != null ? entity.getStream() : null;
		
		if ( entity == null )
		{
			LOG.info( "empty message received" );

    		getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
    		getResponse().setEntity( new StringRepresentation( "FAIL\n" ) );
		}
		else
		{
			AppContext ctx = new AppContext( "en" );
			IOCManager._ParametersManager.loadParameters( ctx );

			int created = 0;
			int failed = 0;
			
			try
			{
				Utils.writeBinaryFile( is, "/tmp/companies.csv" );
				
				String ret = "";
				
				try
				{
					ArrayList<ArrayList<String>> list = Utils.readCsvFile( new FileInputStream( "/tmp/companies.csv" ), ',', '"' );
					
					boolean firstLine = true;
					boolean firstHub = true;
					
					for ( ArrayList<String> tokens : list ) 
					{
						int cols = tokens.size();
						
						if ( cols == 3 )
						{
							try
							{
								Company company = new Company();
								company.setAlias( tokens.get(0) );
								company.setName( tokens.get(1) );
								company.setTax_id( tokens.get(2) );
								company.setTaxable( false );
								company.setLanguage( "es" );
								company.setSignature( false );
								company.setType_company( Company.TYPE_PROVIDER );
								
								IOCManager._CompaniesManager.setRow( ctx, null, company );
								
								LOG.info( "alias " + company.getAlias() );
							}
							catch ( Throwable e )
							{
								Utils.logException( e, LOG );
							}
						}
					}
				}
				catch ( Throwable e )
				{
					Utils.logException( e, LOG );
				}
				
        		getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
	        	getResponse().setEntity( new StringRepresentation( ret ) );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
				
        		getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
        		getResponse().setEntity( new StringRepresentation( Utils.getExceptionString( e ) + "\n" ) );
			}
		}
	}
}