package es.pryades.erp.tasks;

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.Settings;
import es.pryades.erp.common.TaskAction;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Task;
import es.pryades.erp.dto.User;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.CommonEditor;

public class DatabaseUpdateTaskAction implements TaskAction, Serializable
{
	private static final long serialVersionUID = 7452056074022160387L;

	private static final Logger LOG = Logger.getLogger( DatabaseUpdateTaskAction.class );

    public DatabaseUpdateTaskAction()
	{
	}

	private void notifyUser( AppContext ctx, User user, String body )
	{
		try
		{
			String from = ctx.getParameter( Parameter.PAR_MAIL_SENDER_EMAIL );
			String to = user.getEmail();
			String host = ctx.getParameter( Parameter.PAR_MAIL_HOST_ADDRESS );
			String port = ctx.getParameter( Parameter.PAR_MAIL_HOST_PORT );
			String sender = ctx.getParameter( Parameter.PAR_MAIL_SENDER_USER );
			String password = ctx.getParameter( Parameter.PAR_MAIL_SENDER_PASSWORD ); 

			String text = ctx.getString( "tasks.database.update.message.text" ).
					replaceAll( "%user%", user.getName() );

			String proxyHost = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_HOST );
			String proxyPort = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_PORT );
			
			Utils.sendMail( from, to, "", from, ctx.getString( "tasks.database.update.message.subject" ), host, port, sender, password, text + "\n" + body, null, proxyHost, proxyPort, "true".equals( ctx.getParameter( Parameter.PAR_MAIL_AUTH ) ) );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	private String executeUpdate( Connection conn, String queryString )
	{
    	try
		{
			Statement statement = conn.createStatement();
    		
			return Integer.toString( statement.executeUpdate( queryString ) );
		}
    	catch ( Throwable e )
    	{
    		Utils.logException( e, LOG );
    		
    		return "fail";
    	}
	}

	private String readCreateTable( BufferedReader bufferedReader )
	{
		String queryString = "";
		
		while ( true )
		{
			try
			{
				String line = bufferedReader.readLine();
				
				queryString += line + "\n";
				
				if ( line.startsWith( ");" ) )
					break;
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
		
		return queryString;
	}

	
	@Override
	public void doTask( AppContext ctx, Task task ) throws BaseException
	{
		LOG.info( "-------- started" );
		
		DatabaseUpdateTaskData data = (DatabaseUpdateTaskData) Utils.toPojo( task.getDetails(), DatabaseUpdateTaskData.class, false );
		
		User queryUser = new User();
		queryUser.setId( data.getRef_user() );
		User user = (User) IOCManager._UsersManager.getRow( ctx, queryUser );
		
        String body = "";
        
		try 
		{
			Class.forName( Settings.DB_driver );
			Connection conn = DriverManager.getConnection( Settings.DB_url, Settings.DB_user, Settings.DB_password );
	
			String translate = data.getSql().
					
					replaceAll( "_CT_", "CREATE TABLE" ).
					replaceAll( "_AT_", "ALTER TABLE" ).
					replaceAll( "_II_", "INSERT INTO" ).
					replaceAll( "_UD_", "UPDATE" ).
					replaceAll( "_WH_", "WHERE" ).
					replaceAll( "_VA_", "VALUES" );

			StringReader reader = new StringReader( translate );
		       
	        BufferedReader bufferedReader = new BufferedReader( reader );

	        String line = null;
	        while ( (line = bufferedReader.readLine()) != null ) 
	        {
	        	if ( !line.startsWith( "--" ) && !line.isEmpty() )
	        	{
	        		if ( line.startsWith( "CREATE TABLE" ) )
		        	{
	        			String queryString = line + "\n" + readCreateTable( bufferedReader );
		        		body += queryString + " -> " + executeUpdate( conn, queryString ) + "\n";
		        	}
		        	else 
		        	{
		        		body += line + " -> " + executeUpdate( conn, line ) + "\n";
		        	}
	        	}
	        }    

	        conn.close();
	           
	        bufferedReader.close();
		} 
		catch ( Throwable e) 
		{
			body += " error";
		    
			Utils.logException( e, LOG );
		}
		   
		notifyUser( ctx, user, body );

		LOG.info( "-------- finished" );
	}

	@Override
	public CommonEditor getTaskDataEditor( AppContext context )
	{
		return new DatabaseUpdateTaskDataEditor( context );
	}

	@Override
	public boolean isUserEnabledForTask( AppContext context )
	{
		return context.getUser().getRef_profile().equals( Constants.ID_PROFILE_DEVELOPER );
	}
}
