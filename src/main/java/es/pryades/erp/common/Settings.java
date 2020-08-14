package es.pryades.erp.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public class Settings implements Serializable
{
	private static final long serialVersionUID = -8106647838261333215L;

	private static final Logger LOG = Logger.getLogger( Settings.class );

	public static String HOME_dir;

	public static String DB_engine;
	public static String DB_driver;
	public static String DB_url;
	public static String DB_user;
	public static String DB_password;

	public static String LANGUAGES;

	public static Settings instance = null;
	
    Boolean lock = Boolean.FALSE;
    Properties settings;
    
	private Settings()
	{
		super();
	}
	
	static public void Init() throws Exception
	{
		if ( instance == null )
			instance = new Settings();
		
		instance.loadSettingsFromFile( "pryades-erp.properties" );
	}
	
	static public Settings getInstance() throws BaseException
	{
		if ( instance == null )
			throw new BaseException( new Exception( "Settings not initialized " ), LOG, BaseException.SETTINGS_NOT_INITIALIZED );

		return instance;
	}
	
	private synchronized void loadSettingsFromFile( String fileName ) throws BaseException  
	{
		settings = new Properties();

		URL url = Thread.currentThread().getContextClassLoader().getResource( fileName );
		
        if ( url == null )
        	throw new BaseException( new Exception( "Settings file " + fileName + " was not found in the classpath" ), LOG, BaseException.SETTINGS_NOT_FOUND );

        try 
        {
			settings.load( new FileInputStream( url.getPath() ) );
		} 
        catch ( FileNotFoundException e ) 
        {
        	throw new BaseException( e, LOG, BaseException.SETTINGS_NOT_FOUND );
		} 
        catch ( IOException e ) 
        {
        	throw new BaseException( e, LOG, BaseException.SETTINGS_READ_ERROR );
		}

        initSettings();
        
        HOME_dir =  new File( url.getPath().replace( fileName, "" ) ).getAbsolutePath();
        
        LOG.info( "read successfully from " + url.getPath() );
        LOG.info( "HOME_dir set to " + HOME_dir );
	}
	
	private void initSettings() throws BaseException
	{
		DB_engine = getSetting( "DB_engine", "postgresql" );

		DB_driver = getSetting( DB_engine + ".DB_driver" , "org.postgresql.Driver" );
		DB_url = getSetting( DB_engine + ".DB_url", "jdbc:postgresql://localhost/indigo" );
		DB_user = getSetting( DB_engine + ".DB_user", "indigo" );
		DB_password = getSetting( DB_engine + ".DB_password", "secreto" );

		LANGUAGES = getSetting( "LANGUAGES", "en" );
	}

	public static String getSetting( String name, String defValue )
	{
		try
		{
			if ( getInstance().settings == null )
				return defValue;
			
			String value = getInstance().settings.getProperty( name, defValue );
				
			if ( value == null )
				return defValue;
			
			return value.trim();
		}
		catch (Throwable e )
		{
			return "";
		}
	}
	
	public static String getEnviroment( String name, String defValue )
	{
		String value = System.getenv( name );
		
		if ( value == null || value.isEmpty() )
			return defValue;
		
		return value;
	}
}
