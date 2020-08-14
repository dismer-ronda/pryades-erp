package es.pryades.erp.application;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import es.pryades.erp.common.Settings;

public class I18N
{
	private static final Logger LOG = Logger.getLogger( I18N.class );
	
	private static Map<String, ResourceBundle> resources;
	
	private I18N()
	{
		super();
	}
	
	public static void Init()
	{
		resources = new HashMap<String, ResourceBundle>();
		
		List<String> langs = Arrays.asList( Settings.LANGUAGES.split( "," ) );
		
		for ( String language : langs )
		{
			ResourceBundle bundle = ResourceBundle.getBundle( "language", new Locale( language ) );
			resources.put( language, bundle );
			
			LOG.info( "language " + language + " loaded" );
		}
	}
	
	public static String get( String key, String language)
	{
		try
		{
			ResourceBundle bundle = resources.get( language );
			
			String value = bundle.getString( key );
			
			return value != null ? value : "!" + key;
		}
		catch ( Throwable e )
		{
			return "!" + key;
		}
	}
}
