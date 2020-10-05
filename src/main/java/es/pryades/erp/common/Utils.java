package es.pryades.erp.common;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;

import es.pryades.erp.dto.User;

/**
 * 
 * @author dismer.ronda
 * @since 1.0.0.0
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Utils
{
	private static final Logger LOG = Logger.getLogger( Utils.class );

	public static final long ONE_MB = 1024 * 1024;

	public static final long ONE_SECOND = 1000;
	public static final long ONE_MINUTE = 60 * ONE_SECOND;
	public static final long ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long ONE_YEAR = 365 * ONE_DAY;

	private static final String paperSizes[] = { "A4", "A3", "Letter", "Legal" };

	private static final int Crc8Table[] =  /* filter that will be used */
	{
		0x00, 0xD5, 0x7F, 0xAA, 0xFE, 0x2B, 0x81, 0x54,
        0x29, 0xFC, 0x56, 0x83, 0xD7, 0x02, 0xA8, 0x7D,
        0x52, 0x87, 0x2D, 0xF8, 0xAC, 0x79, 0xD3, 0x06,
        0x7B, 0xAE, 0x04, 0xD1, 0x85, 0x50, 0xFA, 0x2F,

        0xA4, 0x71, 0xDB, 0x0E, 0x5A, 0x8F, 0x25, 0xF0,
        0x8D, 0x58, 0xF2, 0x27, 0x73, 0xA6, 0x0C, 0xD9,
        0xF6, 0x23, 0x89, 0x5C, 0x08, 0xDD, 0x77, 0xA2,
        0xDF, 0x0A, 0xA0, 0x75, 0x21, 0xF4, 0x5E, 0x8B,

        0x9D, 0x48, 0xE2, 0x37, 0x63, 0xB6, 0x1C, 0xC9,
        0xB4, 0x61, 0xCB, 0x1E, 0x4A, 0x9F, 0x35, 0xE0,
        0xCF, 0x1A, 0xB0, 0x65, 0x31, 0xE4, 0x4E, 0x9B,
        0xE6, 0x33, 0x99, 0x4C, 0x18, 0xCD, 0x67, 0xB2,

        0x39, 0xEC, 0x46, 0x93, 0xC7, 0x12, 0xB8, 0x6D,
        0x10, 0xC5, 0x6F, 0xBA, 0xEE, 0x3B, 0x91, 0x44,
        0x6B, 0xBE, 0x14, 0xC1, 0x95, 0x40, 0xEA, 0x3F,
        0x42, 0x97, 0x3D, 0xE8, 0xBC, 0x69, 0xC3, 0x16,

        0xEF, 0x3A, 0x90, 0x45, 0x11, 0xC4, 0x6E, 0xBB,
        0xC6, 0x13, 0xB9, 0x6C, 0x38, 0xED, 0x47, 0x92,
        0xBD, 0x68, 0xC2, 0x17, 0x43, 0x96, 0x3C, 0xE9,
        0x94, 0x41, 0xEB, 0x3E, 0x6A, 0xBF, 0x15, 0xC0,

        0x4B, 0x9E, 0x34, 0xE1, 0xB5, 0x60, 0xCA, 0x1F,
        0x62, 0xB7, 0x1D, 0xC8, 0x9C, 0x49, 0xE3, 0x36,
        0x19, 0xCC, 0x66, 0xB3, 0xE7, 0x32, 0x98, 0x4D,
        0x30, 0xE5, 0x4F, 0x9A, 0xCE, 0x1B, 0xB1, 0x64,

        0x72, 0xA7, 0x0D, 0xD8, 0x8C, 0x59, 0xF3, 0x26,
        0x5B, 0x8E, 0x24, 0xF1, 0xA5, 0x70, 0xDA, 0x0F,
        0x20, 0xF5, 0x5F, 0x8A, 0xDE, 0x0B, 0xA1, 0x74,
        0x09, 0xDC, 0x76, 0xA3, 0xF7, 0x22, 0x88, 0x5D,

        0xD6, 0x03, 0xA9, 0x7C, 0x28, 0xFD, 0x57, 0x82,
        0xFF, 0x2A, 0x80, 0x55, 0x01, 0xD4, 0x7E, 0xAB,
        0x84, 0x51, 0xFB, 0x2E, 0x7A, 0xAF, 0x05, 0xD0,
        0xAD, 0x78, 0xD2, 0x07, 0x53, 0x86, 0x2C, 0xF9
	};

    /*******************************************************************************
    *
    * Function Name :   crc8_GetCrc
    *
    * Parameters    :   const uint8_t *data_tart - pointer, first data byte.
    *                   num_bytes - length of data block, based off.
    *                   of dataStart.
    *                   crc - seed value used to update CRC.
    *
    * Return Value  :   Computed CRC value
    *
    * Description   :   Computes CRC on data bytes using a lookup table to achieve
    *                   high speed. This function uses a CRC-8 algorithm.
    *
    *                   The function is set up to allow for computation of a CRC in
    *                   multiple blocks.  To do so, call this function for the first
    *                   block with the default CRC seed value and then use the
    *                   return value from this function as the seed value for the
    *                   call to compute the CRC on the next block.
    *
    ******************************************************************************/
    public static int getCRC( byte data[], int index, int length, int crc )
    {
    	/* The CRC-8 table is generated using the polynomial with non-reflection
        * x^8 + x^7 + x^4 + x^2 + 1. Notating with the most significant (x^8)
        * term missing this becomes a polynomial notation of 0x5D
        *
        * The CRC table can be generated using the following MATLAB algorithm:
        *  for i = 1:256
        *      seed = uint8(i - 1); % set the seed to the zero based table index
        *      for j = uint8(0:7)
        *          if bitand(seed,128) > 0
        *              seed = uint8(bitshift(seed,1)); % Shift the seed up by 1
        *              seed = bitxor(seed,poly); % xor with poly (0x5D = 223)
        *          else
        *              seed = uint8(bitshift(seed,1)); % shift up by 1
        *          end
        *      end
        *      lut(i) = seed;
        *  end
        */

    	for ( int i = 0; i < length; i++ )
    		crc = Crc8Table[crc ^ (data[index + i] & 0xFF)];

    	return crc;
    }

    public static String getSelector( String s, int length )
	{
		StringTokenizer tokens = new StringTokenizer( Normalizer.normalize( s.toUpperCase(), Normalizer.Form.NFD ).replaceAll( "[^\\p{ASCII}]", "" ) );

		String r = "";

		while ( tokens.hasMoreTokens() )
		{
			String token = tokens.nextToken();

			if ( !token.equals( "DE" ) && !token.equals( "DEL" ) && !token.equals( "LA" ) && !token.equals( "LAS" ) && !token.equals( "LOS" ) && !token.equals( "SAN" ) )
				r += token + " ";
		}

		String ret = r.trim();

		if ( ret.length() > length )
			ret = ret.substring( 0, length );

		return ret;
	}

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
	
	public static int bcdToDecimal( byte bcd )
	{
		return (bcd & 0xF) + (((int)bcd & 0xF0) >> 4)*10;
	}

	public static String MD5( String text )
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
			return convertToHex( text.getBytes() );
		}
	}

	public static String MD5( byte[] buffer )
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
			return convertToHex( buffer ).toUpperCase();
		}
	}

	public static String SHA1( String text )
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
			return convertToHex( text.getBytes() );
		}
	}

	public static int getInt( String value, int defValue )
	{
		try
		{
			return Integer.parseInt( value );
		}
		catch ( Throwable e )
		{
			return defValue;
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

	public static double getDouble( String value, double defValue )
	{
		try
		{
			return Double.parseDouble( value );
		}
		catch ( Throwable e )
		{
			return defValue;
		}
	}

	public static String getExceptionMessage( Throwable e )
	{
		String txt;

		if ( e.getMessage() != null )
			txt = e.getMessage();
		else if ( e.getCause() != null )
			txt = getExceptionMessage( e.getCause() );
		else
			txt = e.toString();

		return txt;
	}

	public static String getExceptionString( Throwable e )
	{
		String txt = "";
		
		StackTraceElement stack[] = e.getStackTrace();

		if ( stack.length > 0 )
		{
			boolean msg = true;

			txt = e.getClass().getName() + " ";

			for ( int i = 0; i < stack.length; i++ )
			{
				if ( msg )
				{
					txt += getExceptionMessage( e );

					msg = false;
				}

				txt += "\nat class " + stack[i].getClassName() + " method " + stack[i].getMethodName() + " line " + stack[i].getLineNumber();
			}
		}
		
		return txt;
	}
	
	public static void logException( Throwable e, Logger LOG )
	{
		StackTraceElement stack[] = e.getStackTrace();

		if ( stack.length > 0 )
		{
			boolean msg = true;

			String txt = e.getClass().getName() + " ";
			String extra = "";

			for ( int i = 0; i < stack.length; i++ )
			{
				if ( msg )
				{
					txt += getExceptionMessage( e );

					msg = false;
				}

				extra += "\n\tat class " + stack[i].getClassName() + " method " + stack[i].getMethodName() + " line " + stack[i].getLineNumber();
			}

			LOG.error( txt + extra );
		}
	}

	public static String getRandomPassword( int length )
	{
		return (new RandPass()).getPass( length );
	}

	public static void sendMail( String from, String to, String reply_to, String subject, String host, String port, String user, String password, String text, List<Attachment> attachments, String proxyHost, String proxyPort, boolean auth ) throws BaseException
	{
		try
		{
			// Get system properties
			Properties properties = new Properties();

			if ( auth )
			{
				properties.put( "mail.smtp.auth", "true" );
				properties.put( "mail.smtp.starttls.enable", "true" );
				properties.put( "mail.smtp.ssl.trust", host );
			}
			
			properties.put( "mail.smtp.port", port );

			if ( !proxyHost.isEmpty() )
			{
				properties.setProperty("mail.smtp.socks.host", proxyHost);
				properties.setProperty("mail.smtp.socks.port", proxyPort);
			}
			
			// Get the default Session object.
			Session session = Session.getInstance( properties );
	
			// a default MimeMessage object.
			MimeMessage message = new MimeMessage( session );
	
			// Set the RFC 822 "From" header field using the
			// value of the InternetAddress.getLocalAddress method.
			message.setFrom( new InternetAddress( from ) );
			message.setReplyTo( new Address[] { new InternetAddress( reply_to )	} );
			message.setSentDate( new Date() );
			message.setHeader( "Disposition-Notification-To", reply_to );
			
			// Add the given addresses to the specified recipient type.
			String recipients[] = to.split( "," );

			if ( recipients != null )
			{
				for ( String recipient : recipients )
					message.addRecipient( Message.RecipientType.TO, new InternetAddress( recipient ) );

				// Set the "Subject" header field.
				message.setSubject( subject );

				// Sets the given String as this part's content,
				// with a MIME type of "text/plain".

				// create the message part
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText( text );

				Multipart multipart = new MimeMultipart( "mixed" );

				multipart.addBodyPart( messageBodyPart );

				if ( attachments != null )
				{
					for ( Attachment attachment : attachments )
					{
						MimeBodyPart part = new MimeBodyPart();

						DataSource source = new ByteArrayDataSource( attachment.getContent(), attachment.getType() );

						part.setDataHandler( new DataHandler( source ) );
						part.setFileName( attachment.getName() );
						multipart.addBodyPart( part );
					}
				}

				message.setContent( multipart );
				
				// Send message
				Transport tr = session.getTransport( "smtp" );
				tr.connect( host, user, password );
				tr.sendMessage( message, message.getAllRecipients() );
				tr.close();
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			throw new BaseException( e, LOG, 0 );
		}
	}

	public static Object clone( Object o ) throws Throwable
	{
		Object clone = o.getClass().newInstance();

		for ( Class obj = o.getClass(); !obj.equals( Object.class ); obj = obj.getSuperclass() )
		{
			Field[] fields = obj.getDeclaredFields();

			for ( int i = 0; i < fields.length; i++ )
			{
				if ( !Modifier.isStatic( fields[i].getModifiers() ) )
				{
					fields[i].setAccessible( true );
					fields[i].set( clone, fields[i].get( o ) );
				}
			}
		}

		return clone;
	}

	public static void copy( Object org, Object clone ) throws BaseException
	{
		try
		{
			for ( Class obj = org.getClass(); !obj.equals( Object.class ); obj = obj.getSuperclass() )
			{
				Field[] fields = obj.getDeclaredFields();

				for ( int i = 0; i < fields.length; i++ )
				{
					if ( !Modifier.isStatic( fields[i].getModifiers() ) )
					{
						fields[i].setAccessible( true );
						fields[i].set( clone, fields[i].get( org ) );
					}
				}
			}
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.REFLECTION_ERROR );
		}
	}

	public static boolean isPersonIdentifier( String text )
	{
		for ( int i = 0; i < text.length(); i++ )
			if ( Character.isDigit( text.charAt( i ) ) )
				return true;

		return false;
	}

	public static Method getGetter( Method[] methods, String field )
	{
		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "get" + field ) )
				return method;
		}

		return null;
	}

	public static Method getSetter( Method[] methods, String field )
	{
		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "set" + field ) )
				return method;
		}

		return null;
	}

	public static void emptyToNull( Object obj, Class clazz ) throws BaseException
	{
		try
		{
			if ( List.class.isAssignableFrom( obj.getClass() ) )
			{
				for ( Object subObj : (List)obj )
					emptyToNull( subObj, subObj.getClass() );
			}
			else
			{
				Class superClazz = clazz.getSuperclass();

				if ( superClazz != null )
					Utils.emptyToNull( obj, superClazz );

				Field[] newFields = clazz.getDeclaredFields();
				Method[] methods = clazz.getDeclaredMethods();

				Object empty[] = new Object[] {};
				Object nulls[] = new Object[]
				{ null };

				for ( Field field : newFields )
				{
					if ( !Modifier.isStatic( field.getModifiers() ) )
					{
						String fname = field.getName();

						Method getter = getGetter( methods, fname );

						if ( getter != null )
						{
							Object value = getter.invoke( obj, empty );

							if ( field.getType().equals( String.class ) )
							{
								Method setter = getSetter( methods, fname );

								if ( "".equals( value ) )
									setter.invoke( obj, nulls );
							}
							else if ( field.getType().equals( List.class ) && value != null )
							{
								for ( Object subObj : (List)value )
									emptyToNull( subObj, subObj.getClass() );
							}
						}
					}
				}
			}
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.JSON_STRING_ERROR );
		}
	}

	public static void nullToEmpty( Object obj, Class clazz ) throws BaseException
	{
		try
		{
			Class superClazz = clazz.getSuperclass();

			if ( superClazz != null )
				Utils.nullToEmpty( obj, superClazz );

			Field[] newFields = clazz.getDeclaredFields();
			Method[] methods = clazz.getDeclaredMethods();

			Object empty[] = new Object[] {};
			Object empties[] = new Object[]
			{ "" };

			for ( Field field : newFields )
			{
				if ( !Modifier.isStatic( field.getModifiers() ) )
				{
					String fname = field.getName();

					Method getter = getGetter( methods, fname );

					if ( getter != null )
					{
						Object value = getter.invoke( obj, empty );

						if ( field.getType().equals( String.class ) )
						{
							Method setter = getSetter( methods, fname );

							if ( value == null )
								setter.invoke( obj, empties );
						}
						else if ( field.getType().equals( List.class ) && value != null )
						{
							for ( Object subObj : (List)value )
								nullToEmpty( subObj, subObj.getClass() );
						}
					}
				}
			}
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.REFLECTION_ERROR );
		}
	}

	public static String replaceWildcards( String text, User usuario )
	{
		return text.replaceAll( "%login%", usuario.getLogin() ).replaceAll( "%password%", usuario.getPwd() );
	}

	public static String getUUID()
	{
		return java.util.UUID.randomUUID().toString().replaceAll( "-", "" );
	}

	/**
	 * Limpia textos que introduce el usuario que pueden ser perjudiciales para
	 * la integridad de los datos
	 * 
	 * @param valor
	 *            Texto a limpiar de impurezas
	 * @return Texto limpio para ser guardado en la Base de datos
	 */
	public static String sanitize( String valor )
	{
		if ( valor == null )
			return "";
		valor = valor.trim();
		if ( valor.equals( "<br>" ) || valor.equals( "&nbsp;" ) )
		{
			return "";
		}
		return valor;
	}

	public static void Sleep( long time )
	{
		try
		{
			Thread.sleep( time );
		}
		catch ( InterruptedException e )
		{
			LOG.error( "InterruptedException during a sleep {}", e );
		}
	}

	public static boolean ExistsFile( String fileName )
	{
		return (new File( fileName )).exists();
	}

	public static void DeleteFile( String fileName )
	{
		(new File( fileName )).delete();
	}

	public static void ParseQueryParameters( String query, HashMap<String, String> parameters )
	{
		if ( query != null && query != "" )
		{
			try
			{
				String queries[] = URLDecoder.decode( query, "UTF-8" ).split( "&" );

				for ( int i = 0; i < queries.length; i++ )
				{
					String attrs[] = queries[i].split( "=" );

					if ( attrs.length == 2 )
						parameters.put( attrs[0], attrs[1] );
				}
			}
			catch ( UnsupportedEncodingException e )
			{
				LOG.error( "UnsupportedEncodingException decoding query parameters {}", e );
			}
		}
	}

	public static String getLastYearDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.YEAR, -1 );

		return format.format( now.getTime() );
	}

	public static Date getLastYearDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.YEAR, -1 );

		return now.getTime();
	}

	public static String getLastMonthDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.MONTH, -1 );

		return format.format( now.getTime() );
	}

	public static Date getLastMonthDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.MONTH, -1 );

		return now.getTime();
	}

	public static String getLastWeekDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.WEEK_OF_MONTH, -1 );

		return format.format( now.getTime() );
	}

	public static String getDayOfWeekAsString( Date date, int field, String format )
	{
		SimpleDateFormat formatter = new SimpleDateFormat( format );

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( Calendar.DAY_OF_WEEK, field );

		return formatter.format( calendar.getTime() );
	}

	public static Date getLastWeekDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.WEEK_OF_MONTH, -1 );

		return now.getTime();
	}

	public static String getYesterdayDateAsString()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.DATE, -1 );

		return format.format( now.getTime() );
	}

	public static Date getYesterdayDateAsDate()
	{
		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );
		now.add( Calendar.DATE, -1 );

		return now.getTime();
	}

	public static String getTodayDate()
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd" );

		Calendar now = GregorianCalendar.getInstance();

		now.setTime( new java.util.Date() );

		return format.format( now.getTime() );
	}

	public static double roundDouble( double value, int decimals )
	{
		BigDecimal bd = new BigDecimal( value );

		bd = bd.setScale( decimals, BigDecimal.ROUND_HALF_EVEN );

		return bd.doubleValue();
	}

	public static double roundToNext( double value )
	{
		BigDecimal bd = new BigDecimal( value );

		bd = bd.setScale( 0, BigDecimal.ROUND_UP);

		return bd.doubleValue();
	}

	public static byte lobyte( int value )
	{
		return (byte)(value & 0x00FF);
	}

	public static byte hibyte( int value )
	{
		return (byte)((value & 0xFF00) >> 8);
	}

	public static byte lobyte( short value )
	{
		return (byte)(value & 0x00FF);
	}

	public static byte hibyte( short value )
	{
		return (byte)((value & 0xFF00) >> 8);
	}
	
	public static int loshort( int value )
	{
		return (short)(value & 0x0000FFFF);
	}

	public static int hishort( int value )
	{
		return (short)((value & 0xFFFF0000) >> 16);
	}

	public static int loint( long value )
	{
		return (int)(value & 0x00000000FFFFFFFFL);
	}

	public static int hiint( long value )
	{
		return (int)((value & 0xFFFFFFFF00000000L) >> 32);
	}

	public static short getShort( byte hi, byte lo )
	{
		return (short)(((hi & 0xff) << 8) | (lo & 0xff));
	}

	public static short getShort( short hi, short lo )
	{
		return (short) (((hi & 0xff) << 8) | (lo & 0xff));
	}

	public static int getInt( int hi, int lo )
	{
		return ((hi & 0xffff) << 16) | (lo & 0xffff);
	}

	public static int getInt( short hi, short lo )
	{
		return ((hi & 0xffff) << 16) | (lo & 0xffff);
	}

	
	private static final int wCRCTable[] =
	{ 0X0000, 0XC0C1, 0XC181, 0X0140, 0XC301, 0X03C0, 0X0280, 0XC241, 0XC601, 0X06C0, 0X0780, 0XC741, 0X0500, 0XC5C1, 0XC481, 0X0440, 0XCC01, 0X0CC0, 0X0D80, 0XCD41, 0X0F00, 0XCFC1, 0XCE81, 0X0E40, 0X0A00, 0XCAC1, 0XCB81, 0X0B40, 0XC901, 0X09C0, 0X0880, 0XC841, 0XD801, 0X18C0, 0X1980, 0XD941, 0X1B00, 0XDBC1, 0XDA81, 0X1A40, 0X1E00, 0XDEC1, 0XDF81, 0X1F40, 0XDD01, 0X1DC0, 0X1C80, 0XDC41, 0X1400, 0XD4C1, 0XD581, 0X1540, 0XD701, 0X17C0, 0X1680, 0XD641, 0XD201, 0X12C0, 0X1380, 0XD341, 0X1100, 0XD1C1, 0XD081, 0X1040, 0XF001, 0X30C0, 0X3180, 0XF141, 0X3300, 0XF3C1, 0XF281, 0X3240, 0X3600, 0XF6C1, 0XF781, 0X3740, 0XF501, 0X35C0, 0X3480, 0XF441, 0X3C00, 0XFCC1, 0XFD81, 0X3D40, 0XFF01, 0X3FC0, 0X3E80, 0XFE41, 0XFA01, 0X3AC0, 0X3B80, 0XFB41, 0X3900, 0XF9C1, 0XF881, 0X3840, 0X2800, 0XE8C1, 0XE981, 0X2940, 0XEB01, 0X2BC0, 0X2A80, 0XEA41, 0XEE01, 0X2EC0, 0X2F80, 0XEF41, 0X2D00, 0XEDC1, 0XEC81, 0X2C40, 0XE401, 0X24C0, 0X2580, 0XE541, 0X2700, 0XE7C1, 0XE681, 0X2640, 0X2200, 0XE2C1, 0XE381, 0X2340, 0XE101, 0X21C0, 0X2080, 0XE041, 0XA001, 0X60C0, 0X6180, 0XA141, 0X6300, 0XA3C1, 0XA281, 0X6240, 0X6600, 0XA6C1, 0XA781, 0X6740, 0XA501, 0X65C0, 0X6480, 0XA441, 0X6C00, 0XACC1, 0XAD81, 0X6D40, 0XAF01, 0X6FC0, 0X6E80, 0XAE41, 0XAA01, 0X6AC0, 0X6B80, 0XAB41, 0X6900, 0XA9C1, 0XA881, 0X6840, 0X7800, 0XB8C1, 0XB981, 0X7940, 0XBB01, 0X7BC0, 0X7A80, 0XBA41, 0XBE01, 0X7EC0, 0X7F80, 0XBF41, 0X7D00, 0XBDC1, 0XBC81, 0X7C40, 0XB401, 0X74C0, 0X7580, 0XB541, 0X7700, 0XB7C1, 0XB681, 0X7640, 0X7200, 0XB2C1, 0XB381, 0X7340, 0XB101, 0X71C0, 0X7080, 0XB041, 0X5000, 0X90C1, 0X9181, 0X5140, 0X9301, 0X53C0, 0X5280, 0X9241, 0X9601, 0X56C0, 0X5780, 0X9741, 0X5500, 0X95C1, 0X9481, 0X5440, 0X9C01, 0X5CC0, 0X5D80, 0X9D41, 0X5F00, 0X9FC1, 0X9E81, 0X5E40, 0X5A00, 0X9AC1, 0X9B81, 0X5B40, 0X9901, 0X59C0, 0X5880, 0X9841, 0X8801, 0X48C0, 0X4980, 0X8941, 0X4B00, 0X8BC1, 0X8A81, 0X4A40, 0X4E00, 0X8EC1, 0X8F81, 0X4F40, 0X8D01, 0X4DC0, 0X4C80, 0X8C41, 0X4400, 0X84C1, 0X8581, 0X4540, 0X8701, 0X47C0, 0X4680, 0X8641, 0X8201, 0X42C0, 0X4380, 0X8341, 0X4100, 0X81C1, 0X8081, 0X4040 };

	public static int calculate_crc( int nData[], int length )
	{
		int index;
		int crc = 0xFFFF;

		for ( int i = 0; i < length; i++ )
		{
			index = (nData[i] ^ crc) & 0x00FF;
			crc >>= 8;
			crc ^= wCRCTable[index];
		}

		return crc;
	}

	public static void getParameters( String queryString, HashMap<String, String> parameters ) throws BaseException
	{
		if ( queryString != null && queryString != "" )
		{
			try
			{
				String queries[] = queryString.split( "&" ); //URLDecoder.decode( queryString, "UTF-8" ).split( "&" );

				for ( int i = 0; i < queries.length; i++ )
				{
					String attrs[] = queries[i].split( "=" );

					if ( attrs.length == 2 )
						parameters.put( attrs[0], attrs[1] );
				}
			}
			catch ( Throwable e )
			{
				throw new BaseException( e, LOG, BaseException.DECODE_ERROR );
			}
		}
	}

	public static int getDecimalValue( String format )
	{
		int index = format.indexOf( "." );

		return index == -1 ? 0 : format.substring( index + 1 ).length();
	}

	/**
	 * @author Dismer Ronda
	 * 
	 * @param origin
	 * @param substring
	 * @return
	 */
	public static String removeSubString( String origin, String substring )
	{
		if ( origin != null && substring != null )
		{
			int pos = origin.indexOf( substring );

			return pos != -1 ? origin.substring( 0, pos ) + origin.substring( pos + substring.length(), origin.length() ) : origin;
		}
		else
			return origin;
	}

	/**
	 * Cast and validate objet form String to Integer
	 * 
	 * @param object
	 * @return
	 */
	public static Integer castToIntegerFromObject( Object object ) throws BaseException
	{
		Integer result = null;

		try
		{
			if ( object instanceof String )
			{
				if ( (String)object != null && ((String)object).compareTo( "" ) != 0 )
				{
					result = Integer.parseInt( (String)object );
				}
			}
			else
			{
				result = (Integer)object;
			}
		}
		catch ( NumberFormatException exNum )
		{
			throw new BaseException( new Throwable( "se esperaba número" ), LOG, BaseException.FORMAT_ERROR_INT );
		}

		return result;
	}

	/**
	 * Cast and validate objet form String to Double
	 * 
	 * @param object
	 * @return
	 */
	public static Double castToDoubleFromObject( Object object ) throws BaseException
	{
		Double result = null;

		try
		{
			if ( object instanceof String )
			{
				if ( (String)object != null && ((String)object).compareTo( "" ) != 0 )
				{
					result = Double.parseDouble( (String)object );
				}
			}
			else
			{
				result = (Double)object;
			}
		}
		catch ( NumberFormatException exNum )
		{
			throw new BaseException( new Throwable( "se esperaba double" ), LOG, BaseException.FORMAT_ERROR_INT );
		}

		return result;
	}

	public static String getUserLoginFromRequest( HashMap<String, String> parameters )
	{
		String login = parameters.get( "login" );

		if ( login == null || login.isEmpty() )
			login = parameters.get( "user" );

		if ( login == null || login.isEmpty() )
			login = parameters.get( "usr" );

		if ( login == null || login.isEmpty() )
			login = parameters.get( "usuario" );

		if ( login == null )
			login = "";

		return login;
	}

	public static String getUserPasswordFromRequest( HashMap<String, String> parameters )
	{
		String password = parameters.get( "pwd" );

		if ( password == null || password.isEmpty() )
			password = parameters.get( "password" );

		if ( password == null )
			password = "";

		return password;
	}

	public static Method getGetter( String name, Class clazz )
	{
		Method[] methods = clazz.getDeclaredMethods();

		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "get" + name ) )
				return method;
		}

		Class superClazz = clazz.getSuperclass();

		if ( superClazz != null )
			return getGetter( name, superClazz );

		return null;
	}

	public static Method getSetter( String name, Class clazz )
	{
		Method[] methods = clazz.getDeclaredMethods();

		for ( Method method : methods )
		{
			if ( method.getName().equalsIgnoreCase( "set" + name ) )
				return method;
		}

		Class superClazz = clazz.getSuperclass();

		if ( superClazz != null )
			return getGetter( name, superClazz );

		return null;
	}

	public static Field getField( String name, Class clazz )
	{
		Field[] fields = clazz.getDeclaredFields();

		for ( Field field : fields )
		{
			if ( field.getName().equalsIgnoreCase( name ) )
				return field;
		}

		Class superClazz = clazz.getSuperclass();

		if ( superClazz != null )
			return getField( name, superClazz );

		return null;
	}

	public static Object getFieldObjectInClass( Object obj, String name, Class clazz ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Field field = getField( name, clazz );

		if ( field != null )
		{
			Method getter = getGetter( name, clazz );

			if ( getter != null )
			{
				Object empty[] = new Object[] {};

				return getter.invoke( obj, empty );
			}
		}

		return null;
	}

	public static Object getFieldObject( Object obj, String name ) throws BaseException
	{
		try
		{
			int p = name.indexOf( "." );

			String field = p != -1 ? name.substring( 0, p ) : name;
			String rest = p != -1 ? name.substring( p + 1 ) : "";

			Object subObj = getFieldObjectInClass( obj, field, obj.getClass() );

			if ( rest.isEmpty() )
				return subObj;

			return getFieldObject( subObj, rest );
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.JSON_STRING_ERROR );
		}
	}

	public static Class getMethodClass( Class classZ, String name, Class types[] ) throws BaseException
	{
		try
		{
			return classZ.getMethod( name, types ).getReturnType();
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.REFLECTION_ERROR );
		}
	}

	public static Map<String, Field> getAllFields( Class clazz )
	{

		Map<String, Field> fields = new HashMap<String, Field>();

		Class cl = clazz;
		while ( cl != null && !(cl.getName().equalsIgnoreCase( "java.lang.Object" )) )
		{
			Field[] fieldsClass = cl.getDeclaredFields();

			for ( Field field : fieldsClass )
			{
				fields.put( field.getName(), field );
			}
			cl = cl.getSuperclass();
		}

		return fields;
	}

	public static Class getFieldClass( Class classZ, String name ) throws BaseException
	{
		try
		{
			StringTokenizer st = new StringTokenizer( name, "." );
			Map<String, Field> metodos = getAllFields( classZ );
			Field field = null;
			while ( st.hasMoreTokens() )
			{
				String atributo = st.nextToken();
				field = metodos.get( atributo );
				if ( field == null )
				{
					throw new Exception( "field unknown: " + atributo + " in " + name );
				}
				Class tipo = field.getType();
				metodos = getAllFields( tipo );
			}

			if ( field == null )
			{
				throw new BaseException( new Exception( "field " + name + " not found" ), LOG, BaseException.REFLECTION_ERROR );
			}

			return field.getType();
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
			{
				throw (BaseException)e;
			}

			throw new BaseException( e, LOG, BaseException.REFLECTION_ERROR );
		}
	}

	public static String addCause( String cause, String newCause )
	{
		if ( !cause.isEmpty() )
			cause += ", ";
		cause += newCause;

		return cause;
	}

