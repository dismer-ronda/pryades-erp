package es.pryades.erp.common;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpHost;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.erp.application.I18N;
import es.pryades.erp.dal.ibatis.DalManager;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.ProfileRight;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Data;

/**
 * @author Dismer Ronda
 *
 */
@Data
public class AppContext extends Object implements Serializable  
{
	private static final long serialVersionUID = -899983198743845287L;

	static final Logger LOG = Logger.getLogger( AppContext.class );
	
	private SqlSession session;
	private User user;
	private List<ProfileRight> rights;
	private HashMap<Long, Parameter> parameters;
	private HashMap<String,Object> extra;
	private long start;
	private String language;
	
	public AppContext( String language )
	{
		this.language = language;
		
		session = null;
		
		extra = new HashMap<String,Object>();
	}
	
	protected void finalize() throws Throwable 
	{
		closeSession();
	}

	public SqlSession openSession() throws BaseException
	{
		if ( getSession() != null )
			throw new BaseException( new Exception( "SQL session MUST be closed previously" ), LOG, BaseException.CONNECTION );
			
		if ( getSession() == null )
		{
			start = new Date().getTime();
			
			setSession( DalManager.openSession() );
		}
		
		return getSession();
	}
	
	public void closeSession() throws BaseException
	{
		if ( getSession() != null )
		{
			session.close();
			
			if ( LOG.isDebugEnabled() )
				LOG.debug( "session " + getSession().hashCode() + " consumed " + (new Date().getTime() - start) + " ms" );
			
			setSession( null );
		}
	}

	public boolean isPasswordExpired() throws BaseException
    {
		return CalendarUtils.isExpired( getUser().getChanged(), getIntegerParameter( Parameter.PAR_PASSWORD_VALID_TIME ) );
    }
	
	public void addData( String key, Object value )
	{
		extra.put( key, value );
	}
	
	public Object getData( String key )
	{
		return extra.get( key );
	}
	
	public void removeData( String key )
	{
		extra.remove( key );
	}
	
	public void clearData()
	{
		extra.clear();
	}
	
	public boolean hasRight( String code )
	{
		for ( ProfileRight right : rights )
			if ( code.equals( right.getRight_code() ) )
				return true;

		return false;
	}
	
	public String getString( String key )
	{
		return I18N.get( key, language );
		
		/*try 
		{
			String ret = resources.get( key );
			
			return ret != null ? ret : key;
		}
		catch ( Throwable e )
		{
			return key;
		}*/
	}	
	
	public String getParameter( long id )
	{
		Parameter parameter = parameters.get( id );
		
		if ( parameter != null ) 
			return parameter.getValue();
		
		return "";
	}

	public Long getLongParameter( long id )
	{
		return Long.parseLong( parameters.get( id ).getValue() );
	}

	public Double getDoubleParameter( long id )
	{
		return Double.parseDouble( parameters.get( id ).getValue() );
	}

	public Integer getIntegerParameter( long id )
	{
		return Integer.parseInt( parameters.get( id ).getValue() );
	}
	
	public HttpHost getHttpProxy()
	{
		HttpHost proxy = null;
		
		String proxyHost = getParameter( Parameter.PAR_HTTP_PROXY_HOST );
		
		if ( !proxyHost.isEmpty() )
		{
			Integer proxyPort = 80;
			
			try
			{
				proxyPort = getIntegerParameter( Parameter.PAR_HTTP_PROXY_PORT );
			}
			catch ( Throwable e )
			{
			}

			proxy = new HttpHost( proxyHost, proxyPort );
		}
		
		return proxy;
	}
	
	public boolean isLogEnabled( long id, String operation )
	{
		return getParameter( id ).contains( operation );
	}
	
	public String getSignatureUrl( User user )
	{
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );

			String timeout = "0";
			String extra = "ts=" + ts + 
							"&timeout=" + timeout;
			
			String login = user.getLogin();
			String password = user.getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;
	
			return StringEscapeUtils.escapeXml( getData( "Url" ) + "/services/signature" + "?user=" + login + "&" + token + "&" + code + "&ts=" + ts ); 
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			return "";
		}
	}

	public String getLogoUrl()
	{
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );

			String timeout = "0";
			String extra = "ts=" + ts + 
							"&timeout=" + timeout;
			
			String user = getUser().getLogin();
			String password = getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			return StringEscapeUtils.escapeXml( getData( "Url" ) + "/services/logo" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts ); 
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			return "";
		}
	}

	public String getCompanyData( User user )
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_OWNER );
			
			Company company = (Company)IOCManager._CompaniesManager.getRow( this, query );
			
			String data = user.getName() + "\n" + 
					company.getName() + "\n" +
					getString( "template.common.tax_id" ) + ": " + company.getTax_id() + "\n" + 
					company.getAddress() + "\n" +
					getString( "template.common.phone" ) + ": " + company.getPhone() + "\n" + 
					getString( "template.common.email" ) + ": " + company.getEmail();

			return data;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			return "";
		}
	}

	public String getCompanyDataAndLegal( Company company, User user )
	{
		try
		{
			String legal = getString( "template.common.email.legal" ).
					replaceAll( "%owner_name%", company.getName() ).
					replaceAll( "%owner_address%", company.getAddress().replaceAll( "\n", ", " ) ).
					replaceAll( "%owner_email%", user.getEmail() );

			String data = user.getName() + "\n" + 
					company.getName() + "\n" +
					getString( "template.common.tax_id" ) + ": " + company.getTax_id() + "\n" + 
					company.getAddress() + "\n" +
					getString( "template.common.phone" ) + ": " + company.getPhone() + "\n" + 
					getString( "template.common.email" ) + ": " + company.getEmail() + "\n\n" +
					legal;

			return data;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			return "";
		}
	}

	public String getCompanyDataAsHtml( User user )
	{
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( getCompanyData( user ) ) );
	}
}
