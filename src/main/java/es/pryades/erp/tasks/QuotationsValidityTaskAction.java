package es.pryades.erp.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Attachment;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.TaskAction;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.Task;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.CommonEditor;

public class QuotationsValidityTaskAction implements TaskAction, Serializable
{
	private static final long serialVersionUID = 1251073110678621625L;
	
	private static final Logger LOG = Logger.getLogger( QuotationsValidityTaskAction.class );

    public QuotationsValidityTaskAction()
	{
	}

	private void notifyUser( AppContext ctx, Quotation quotation )
	{
		try
		{
			String from = ctx.getParameter( Parameter.PAR_MAIL_SENDER_EMAIL );
			String host = ctx.getParameter( Parameter.PAR_MAIL_HOST_ADDRESS );
			String port = ctx.getParameter( Parameter.PAR_MAIL_HOST_PORT );
			String sender = ctx.getParameter( Parameter.PAR_MAIL_SENDER_USER );
			String password = ctx.getParameter( Parameter.PAR_MAIL_SENDER_PASSWORD ); 

			String text = ctx.getString( "tasks.quotation.validity.text" ).
					replaceAll( "%contact_person%", quotation.getContact().getName() ).
					replaceAll( "%number%", quotation.getFormattedNumber() ).
					replaceAll( "%reference_request%", quotation.getReference_request() );

			String proxyHost = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_HOST );
			String proxyPort = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_PORT );

			List<Attachment> attachments = new ArrayList<Attachment>();
			
			String subject = ctx.getString( "tasks.quotation.validity.subject" ).replaceAll( "%reference_request%", quotation.getReference_request() );
			String body = text + "\n\n" + ctx.getCompanyDataAndLegal( quotation.getUser() ); 
			
			Utils.sendMail( from, quotation.getContact().getEmail(), "", quotation.getUser().getEmail(), subject, host, port, sender, password, body, attachments, proxyHost, proxyPort, "true".equals( ctx.getParameter( Parameter.PAR_MAIL_AUTH ) ) );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	@Override
	public void doTask( AppContext ctx, Task task ) throws BaseException
	{
		LOG.info( "-------- started" );
		
		try 
		{
			QuotationQuery queryQuotation = new QuotationQuery();
			queryQuotation.setStatus( Quotation.STATUS_SENT );
			
			@SuppressWarnings("unchecked")
			List<Quotation> quotations = IOCManager._QuotationsManager.getRows( ctx, queryQuotation );
			
			for ( Quotation quotation : quotations )
			{
				if ( quotation.isExpired() )
				{
					LOG.info( "quotation " + quotation.getTitle() + " is expired" );
					
					notifyUser( ctx, quotation );
				}
			}
		} 
		catch ( Throwable e) 
		{
			Utils.logException( e, LOG );
		}
		   
		LOG.info( "-------- finished" );
	}

	@Override
	public CommonEditor getTaskDataEditor( AppContext context )
	{
		return null;
	}

	@Override
	public boolean isUserEnabledForTask( AppContext context )
	{
		return context.getUser().getRef_profile().equals( Constants.ID_PROFILE_DEVELOPER );
	}
}