/*	public static boolean evaluateCondition( String condition, Map<String, Double> map, boolean exception ) throws Throwable
	{
		try
		{
			JexlEngine jexl = new JexlEngine();
			jexl.setStrict( true );
			jexl.setSilent( false );

			Expression e = jexl.createExpression( condition );

			// Create a context and add data
			JexlContext jc = new MapContext();

			Iterator it = map.entrySet().iterator();
			while ( it.hasNext() )
			{
				Map.Entry pairs = (Map.Entry)it.next();

				jc.set( (String)pairs.getKey(), pairs.getValue() );
			}

			// Now evaluate the expression, getting the result
			Object o = e.evaluate( jc );

			return (o instanceof Boolean) ? (Boolean)o : false;
		}
		catch ( Throwable e )
		{
			LOG.info( "exception evaluating " + condition + " " + e.getMessage() );

			if ( exception )
				throw e;
		}

		return false;
	}*/

	public static int getRedFromRgb( String color )
	{
		return Integer.valueOf( color.substring( 1, 3 ), 16 );
	}

	public static int getGreenFromRgb( String color )
	{
		return Integer.valueOf( color.substring( 3, 5 ), 16 );
	}

	public static int getBlueFromRgb( String color )
	{
		return Integer.valueOf( color.substring( 5, 7 ), 16 );
	}
	
	public static String [] getTimezones()
	{
		return new String[]
		{
			"America/Adak",
			"America/Atka",
			"America/Anchorage",
			"America/Juneau",
			"America/Nome",
			"America/Yakutat",
			"America/Dawson",
			"America/Danmarkshavn",
			"America/Ensenada",
			"America/Los_Angeles",
			"America/Tijuana",
			"America/Vancouver",
			"America/Whitehorse",
			"America/Boise",
			"America/Cambridge_Bay",
			"America/Chihuahua",
			"America/Dawson_Creek",
			"America/Denver",
			"America/Edmonton",
			"America/Hermosillo",
			"America/Inuvik",
			"America/Mazatlan",
			"America/Phoenix",
			"America/Shiprock",
			"America/Yellowknife",
			"America/Belize",
			"America/Cancun",
			"America/Chicago",
			"America/Costa_Rica",
			"America/El_Salvador",
			"America/Guatemala",
			"America/Managua",
			"America/Menominee",
			"America/Merida",
			"America/Mexico_City",
			"America/Monterrey",
			"America/North_Dakota/Center",
			"America/Rainy_River",
			"America/Rankin_Inlet",
			"America/Regina",
			"America/Swift_Current",
			"America/Tegucigalpa",
			"America/Winnipeg",
			"America/Bogota",
			"America/Cayman",
			"America/Detroit",
			"America/Eirunepe",
			"America/Fort_Wayne",
			"America/Grand_Turk",
			"America/Guayaquil",
			"America/Havana",
			"America/Indiana/Indianapolis",
			"America/Indiana/Knox",
			"America/Indiana/Marengo",
			"America/Indiana/Vevay",
			"America/Indianapolis",
			"America/Iqaluit",
			"America/Jamaica",
			"America/Kentucky/Louisville",
			"America/Kentucky/Monticello",
			"America/Knox_IN",
			"America/Lima",
			"America/Louisville",
			"America/Montreal",
			"America/Nassau",
			"America/New_York",
			"America/Nipigon",
			"America/Panama",
			"America/Pangnirtung",
			"America/Port-au-Prince",
			"America/Porto_Acre",
			"America/Rio_Branco",
			"America/Thunder_Bay",
			"America/Toronto",
			"America/Anguilla",
			"America/Antigua",
			"America/Aruba",
			"America/Asuncion",
			"America/Barbados",
			"America/Boa_Vista",
			"America/Campo_Grande",
			"America/Caracas",
			"America/Cuiaba",
			"America/Curacao",
			"America/Dominica",
			"America/Glace_Bay",
			"America/Goose_Bay",
			"America/Grenada",
			"America/Guadeloupe",
			"America/Guyana",
			"America/Halifax",
			"America/La_Paz",
			"America/Manaus",
			"America/Martinique",
			"America/Montserrat",
			"America/Port_of_Spain",
			"America/Porto_Velho",
			"America/Puerto_Rico",
			"America/Santiago",
			"America/Santo_Domingo",
			"America/St_Kitts",
			"America/St_Lucia",
			"America/St_Thomas",
			"America/St_Vincent",
			"America/Thule",
			"America/Tortola",
			"America/Virgin",
			"America/Araguaina",
			"America/Bahia",
			"America/Belem",
			"America/Buenos_Aires",
			"America/Catamarca",
			"America/Cayenne",
			"America/Cordoba",
			"America/Fortaleza",
			"America/Godthab",
			"America/Jujuy",
			"America/Maceio",
			"America/Mendoza",
			"America/Miquelon",
			"America/Montevideo",
			"America/Paramaribo",
			"America/Recife",
			"America/Rosario",
			"America/Sao_Paulo",
			"America/Noronha",
			"America/Scoresbysund",
			"America/St_Johns",

			"Europe/Belfast",
			"Europe/Dublin",
			"Europe/Lisbon",
			"Europe/London",
			"Europe/Amsterdam",
			"Europe/Andorra",
			"Europe/Belgrade",
			"Europe/Berlin",
			"Europe/Bratislava",
			"Europe/Brussels",
			"Europe/Budapest",
			"Europe/Copenhagen",
			"Europe/Gibraltar",
			"Europe/Ljubljana",
			"Europe/Luxembourg",
			"Europe/Madrid",
			"Europe/Malta",
			"Europe/Monaco",
			"Europe/Oslo",
			"Europe/Paris",
			"Europe/Prague",
			"Europe/Rome",
			"Europe/San_Marino",
			"Europe/Sarajevo",
			"Europe/Skopje",
			"Europe/Stockholm",
			"Europe/Tirane",
			"Europe/Vaduz",
			"Europe/Vatican",
			"Europe/Vienna",
			"Europe/Warsaw",
			"Europe/Zagreb",
			"Europe/Zurich",
			"Europe/Athens",
			"Europe/Bucharest",
			"Europe/Chisinau",
			"Europe/Helsinki",
			"Europe/Istanbul",
			"Europe/Kaliningrad",
			"Europe/Kiev",
			"Europe/Minsk",
			"Europe/Nicosia",
			"Europe/Riga",
			"Europe/Simferopol",
			"Europe/Sofia",
			"Europe/Tallinn",
			"Europe/Tiraspol",
			"Europe/Uzhgorod",
			"Europe/Vilnius",
			"Europe/Zaporozhye",
			"Europe/Moscow",
			"Europe/Samara",
			
			"Africa/Abidjan",
			"Africa/Accra",
			"Africa/Bamako",
			"Africa/Banjul",
			"Africa/Bissau",
			"Africa/Casablanca",
			"Africa/Conakry",
			"Africa/Dakar",
			"Africa/El_Aaiun",
			"Africa/Freetown",
			"Africa/Lome",
			"Africa/Monrovia",
			"Africa/Nouakchott",
			"Africa/Ouagadougou",
			"Africa/Sao_Tome",
			"Africa/Timbuktu",
			"Africa/Algiers",
			"Africa/Bangui",
			"Africa/Brazzaville",
			"Africa/Ceuta",
			"Africa/Douala",
			"Africa/Kinshasa",
			"Africa/Lagos",
			"Africa/Libreville",
			"Africa/Luanda",
			"Africa/Malabo",
			"Africa/Ndjamena",
			"Africa/Niamey",
			"Africa/Porto-Novo",
			"Africa/Tunis",
			"Africa/Windhoek",
			"Arctic/Longyearbyen",
			"Atlantic/Jan_Mayen",
			"Africa/Blantyre",
			"Africa/Bujumbura",
			"Africa/Cairo",
			"Africa/Gaborone",
			"Africa/Harare",
			"Africa/Johannesburg",
			"Africa/Kigali",
			"Africa/Lubumbashi",
			"Africa/Lusaka",
			"Africa/Maputo",
			"Africa/Maseru",
			"Africa/Mbabane",
			"Africa/Tripoli",
			"Africa/Addis_Ababa",
			"Africa/Asmera",
			"Africa/Dar_es_Salaam",
			"Africa/Djibouti",
			"Africa/Kampala",
			"Africa/Khartoum",
			"Africa/Mogadishu",
			"Africa/Nairobi",

			"Asia/Amman",
			"Asia/Beirut",
			"Asia/Damascus",
			"Asia/Gaza",
			"Asia/Istanbul",
			"Asia/Jerusalem",
			"Asia/Nicosia",
			"Asia/Tel_Aviv",
			"Asia/Aden",
			"Asia/Baghdad",
			"Asia/Bahrain",
			"Asia/Kuwait",
			"Asia/Qatar",
			"Asia/Riyadh",
			"Asia/Riyadh87",
			"Asia/Riyadh88",
			"Asia/Riyadh89",
			"Asia/Aqtau",
			"Asia/Baku",
			"Asia/Dubai",
			"Asia/Muscat",
			"Asia/Oral",
			"Asia/Tbilisi",
			"Asia/Yerevan",
			"Asia/Kabul",
			"Asia/Aqtobe",
			"Asia/Ashgabat",
			"Asia/Ashkhabad",
			"Asia/Bishkek",
			"Asia/Dushanbe",
			"Asia/Karachi",
			"Asia/Samarkand",
			"Asia/Tashkent",
			"Asia/Yekaterinburg",
			"Asia/Tehran",
			"Asia/Calcutta",
			"Asia/Katmandu",
			"Asia/Almaty",
			"Asia/Colombo",
			"Asia/Dacca",
			"Asia/Dhaka",
			"Asia/Novosibirsk",
			"Asia/Omsk",
			"Asia/Qyzylorda",
			"Asia/Thimbu",
			"Asia/Thimphu",
			"Asia/Rangoon",
			"Asia/Bangkok",
			"Asia/Hovd",
			"Asia/Jakarta",
			"Asia/Krasnoyarsk",
			"Asia/Phnom_Penh",
			"Asia/Pontianak",
			"Asia/Saigon",
			"Asia/Vientiane",
			"Asia/Brunei",
			"Asia/Chongqing",
			"Asia/Chungking",
			"Asia/Harbin",
			"Asia/Hong_Kong",
			"Asia/Irkutsk",
			"Asia/Kashgar",
			"Asia/Kuala_Lumpur",
			"Asia/Kuching",
			"Asia/Macao",
			"Asia/Macau",
			"Asia/Makassar",
			"Asia/Manila",
			"Asia/Shanghai",
			"Asia/Singapore",
			"Asia/Taipei",
			"Asia/Ujung_Pandang",
			"Asia/Ulaanbaatar",
			"Asia/Ulan_Bator",
			"Asia/Urumqi",
			"Asia/Choibalsan",
			"Asia/Dili",
			"Asia/Jayapura",
			"Asia/Pyongyang",
			"Asia/Seoul",
			"Asia/Tokyo",
			"Asia/Yakutsk",
			"Asia/Sakhalin",
			"Asia/Vladivostok",
			"Asia/Magadan",
			"Asia/Anadyr",
			"Asia/Kamchatka",
			
			"Australia/LHI",
			"Australia/Perth",
			"Australia/West",
			"Australia/Adelaide",
			"Australia/Broken_Hill",
			"Australia/Darwin",
			"Australia/North",
			"Australia/South",
			"Australia/Yancowinna",
			"Australia/ACT",
			"Australia/Brisbane",
			"Australia/Canberra",
			"Australia/Hobart",
			"Australia/Lindeman",
			"Australia/Melbourne",
			"Australia/NSW",
			"Australia/Queensland",
			"Australia/Sydney",
			"Australia/Tasmania",
			"Australia/Victoria",
			"Australia/Lord_Howe",
			
			"Pacific/Apia",
			"Pacific/Midway",
			"Pacific/Niue",
			"Pacific/Pago_Pago",
			"Pacific/Samoa",
			"Pacific/Fakaofo",
			"Pacific/Honolulu",
			"Pacific/Johnston",
			"Pacific/Rarotonga",
			"Pacific/Tahiti",
			"Pacific/Marquesas",
			"Pacific/Gambier",
			"Pacific/Pitcairn",
			"Pacific/Easter",
			"Pacific/Galapagos",
			"Pacific/Guam",
			"Pacific/Port_Moresby",
			"Pacific/Saipan",
			"Pacific/Truk",
			"Pacific/Yap",
			"Pacific/Efate",
			"Pacific/Guadalcanal",
			"Pacific/Kosrae",
			"Pacific/Noumea",
			"Pacific/Ponape",
			"Pacific/Norfolk",
			"Pacific/Auckland",
			"Pacific/Fiji",
			"Pacific/Funafuti",
			"Pacific/Kwajalein",
			"Pacific/Majuro",
			"Pacific/Nauru",
			"Pacific/Tarawa",
			"Pacific/Wake",
			"Pacific/Wallis",
			"Pacific/Chatham",
			"Pacific/Enderbury",
			"Pacific/Tongatapu",
			"Pacific/Kiritimati",		
			"Pacific/Palau",

			"Atlantic/Bermuda",
			"Atlantic/Stanley",
			"Atlantic/South_Georgia",
			"Atlantic/Azores",
			"Atlantic/Cape_Verde",
			"Atlantic/Canary",
			"Atlantic/Faeroe",
			"Atlantic/Madeira",
			"Atlantic/Reykjavik",
			"Atlantic/St_Helena",

			"MIT",
			"HST",
			"AST",
			"PST",
			"MST",
			"PNT",
			"CST",
			"EST",
			"IET",
			"PRT",
			"CNT",
			"AGT",
			"BET",
			"GMT",
			"UCT",
			"UTC",
			"WET",
			"CET",
			"ECT",
			"MET",
			"ART",
			"CAT",
			"EET",
			"EAT",
			"NET",
			"PLT",
			"IST",
			"BST",
			"VST",
			"CTT",
			"PRC",
			"JST",
			"ROK",
			"ACT",
			"AET",
			"SST",
			"NST",
			
			/*
			"Canada/Pacific",
			"Canada/Yukon",
			"Canada/Mountain",
			"Canada/Central",
			"Canada/East-Saskatchewan",
			"Canada/Saskatchewan",
			"Canada/Eastern",
			"Canada/Atlantic",
			"Canada/Newfoundland",

			"Mexico/BajaNorte",
			"Mexico/BajaSur",
			"Mexico/General",
			
			"Chile/EasterIsland",
			"Chile/Continental",

			"Brazil/Acre",
			"Brazil/West",
			"Brazil/East",
			"Brazil/DeNoronha",

			"US/Samoa",
			"US/Aleutian",
			"US/Hawaii",
			"US/Alaska",
			"US/Pacific",
			"US/Pacific-New",
			"US/Central",
			"US/Arizona",
			"US/Mountain",
			"US/East-Indiana",
			"US/Eastern",
			"US/Indiana-Starke",
			"US/Michigan",

			"Navajo",
			"Cuba",
			"Jamaica",
			"GB",
			"Iceland",
			"Portugal",
			"Eire",
			"Greenwich",
			"Poland",
			"Egypt",
			"Israel",
			"Libya",
			"Turkey",
			
			"GB-Eire",
			"GMT0",
			"Universal",
			"Zulu",
			
			"Indian/Antananarivo",
			"Indian/Comoro",
			"Indian/Mayotte",
			"Indian/Mahe",
			"Indian/Mauritius",
			"Indian/Reunion",
			"Indian/Kerguelen",
			"Indian/Maldives",
			"Indian/Chagos",
			"Indian/Cocos",
			"Indian/Christmas",
			
			"W-SU",
			"Mideast/Riyadh87",
			"Mideast/Riyadh88",
			"Mideast/Riyadh89",
			"Iran",

			"Hongkong",
			"Singapore",

			"Japan",
			"Antarctica/DumontDUrville",

			"Antarctica/McMurdo",
			"Antarctica/South_Pole",
			"Kwajalein",
			"NZ",
			"NZ-CHAT",*/

		};
	}
	
	public static String getDottedMacAddress( String serial )
	{
		try 
		{
			return serial.substring( 0, 2 ) + ":" + serial.substring( 2, 4 ) + ":" + serial.substring( 4, 6 ) + ":" + serial.substring( 6, 8 ) + ":" + serial.substring( 8, 10 ) + ":" + serial.substring( 10 ); 
		}
		catch ( Throwable e )
		{
			return serial;
		}
	}
	
	public static String toJson( Object obj, boolean empty ) throws BaseException
	{
		try
		{
			if ( empty )
				emptyToNull( obj, obj.getClass() );

			return JsonSerializer.toJson( obj );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.JSON_STRING_ERROR );
		}
	}

	public static String toJson( Object obj ) throws BaseException
	{
		return toJson( obj, true );
	}

	public static Object toPojo( String text, Class clazz, boolean empty ) throws BaseException
	{
		try
		{
			Object obj = JsonSerializer.toPojo( text, clazz );

			if ( obj != null && empty )
				nullToEmpty( obj, clazz );

			return obj;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.JSON_STRING_ERROR );
		}
	}

	public static ArrayList toArrayList( String text, Type listType ) throws BaseException
	{
		try
		{
			return JsonSerializer.toArrayList( text, listType );
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.JSON_STRING_ERROR );
		}
	}

	
	public static String formatTimeSpan( int years, int days, int hours, int mins, int secs )
	{
		String ret = "";
		
		int parts = 0;
		
		if ( years > 0 && parts < 2 )
		{
			ret += String.format( "%02dy", years );
			parts++;
		}

		if ( days > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02dd", days );
			parts++;
		}
		
		if ( hours > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02dh", hours );
			parts++;
		}
		
		if ( mins > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02d'", mins ); 
			parts++;
		}
		
		if ( secs > 0 && parts < 2 )
		{
			if ( !ret.isEmpty() )
				ret += " ";
			ret += String.format( "%02d\"", secs );
			parts++;
		}		
		
		return ret;
	}
	
	public static String getDuration( long from, long to )
	{
		try 
		{
			long time = (CalendarUtils.getServerCalendarFromDateLong( to ).getTimeInMillis() - CalendarUtils.getServerCalendarFromDateLong( from ).getTimeInMillis()) / 1000;
			
		    int years = (int) time / (365 * 3600 * 24);
		    int remainder = (int) time - years * 365 * 3600 * 24;
		    
		    int days = (int) remainder / (3600*24);
		    remainder = (int) remainder - days * 3600 * 24;

		    int hours = (int) remainder / 3600;
		    remainder = (int) remainder - hours * 3600;
		    
		    int mins = remainder / 60;
		    remainder = remainder - mins * 60;
		    
		    int secs = remainder;
		    
			return Utils.formatTimeSpan( years, days, hours, mins, secs );
		} 
		catch (Throwable e) 
		{
			Utils.logException( e, LOG );
		}
		
		return "";
	}

	public static String getDuration( long duration )
	{
		try 
		{
		    int years = (int) duration / (365 * 3600 * 24);
		    int remainder = (int) duration - years * 365 * 3600 * 24;
		    
		    int days = (int) remainder / (3600*24);
		    remainder = (int) remainder - days * 3600 * 24;
		    
		    int hours = (int) remainder / 3600;
		    remainder = (int) remainder - hours * 3600;
		    
		    int mins = remainder / 60;
		    remainder = remainder - mins * 60;
		    
		    int secs = remainder;
		    
			return Utils.formatTimeSpan( years, days, hours, mins, secs );
		} 
		catch (Throwable e) 
		{
		}
		
		return "";
	}

	public static String formatTimeSpanShort( int years, int days, int hours, int mins, int secs )
	{
		if ( years > 0 )
			return String.format( "%02dy", years );
		else if ( days > 0 )
			return String.format( "%02dd", days );
		else if ( hours > 0 )
			return String.format( "%02dh", hours );
		else if ( mins > 0 )
			return String.format( "%02d'", mins ); 
		else if ( secs > 0 )
			return String.format( "%02d\"", secs );
		
		return "";
	}
	
	public static String getShortDuration( long duration )
	{
		try 
		{
		    int years = (int) duration / (365 * 3600 * 24);
		    int remainder = (int) duration - years * 365 * 3600 * 24;
		    
		    int days = (int) remainder / (3600*24);
		    remainder = (int) remainder - days * 3600 * 24;
		    
		    int hours = (int) remainder / 3600;
		    remainder = (int) remainder - hours * 3600;
		    
		    int mins = remainder / 60;
		    remainder = remainder - mins * 60;
		    
		    int secs = remainder;
		    
			return Utils.formatTimeSpanShort( years, days, hours, mins, secs );
		} 
		catch (Throwable e) 
		{
		}
		
		return "";
	}
	
	public static long getDurationInSeconds( long from, long to )
	{
		try 
		{
			return (CalendarUtils.getServerCalendarFromDateLong( to ).getTimeInMillis() - CalendarUtils.getServerCalendarFromDateLong( from ).getTimeInMillis()) / 1000;
		} 
		catch (Throwable e) 
		{
		}
		
		return 0;
	}

	public static List<SolidColor> getLindeColors()
	{
		List<SolidColor> colors = new ArrayList<SolidColor>();
		
		colors.add( new SolidColor( 0x00, 0x8A, 0xC4 ) );
		colors.add( new SolidColor( 0x42, 0x81, 0xAF ) ); 
		colors.add( new SolidColor( 0x7B, 0xA0, 0xC5 ) );
		colors.add( new SolidColor( 0xB1, 0xC4, 0xDC ) );
		colors.add( new SolidColor( 0x02, 0x3B, 0x5A ) );
		colors.add( new SolidColor( 0x39, 0x60, 0x83 ) );
		colors.add( new SolidColor( 0x68, 0x84, 0xA3 ) );
		colors.add( new SolidColor( 0xA1, 0xB1, 0xC7 ) );
		colors.add( new SolidColor( 0x00, 0x5D, 0x64 ) );
		colors.add( new SolidColor( 0x6B, 0x92, 0xA7 ) );

		return colors;
	}
	
	public static String getDotedVersion( String version, int radix )
	{
		try 
		{
			int a = Integer.parseInt( version.substring( 0, 2 ), radix );
			int b = Integer.parseInt( version.substring( 2, 4 ), radix );
			int c = Integer.parseInt( version.substring( 4, 6 ), radix );
			int d = Integer.parseInt( version.substring( 6, 8 ), radix );
			
			return a + "." + b + "." + c + "." + d;
		}
		catch ( Throwable e )
		{
			return version;
		}
	}

	public static long getSecondsFromLastCall( long date )
	{
		try
		{
			Calendar calendarThen = CalendarUtils.getServerCalendarFromDateLong( date );
			Calendar calendarNow = CalendarUtils.getServerCurrentCalendar();
			
			return (calendarNow.getTimeInMillis() - calendarThen.getTimeInMillis() ) / 1000;
		}
		catch ( Throwable e )
		{
			return Constants.ONE_YEAR;
		}
	}
	
	public static String readFile(String filename)
	{
	   String content = "";
	   
	   File file = new File(filename); 
	   try 
	   {
	       FileReader reader = new FileReader(file);
	       
           BufferedReader bufferedReader = new BufferedReader( reader );

           String line = null;
           while ( (line = bufferedReader.readLine()) != null ) 
           {
        	   if ( !content.isEmpty())
        		   content += "\n";
        	   
               content += line;
           }    

           bufferedReader.close();
	   } 
	   catch ( Throwable e) 
	   {
	       Utils.logException( e, LOG );
	   }

	   return content;
	}

	public static byte [] readBinaryFile( String fileName )
	{
		try
		{
			File file = new File( fileName );
		    byte[] fileData = new byte[(int) file.length()];
		    DataInputStream dis = new DataInputStream(new FileInputStream(file));
		    dis.readFully( fileData );
		    dis.close();
		    
		    return fileData;
		}
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}
	
	public static InputStream readBinaryFileAsStream( String fileName )
	{
		try
		{
			File file = new File( fileName );
		    byte[] fileData = new byte[(int) file.length()];
		    DataInputStream dis = new DataInputStream(new FileInputStream(file));
		    dis.readFully( fileData );
		    
		    return dis;
		}
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}

	public static boolean writeBinaryFile( String fileName, byte [] buffer )
	{
		try 
		{
            FileOutputStream outputStream = new FileOutputStream( fileName );

            outputStream.write(buffer);

            outputStream.close();       
        }
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			return false;
		}
		
		return true;
	}

	public static boolean writeBinaryFile( String fileName, InputStream is )
	{
		try 
		{
            FileOutputStream outputStream = new FileOutputStream( fileName );

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }            

            outputStream.close();       
        }
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			return false;
		}
		
		return true;
	}

	public static boolean writeBinaryFile( InputStream is, OutputStream os )
	{
		try 
		{
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }            

            os.close();       
        }
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			return false;
		}
		
		return true;
	}

	public static boolean writeBinaryFile( InputStream is, String fileName )
	{
		try
		{
			FileOutputStream os = new FileOutputStream( fileName );

			Utils.writeBinaryFile( is, os );

			os.close();
			
			return true;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return false;
	}
	
	public static void writeFile( String filename, String text, boolean append )
	{
	   try 
	   {
	       FileWriter writer = new FileWriter( filename, append );
	       
           BufferedWriter bufferedWriter = new BufferedWriter(writer);

           bufferedWriter.write( text );
           //bufferedWriter.newLine();

           bufferedWriter.close();
	   } 
	   catch (IOException e) 
	   {
	       Utils.logException( e, LOG );
	   }
	}	

    public static int getBDCValue( byte value )
    {
    	return value & 0xF0 * 10 + value & 0x0F;
    }
    
    public static long getDate( byte year, byte month, byte day, byte hour, byte minute, byte second )
    {
    	return CalendarUtils.getTimeAsLong( Utils.bcdToDecimal( year ) + 2000, 
    			Utils.bcdToDecimal(month),
    			Utils.bcdToDecimal(day),
    			Utils.bcdToDecimal(hour),
    			Utils.bcdToDecimal(minute),
    			Utils.bcdToDecimal(second) );
    }
    
	public static long getHowMuchToWaitForFirstSecondOfNextHour()
	{
		Calendar calendar = Calendar.getInstance();

		calendar.add( Calendar.HOUR_OF_DAY, 1 );

		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime().getTime() - Calendar.getInstance().getTime().getTime();
	}

	public static long getHowMuchToWaitForFirstSecondOfNextMinute()
	{
		Calendar calendar = Calendar.getInstance();

		calendar.add( Calendar.MINUTE, 1 );

		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime().getTime() - Calendar.getInstance().getTime().getTime();
	}
	
	public static DateRange getDateRange( Integer id, Calendar calendar )
	{
		if ( id == null )
			return new DateRange( null, null );
		
		switch ( id.intValue() )
		{
			case Constants.DATE_TODAY:
			{
				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( calendar ) );
			}
			
			case Constants.DATE_YESTERDAY:
			{
				calendar.add( Calendar.DATE, -1 );
				
				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( calendar ) );
			}
			
			case Constants.DATE_LAST_WEEK:
			{
				calendar.add( Calendar.DATE, -7 );

				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( CalendarUtils.getServerCurrentCalendar() ) );
			}
			
			case Constants.DATE_LAST_MONTH:
			{
				calendar.add( Calendar.MONTH, -1 );
				
				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( CalendarUtils.getServerCurrentCalendar() ) );
			}
			
			case Constants.DATE_LAST_THREMESTER:
			{
				calendar.add( Calendar.MONTH, -3 );
				
				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( CalendarUtils.getServerCurrentCalendar() ) );
			}
			
			case Constants.DATE_LAST_SEMESTER:
			{
				calendar.add( Calendar.MONTH, -6 );
				
				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( CalendarUtils.getServerCurrentCalendar() ) );
			}
			
			case Constants.DATE_LAST_YEAR:
			{
				calendar.add( Calendar.YEAR, -1 );
				
				return new DateRange( CalendarUtils.getDayFirstSecondAsLong( calendar ), CalendarUtils.getDayLastSecondAsLong( CalendarUtils.getServerCurrentCalendar() ) );
			}
			
			default:
				return new DateRange( null, null );
		}
	}

	public static void fillComboDates( ComboBox comboDate, AppContext ctx )
	{
		comboDate.removeAllItems();
		
		for ( int i = Constants.DATE_TODAY; i <= Constants.DATE_LAST_YEAR; i++ )
		{
			comboDate.addItem( i );
			comboDate.setItemCaption( i,  ctx.getString( "dates." + i ) );
		}
	}
		
	public static String getPaperSize( int size )
	{
		return size >= 0 && size <= 3 ? paperSizes[size] : "A4";
	}

	public static void fillComboPaperSize( ComboBox comboSize )
	{
		for ( int i = 0; i < paperSizes.length; i++ )
		{
			comboSize.addItem( i );
			comboSize.setItemCaption( i, paperSizes[i] );
		}
	}

	public static String cmdExec( String cmdLine )
	{
		String line;
		String output = "";
		try
		{
			String[] cmd = { "/bin/sh", "-c", cmdLine };
			
			Process p = Runtime.getRuntime().exec( cmd );
			
			BufferedReader input = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			while ( (line = input.readLine()) != null )
			{
				output += (line + '\n');
			}
			input.close();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return output;
	}
	
	public static BufferedImage addOverlaysToImage( BufferedImage base, List<OverlayImage> overlays )
	{
		BufferedImage combined = new BufferedImage( base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB );

		Graphics g = combined.getGraphics();
		
		Hashtable attributes = new Hashtable();
	    attributes.put( TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD );
	    
		g.drawImage( base, 0, 0, null );
		g.setFont( g.getFont().deriveFont( 16f ) );
		g.setFont( g.getFont().deriveFont( attributes ) );

		FontMetrics metrics = g.getFontMetrics();
		
		for ( OverlayImage overlay : overlays )
		{
			int textWidth = metrics.stringWidth( overlay.getText() );
			int minWidth = metrics.stringWidth( "000" );
			
			int maxWidth = Math.max( textWidth, minWidth );

			int x = overlay.getX();
			int y = overlay.getY();
			int w = 3 * maxWidth / 2;
			int h = 2 * metrics.getHeight();
			
			Polygon p = new Polygon();
			p.addPoint( x, y );
			p.addPoint( x, (int)(y - 1.15 * h) );
			p.addPoint( x + w, (int)(y - 1.15 * h) );
			p.addPoint( x + w, (int)(y - 0.15 * h) );
			p.addPoint( (int)(x + 0.15 * w), (int)(y - 0.15 * h) );
					
			g.setColor( new Color( 0xE6, 0x0D, 0x2E ) );
			g.fillPolygon( p );
			
			g.setColor( Color.WHITE );
			g.drawString( overlay.getText(), (int)(x + w/2.0 - textWidth/2.0), (int)(y - 0.15 * h - h/3.0) );
		}
		
		
		return combined;
	}

	public static BufferedImage resizeSubImage( BufferedImage image, int orgX, int orgY, int orgW, int orgH, int destW, int destH )
	{
        BufferedImage outputImage = new BufferedImage( orgW, orgH, BufferedImage.TYPE_INT_ARGB ); //image.getType() );

        Graphics g = outputImage.createGraphics();
        g.drawImage( image, 0, 0, orgW, orgH, orgX, orgY, orgX + orgW, orgY + orgH, null ); 
        g.dispose();
        
        return resize( outputImage, destW, destH );
	}
	
	public static BufferedImage resize( BufferedImage bi, int w, int h ) 
	{
        double ar = (double)bi.getWidth() / bi.getHeight();
        
        if ( h * ar > w ) 
        	h = (int) (w / ar);
        else 
        	w = (int) (h * ar);
        
        double sx = (double) w / bi.getWidth();
        double sy = (double) h / bi.getHeight();

        AffineTransform scale = AffineTransform.getScaleInstance( sx, sy );
        
        AffineTransformOp scaleOp = new AffineTransformOp( scale, AffineTransformOp.TYPE_BICUBIC ); 
        
        BufferedImage biDest = scaleOp.createCompatibleDestImage( bi, bi.getColorModel() );
        
        scaleOp.filter( bi, biDest );
        
        return biDest;
    }
	
	public static void logResult( String result, OutputStream os )
	{
		if ( os != null )
		{
			try
			{
				os.write( (result + "\n").getBytes() );
			}
			catch ( Throwable e)
			{
				Utils.logException( e, LOG );
			}
		}
	}
	
	public static String getUrlEncoded( String value )
	{
		try 
		{
			return URLEncoder.encode( value, "UTF-8" );
		} 
		catch ( Throwable e) 
		{
			return value;
		}
	}

	public static String getUrlDecoded( String value )
	{
		try 
		{
			return URLDecoder.decode( value, "UTF-8" );
		} 
		catch ( Throwable e) 
		{
			return value;
		}
	}
	

	public static Map<String, String> convertResourceBundleToMap( ResourceBundle resource ) 
	{
	    Map<String, String> map = new HashMap<String, String>();

	    Enumeration<String> keys = resource.getKeys();
	    
	    while (keys.hasMoreElements()) 
	    {
	    	String key = keys.nextElement();
	    	map.put(key, resource.getString(key));
	    }

	    return map;
	}
	
	public static byte[] convertImageToByteArray( BufferedImage originalImage )
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageIO.write( originalImage, "png", baos );
			baos.flush();
			byte[] byteArray = baos.toByteArray();
			baos.close();
			return byteArray;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}
	
	public static String getEnviroment( String variable )
	{
		return System.getenv( variable );
	}
	
	public static void showNotification( AppContext ctx, String text, Notification.Type type )
	{
		/*Notification notification = new Notification( text + (type.equals( Notification.Type.ERROR_MESSAGE ) ? "\n" + ctx.getString( "words.click.escape" ) : ""), type );
		notification.setIcon( new ThemeResource( "images/alerts-32.png" ) );
		notification.show( Page.getCurrent() );*/
		
		Notification.show( text + (type.equals( Notification.Type.ERROR_MESSAGE ) ? "\n" + ctx.getString( "words.click.escape" ) : ""), type );
	}
	
	public static Image getBarcode( String text )
	{
    	try
    	{
        	Code128Bean code128 = new Code128Bean();
        	code128.setHeight(15f);
        	code128.setModuleWidth(0.3);
        	code128.setQuietZone(10);
        	code128.doQuietZone(true);

	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 150, BufferedImage.TYPE_BYTE_BINARY, false, 0);
	    	code128.generateBarcode( canvas, text );
	    	canvas.finish();
	
	    	return new Image( null, new StreamResource( new ByteStreamStreamResource( baos.toByteArray() ), "barcode" ) );
    	}
    	catch( Throwable e )
    	{
    		Utils.logException( e, LOG );
    	}
    	
    	return null;
	}

	public static Image get2DBarcode( String text )
	{
    	try
    	{
        	Code128Bean code128 = new Code128Bean();
        	code128.setHeight(15f);
        	code128.setModuleWidth(0.3);
        	code128.setQuietZone(10);
        	code128.doQuietZone(true);

	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 150, BufferedImage.TYPE_BYTE_BINARY, false, 0);
	    	code128.generateBarcode( canvas, text );
	    	canvas.finish();
	
	    	return new Image( null, new StreamResource( new ByteStreamStreamResource( baos.toByteArray() ), "barcode" ) );
    	}
    	catch( Throwable e )
    	{
    		Utils.logException( e, LOG );
    	}
    	
    	return null;
	}

	public static List<String> getListOfFiles( String directory, FilenameFilter filter )
	{
		List<String> ret = new ArrayList<String>();
		
		try
		{
			File folder = new File( directory );
		
			File[] listOfFiles = folder.listFiles( filter );

		    for ( int i = 0; i < listOfFiles.length; i++ ) 
		    	if ( listOfFiles[i].isFile() ) 
		    		ret.add( listOfFiles[i].getAbsolutePath() );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return ret;
	}
	
	public static String getSerializedObject( Object obj )
	{
		ObjectOutputStream oos = null;
		
		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			oos = new ObjectOutputStream(os);
			oos.writeObject(obj);

			return Utils.convertToHex( os.toByteArray() );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		finally
		{
			if ( oos != null )
			{
				try
				{
					oos.close();
				}
				catch ( Throwable e )
				{
					Utils.logException( e, LOG );
				}
			}
		}

		return null;
	}

	public static Object getDeserializedObject( String content )
	{
		ObjectInputStream ois = null;
		
		try
		{
			
			ByteArrayInputStream is = new ByteArrayInputStream( Utils.convertFromHex( content.getBytes() ) );
			
			ois = new ObjectInputStream( is );

			return ois.readObject();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		finally
		{
			if ( ois != null )
			{
				try
				{
					ois.close();
				}
				catch ( Throwable e )
				{
					Utils.logException( e, LOG );
				}
			}
		}

		return null;
	}
	
	public static String getProtocolFromUrl( String url )
	{
		if ( url.startsWith( "https://" ) )
			return "https";
		if ( url.startsWith( "http://" ) )
			return "http";
				
		return "http";
	}
	
	public static boolean isHostSeparator( char c )
	{
		return c == '/' || c == '?' || c == ':';
	}
	
	public static boolean isPortSeparator( char c )
	{
		return c == '/' || c == '?';
	}
	
	public static String getHostFromUrl( String url )
	{
		int pos = url.indexOf( "://" );
		
		String ret = "";

		int i = pos != -1 ? pos + 3 : 0;
		
		while ( i < url.length() && !isHostSeparator( url.charAt( i ) ) )
		{
			ret += url.charAt( i );
			i++;
		}

		return ret;
	}

	public static int getPortFromUrl( String url )
	{
		String protocol = getProtocolFromUrl( url );
		
		String ret = "";

		int pos = url.indexOf( "://" );
		int i = pos != -1 ? pos + 3 : 0;
		while ( i < url.length() && url.charAt( i ) != ':' )
			i++;
		if ( i < url.length() )
			i++;
		
		while ( i < url.length() && !isPortSeparator( url.charAt( i ) ) )
		{
			ret += url.charAt( i );
			i++;
		}
		
		return !ret.isEmpty() ? Integer.parseInt( ret ) : (protocol.equals( "https" ) ? 443 : 80 );
	}

	public static String getUriFromUrl( String url )
	{
		int pos = url.indexOf( "://" );
		
		String ret = "";

		int i = pos != -1 ? pos + 3 : 0;
		
		while ( i < url.length() && url.charAt( i ) != '/' )
			i++;

		while ( i < url.length() )
		{
			ret += url.charAt( i );
			i++;
		}

		return ret;
	}
	
	public static ArrayList<String> getCsvTokens( String line, char fieldSeparator, char textSeparator )
	{
		ArrayList<String> tokens = new ArrayList<String>();
		
		int pos = 0;
		
		while ( pos < line.length() )
		{
			String token = "";

			if ( line.charAt( pos ) == textSeparator )
			{
				pos++;
				
				while ( pos < line.length() && line.charAt( pos ) != textSeparator )
				{
					token += line.charAt( pos++ );
				}
				
				pos++;

				while ( pos < line.length() && line.charAt( pos ) != fieldSeparator )
					pos++;
				
				pos++;
			}
			else
			{
				while ( pos < line.length() && line.charAt( pos ) != fieldSeparator )
				{
					token += line.charAt( pos++ );
				}

				pos++;
			}
			
			tokens.add( token );
		}
		
		return tokens;
	}

	public static ArrayList<ArrayList<String>> readCsvFile( InputStream is, char fieldSeparator, char textSeparator )
	{
	   ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
	   
	   try 
	   {
           BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( is ) );

           String line = null;
           while ( (line = bufferedReader.readLine()) != null ) 
           {
        	   if ( !line.isEmpty() )
        		   list.add( getCsvTokens( line, fieldSeparator, textSeparator ) );
           }    

           bufferedReader.close();
	   } 
	   catch ( Throwable e) 
	   {
	       Utils.logException( e, LOG );
	   }

	   return list;
	}
	
	public static String getStringAsHtml( String text )
	{
		String ret = "";

		String lines[] = text.split( "\n" );
		
		for ( String line : lines )
		{
			if ( !ret.isEmpty() )
				ret += "<br/>";
			ret += line;
		}
		
		return ret;
	}
	
	public static String getFormattedCurrency( Double value )
	{
		return new DecimalFormat("#,##0.00").format(value);
	}

	public static String removeTrailing( String source, String trail )
	{
		if ( source.endsWith( trail ) )
			return source.substring( 0, source.length() - trail.length() );

		return source;
	}

	public static double getPageHeightInMilimeters( String pagesize )
	{
		if ( pagesize.contentEquals( "A0" ) )
			return 1189;
		if ( pagesize.contentEquals( "A1" ) )
			return 841;
		if ( pagesize.contentEquals( "A2" ) )
			return 594;
		if ( pagesize.contentEquals( "A3" ) )
			return 420;
		if ( pagesize.contentEquals( "A4" ) )
			return 297;
		if ( pagesize.contentEquals( "A5" ) )
			return 210;
		if ( pagesize.contentEquals( "Letter" ) )
			return 279;
		if ( pagesize.contentEquals( "Legal" ) )
			return 356;
		
		return 0;
	}

}
