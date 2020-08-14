package es.pryades.erp.common;

import java.io.Serializable;
import java.security.MessageDigest;

import org.apache.log4j.Logger;

/**
 * 
 * @author dismer.ronda
 * @since 1.0.0.0
 */

public class Authorization implements Serializable
{
	private static final long serialVersionUID = 2914621319303201816L;
	
	private static final Logger LOG = Logger.getLogger( Authorization.class );

	public static String convertToHex( byte value )
	{
		String table = "0123456789ABCDEF";

		byte lo = (byte)(value & 0x0F);
		byte hi = (byte)((value & 0xF0) >> 4);

		return "" + table.charAt( hi ) + table.charAt( lo );
	}

	public static String convertToHex( byte[] data )
	{
		StringBuffer buf = new StringBuffer();

		for ( int i = 0; i < data.length; i++ )
			buf.append( convertToHex( data[i] ) );

		return buf.toString();
	}

	public static byte convertFromHex( byte hi, byte lo )
	{
		String table = "0123456789ABCDEF";

		return (byte)(table.indexOf( lo ) | (table.indexOf( hi ) << 4));
	}

	public static byte[] convertFromHex( byte[] data )
	{
		byte buffer[] = new byte[data.length / 2];

		for ( int i = 0; i < data.length; i += 2 )
			buffer[i / 2] = convertFromHex( data[i], data[i + 1] );

		return buffer;
	}
	
	public static String MD5( String text ) throws BaseException
	{
		MessageDigest md;

		byte[] md5hash = new byte[32];

		try
		{
			md = MessageDigest.getInstance( "MD5" );

			md.update( text.getBytes( "iso-8859-1" ), 0, text.length() );

			md5hash = md.digest();

			return convertToHex( md5hash );
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.ENCRYPT_ERROR );
		}
	}

	public static String MD5( byte[] buffer ) throws BaseException
	{
		MessageDigest md;

		byte[] md5hash = new byte[32];

		try
		{
			md = MessageDigest.getInstance( "MD5" );

			md.update( buffer, 0, buffer.length );

			md5hash = md.digest();

			return convertToHex( md5hash ).toUpperCase();
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.ENCRYPT_ERROR );
		}
	}

	public static String SHA1( String text ) throws BaseException
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance( "SHA1" );

			byte[] md5hash = new byte[32];

			md.update( text.getBytes( "iso-8859-1" ), 0, text.length() );

			md5hash = md.digest();

			return convertToHex( md5hash );
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.ENCRYPT_ERROR );
		}
	}

	public static String getTrustedToken( String data, String pwd ) throws BaseException
	{
		return Authorization.SHA1( pwd + data );
	}

	public static boolean isTrustedToken( String token, String data, String pwd ) throws BaseException
	{
		return token.equalsIgnoreCase( getTrustedToken( data, pwd ) );
	}

	public static String encrypt( String data, String pwd ) throws BaseException
	{
		try
		{
			return convertToHex( Cryptor.encrypt( data.getBytes(), pwd ) );
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.ENCRYPT_ERROR );
		}
	}

	public static String decrypt( String data, String pwd ) throws BaseException
	{
		try
		{
			return new String( Cryptor.decrypt( convertFromHex( data.getBytes() ), pwd ) );
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.DECRYPT_ERROR );
		}
	}

	public static long getLong( String value, long defValue )
	{
		try
		{
			return Long.parseLong( value );
		}
		catch ( Throwable e )
		{
			return defValue;
		}
	}

	public static boolean isValidRequest( String token, String data, String ts, String password, long timeout )
	{
		if ( token == null || data == null || ts == null || password == null )
			return false;

		boolean ret = false;
		
		long when = getLong( ts, 0 );
		long now = CalendarUtils.getTodayAsLong( "UTC" );
		long elapsed = Utils.getDurationInSeconds( when, now );
		
		String log = "when = " + when + " now = " + now + " elapsed = " + elapsed + " timeout = " + timeout + " reveived token = " + token; 

		try
		{
			log += " calculated token = " + Authorization.SHA1( password + data ).toLowerCase(); 

			if ( isTrustedToken( token, data, password ) )
			{
				ret = timeout != 0 ? elapsed < timeout : true;

				log += " trusted";
			}
			else
				log += " NOT trusted";
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			ret = false;
			
			log += " EXCEPTION";
		}

		LOG.info( log );

		return ret;
	}

	public static String getTokenString( String data, String password )
	{
		try
		{
			return getTrustedToken( data, password );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}

		return "";
	}
}