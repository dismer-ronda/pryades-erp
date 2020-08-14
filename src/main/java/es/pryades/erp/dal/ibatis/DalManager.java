package es.pryades.erp.dal.ibatis;

import java.io.StringReader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class DalManager  
{
    private static final Logger LOG = Logger.getLogger( DalManager.class );

    static DalManager instance;
    
    SqlSessionFactory sessionFactory;
	
    String engine;
	String dbDriver;
	String dbUrl;
	String dbUser;
	String dbPassword;
	
	public DalManager( String engine, String dbDriver, String dbUrl, String dbUser, String dbPassword ) 
	{
		super();
		
		this.engine = engine;
		this.dbDriver = dbDriver;
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}
	
	public void Init()
	{
		String xml = 
			"<?xml version='1.0' encoding='UTF-8'?>" + 
				"<!DOCTYPE configuration PUBLIC '-//mybatis.org//DTD Config 3.0//EN ' 'http://mybatis.org/dtd/mybatis-3-config.dtd'>" +
				"<configuration>" +
					"<environments default='development'>" +
						"<environment id='development'>" +
							"<transactionManager type='JDBC' />" + 
							"<dataSource type='POOLED'>" +
								"<property name='driver' value='" + dbDriver + "' />" +
								"<property name='url' value='" + dbUrl + "'/>" +
								"<property name='username' value='" + dbUser + "' />" +
								"<property name='password' value='" + dbPassword + "' />" + 
							"</dataSource>" +
						"</environment>" +
					"</environments>" +
					"<mappers>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/RightMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/ProfileMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/ProfileRightMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/UserMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/ParameterMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/UserDefaultMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/AuditMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/FileMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/TaskMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/CompanyMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/QuotationMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/QuotationDeliveryMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/QuotationLineMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/QuotationLineDeliveryMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/QuotationAttachmentMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/InvoiceMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/InvoiceLineMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/ShipmentMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/ShipmentBoxMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/ShipmentBoxLineMapper.xml'/>" +
						"<mapper resource='es/pryades/erp/dal/" + engine + "/UserCompanyMapper.xml'/>" +
					"</mappers>"+
				"</configuration>";
				
		StringReader reader = new StringReader( xml.replace('\'', '"') );
		
		sessionFactory = new SqlSessionFactoryBuilder().build( reader );
		
		LOG.info( "data access layer for " + engine + " initialized" );
	}
	
	public static void Init( String engine, String dbDriver, String dbUrl, String dbUser, String dbPassword ) throws BaseException
	{
		if ( instance == null )
		{
			instance = new DalManager( engine, dbDriver, dbUrl, dbUser, dbPassword );
			
			try 
			{
				instance.Init();
			} 
			catch ( Throwable e ) 
			{
				Utils.logException( e, LOG );
				
				throw new BaseException( e, LOG, 0 );
			}
		}
	}
	
	public static DalManager getInstance() throws BaseException
	{
		if ( instance == null )
			throw new BaseException( new Exception( "database not initialized" ), LOG, BaseException.INSTANCE_NOT_INITIALIZED );

		return instance;
	}

	public static SqlSession openSession() throws BaseException
	{
		DalManager instance = getInstance();
	
		for ( int i = 0; i < 3; i++ )
		{
			try 
			{
				return instance.sessionFactory.openSession();	
			}
			catch ( Throwable e )
			{
				LOG.error( e.getMessage() + ". Retrying " + (3 - i - 1) + " more times" );
				
				if ( i == 2 )
					throw new BaseException( e, LOG, 0 );	
			}
		}
		
		// This code should not be reached
		return null;
	}
}
