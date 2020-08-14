package es.pryades.erp.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class CalendarUtils
{
	private static final Logger LOG = Logger.getLogger( CalendarUtils.class );
	
	public static boolean isExpired( int from, int expiration ) throws BaseException
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.setTime( CalendarUtils.getDateFromInt( from ) );
		calendar.add( Calendar.DATE, expiration );

		return CalendarUtils.getTodayAsInt() > CalendarUtils.getCalendarTimeAsInt( calendar );
	}

	public static int getTodayAsInt()
	{
		return getCalendarTimeAsInt( GregorianCalendar.getInstance() );
	}

	public static long getTodayAsLong()
	{
		return getCalendarTimeAsLong( GregorianCalendar.getInstance() );
	}

	public static long getTodayAsLong( String timezone )
	{
		return getCalendarTimeAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( timezone ) ) );
	}

	public static String getTodayAsString( String timezone, String format )
	{
		return getFormatedDate( getTodayAsLong( timezone ), format );
	}

	public static Calendar getCalendarDayFromDateLong( long time ) throws BaseException
	{
		return getCalendarDayFromDateLong( time, "UTC" );
	}

	public static Calendar getCalendarDayFromDateLong( long time, String timezone ) throws BaseException
	{
		Calendar calendar = getCalendarFromDateLong( time, timezone );
		
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );
		
		return calendar;
	}

	public static Calendar getServerCurrentCalendar()
	{
		return GregorianCalendar.getInstance();
	}
	
	public static Calendar getCurrentCalendar( String timezone )
	{
		return GregorianCalendar.getInstance( TimeZone.getTimeZone( timezone ) );
	}
	
	public static Calendar getServerCalendarFromDateLong( long time ) throws BaseException
	{
		try
		{
			Calendar calendar = GregorianCalendar.getInstance(); 

			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( time ) ) );
			
			return calendar;
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.DECODE_ERROR );
		}
	}

	public static long getServerTimeToTimezone( long time, String timezone ) throws BaseException
	{
		try
		{
			Calendar calendar = GregorianCalendar.getInstance(); 

			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( time ) ) );

			calendar.setTimeZone( TimeZone.getTimeZone( timezone ) );

			return getCalendarTimeAsLong( calendar );
		}
		catch ( Throwable e )
		{
			throw new BaseException( e, LOG, BaseException.DECODE_ERROR );
		}
	}


	public static Calendar getCalendarFromDateLong( long time, String timezone )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( "UTC" ) ); 

		try
		{
			calendar.setTime( new SimpleDateFormat( "yyyyMMddHHmmssZ" ).parse( Long.toString( time ) + "+0000" ) );
			
			calendar.setTimeZone( TimeZone.getTimeZone( timezone ) );
		}
		catch ( Throwable e )
		{
		}

		return calendar;
	}

	public static boolean isSameDay( Calendar calPrev, Calendar calNext )
	{
		return calPrev.get( Calendar.DAY_OF_MONTH ) == calNext.get( Calendar.DAY_OF_MONTH ) && calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static boolean isSameHour( Calendar calPrev, Calendar calNext )
	{
		return calPrev.get( Calendar.HOUR ) == calNext.get( Calendar.HOUR ) && calPrev.get( Calendar.DAY_OF_MONTH ) == calNext.get( Calendar.DAY_OF_MONTH ) && calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}
	
	public static boolean isSameHour( long utc1, long utc2 )
	{
		Calendar calendar1 = GregorianCalendar.getInstance( TimeZone.getTimeZone( "UTC" ) ); 
		calendar1.setTimeInMillis( utc1 );

		Calendar calendar2 = GregorianCalendar.getInstance( TimeZone.getTimeZone( "UTC" ) ); 
		calendar2.setTimeInMillis( utc2 );
	
		return isSameHour( calendar1, calendar2 );
	}

	public static long getDayFirstSecondAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getDayLastSecondAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static int getCalendarTimeAsInt( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return day + month * 100 + year * 10000;
	}

	public static long getCalendarTimeAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );
		int min = calendar.get( Calendar.MINUTE );
		int sec = calendar.get( Calendar.SECOND );

		return (long)sec + (long)min * 100 + (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getTimeAsLong( int year, int month, int day, int hour, int min, int sec )
	{
		return (long)sec + (long)min * 100 + (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getDateAsLong( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.setTime( date );
						
		return CalendarUtils.getCalendarTimeAsLong( calendar );
	}

	public static long getDateFromTimeZoneToUTC( Date date, String timezone )
	{
		long dateLong = getDateAsLong( date );
		
		try
		{
			Calendar calendar = getCalendarFromDateLong( dateLong, timezone );
			
			return CalendarUtils.getCalendarTimeAsLong( calendar );
		}
		catch ( Throwable e )
		{
			return getTodayAsLong();
		}
	}
	
	public static long getServerDateAsLong()
	{
		Calendar calendar = GregorianCalendar.getInstance();

		return CalendarUtils.getCalendarTimeAsLong( calendar );
	}

	public static long getDateAsLong( Date date, String timezone )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( timezone ) );

		calendar.setTime( date );
						
		calendar.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
		
		return CalendarUtils.getCalendarTimeAsLong( calendar );
	}
	
	public static int getDateAsInt( Date date )
	{
		return getDateAsInt( date, "UTC" );
	}
	
	public static int getDateAsInt( Date date, String timezone )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( timezone ) );

		calendar.setTime( date );
		
		calendar.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

		return CalendarUtils.getCalendarTimeAsInt( calendar );
	}
	
	public static String getDateFromCalendarAsString( Calendar calendar, String format )
	{
		return getDateFromLongAsString( getCalendarTimeAsLong(calendar), format, "UTC" );
	}

	public static String getDateFromLongAsString( long date, String format )
	{
		return getDateFromLongAsString( date, format, "UTC" );
	}

	public static String getDateFromLongAsString( long date, String format, String timezone )
	{
		try
		{
			Calendar calendar = getCalendarFromDateLong( date, timezone );
			
			Date input = calendar.getTime();
			
			SimpleDateFormat df = new SimpleDateFormat( format );
			
			df.setTimeZone( TimeZone.getTimeZone( timezone ) );
			
			return df.format( input );
		}
		catch ( Throwable e )
		{
			return "";
		}
	}

	public static Date getDateFromInt( int date )
	{
		try
		{
			return new SimpleDateFormat( "yyyyMMdd" ).parse( Integer.toString( date ) );
		}
		catch ( Throwable e )
		{
			return new Date();
		}
	}
	
	public static Date getDateHourFromLong( long ldate )
	{
		Date date = null;
		try
		{
			date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );
		}
		catch ( Throwable e )
		{
			date = new Date();
		}
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );

		return cal.getTime();
	}

	public static Date getCurrentDate( String horario )
	{
		return getDateHourFromLong( getCalendarTimeAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}

	public static Date getDateFromString( String date, String format )
	{
		if ( date == null )
			return new Date();
		
		try
		{
			Calendar calendar = GregorianCalendar.getInstance(); // TimeZone.getTimeZone( "UTC" ) );

			calendar.setTime( new SimpleDateFormat( format ).parse( date ) );

			return calendar.getTime();
		}
		catch ( Throwable e )
		{
			new BaseException( e, LOG, BaseException.DECODE_ERROR );

			return new Date();
		}
	}


	public static String formatDate( String strFecha, String strFechaPattern, String resulFechaPattern ) throws BaseException
	{
		SimpleDateFormat formatIn = new SimpleDateFormat( strFechaPattern );
		SimpleDateFormat formatOut = new SimpleDateFormat( resulFechaPattern );
		String fechaFormated = "";

		if ( strFecha.compareTo( "" ) != 0 )
		{
			try
			{
				Date dateInFormated = formatIn.parse( strFecha );
				fechaFormated = formatOut.format( dateInFormated );
			}
			catch ( Throwable e )
			{
				throw new BaseException( e, LOG, BaseException.FORMAT_ERROR_DATE );
			}
		}

		return fechaFormated;
	}
	
	public static String getFormatedDate( Long ldate, String format )
	{
		try
		{
			Date date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis( date.getTime() );

			return new SimpleDateFormat( format ).format( cal.getTime() );
		}
		catch ( Throwable e )
		{
			return Long.toString( ldate );
		}
	}

	public static String getFormatedDate( Date date, String format )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		return new SimpleDateFormat( format ).format( cal.getTime() );
	}

	/*

	public static Calendar getCalendarFromDate( Date date, String horario, int field, int offset )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		calendar.setTime( date );
		calendar.add( field, offset );

		return calendar;
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

	public static String getFormatedDate( String format ) throws BaseException
	{
		try
		{
			SimpleDateFormat formato = new SimpleDateFormat( format );

			Calendar cal = new GregorianCalendar();
			java.util.Date fecha = cal.getTime();

			return formato.format( fecha );
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, BaseException.JSON_STRING_ERROR );
		}
	}

	public static long getCalendarHourAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.HOUR_OF_DAY, offset );

		return getCalendarTimeAsLong( calendar );
	}

	public static long getCalendarMinuteAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.MINUTE, offset );

		return getCalendarTimeAsLong( calendar );
	}

	public static int getDateAsInt( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return getCalendarDateAsInt( calendar, 0 );
	}

	public static long getTodayAsLong( String horario )
	{
		return getCalendarDateAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ), 0 );
	}

	public static long getDayAsLong( TimeZone timezone, int offset )
	{
		return getCalendarDateAsLong( GregorianCalendar.getInstance( timezone ), offset );
	}

	public static long getTodayFirstSecondAsLong( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getTodayLastSecondAsLong( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = 23;
		int min = 59;
		int sec = 59;

		return (long)sec + (long)min * 100 + (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getTomorrowFirstSecondAsLong( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );

		calendar.add( Calendar.DATE, 1 );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static Date getTodayFirstSecondAsDate( String horario )
	{
		return getDateHourFromLong( getTodayFirstSecondAsLong( horario ) ); 
	}

	public static Date getTodayLastSecondAsDate( String horario )
	{
		return getDateHourFromLong( getTodayLastSecondAsLong( horario ) ); 
	}

	public static Date getTodayFirstSecondAsDate()
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );

		return calendar.getTime();
	}

	public static Date getTodayLastSecondAsDate()
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 23, 59, 59 );

		return calendar.getTime();
	}

	public static Date getDayFirstSecondAsDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );

		return calendar.getTime();
	}

	public static Date getDayLastSecondAsDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 23, 59, 59 );

		return calendar.getTime();
	}

	public static Date getFirstDayWeekAsDate( String horario )
	{
		return getDateHourFromLong( getCalendarFirstDayWeekAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}

	public static Date getLastDayWeekAsDate( String horario )
	{
		return getDateHourFromLong( getCalendarLastDayWeekAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}

	public static Date getFirstDayMonthAsDate( String horario )
	{
		return getDateHourFromLong( getCalendarFirstDayMonthAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}

	public static Date getFirstDayMonthAsDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), 1, 0, 0, 0 );
		return calendar.getTime();
	}

	public static Date getLastDayMonthAsDate( String horario )
	{
		return getDateHourFromLong( getCalendarLastDayMonthAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}

	public static Date getFirstDayYearAsDate( String horario )
	{
		return getDateHourFromLong( getCalendarFirstDayYearAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}

	public static Date getLastDayYearAsDate( String horario )
	{
		return getDateHourFromLong( getCalendarLastDayYearAsLong( GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) ) ) );
	}
		
	public static long getElapsedTimeUntilNow( Date date, String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
		calendar.setTime( date );

		return getDateHourFromLong( getTodayAsLong( horario ) ).getTime() - calendar.getTime().getTime();
	}

	public static Date getCurrentDate( String horario )
	{
		return getDateHourFromLong( getTodayAsLong( horario ) );
	}

	public static long getHourFirstSecondAsLong( Calendar calendar, int offset )
	{
		calendar.add( Calendar.HOUR_OF_DAY, offset );

		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );

		return (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getHourLastSecondAsLong( Calendar calendar )
	{
		int day = calendar.get( Calendar.DATE );
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );
		int hour = calendar.get( Calendar.HOUR_OF_DAY );

		return (long)59 + (long)59 * 100 + (long)hour * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getCalendarFirstDayWeekAsLong( Calendar calendar )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DAY_OF_MONTH ) );

		while ( cal.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY )
		{
			cal.add( Calendar.DAY_OF_MONTH, -1 );
		}

		int day = cal.get( Calendar.DATE );
		int month = cal.get( Calendar.MONTH ) + 1;
		int year = cal.get( Calendar.YEAR );

		return (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getCalendarLastDayWeekAsLong( Calendar calendar )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DAY_OF_MONTH ) );

		while ( cal.get( Calendar.DAY_OF_WEEK ) != Calendar.SUNDAY )
		{
			cal.add( Calendar.DAY_OF_MONTH, 1 );
		}

		int day = cal.get( Calendar.DATE );
		int month = cal.get( Calendar.MONTH ) + 1;
		int year = cal.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static int getLastDayOfMonth( Date date )
	{
		int result = -1;

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		result = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
		return result;
	}

	public static boolean isFirstDayOfMonth( Date date )
	{
		int monthDay = -1;

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );
		monthDay = calendar.get( Calendar.DAY_OF_MONTH );

		return monthDay == 1;
	}

	public static boolean isLastDayOfMonth( Date date )
	{
		int monthDay = -1;
		int lasMonthDay = -2;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		monthDay = calendar.get( Calendar.DAY_OF_MONTH );
		lasMonthDay = getLastDayOfMonth( date );

		return monthDay == lasMonthDay;
	}

	public static long getCalendarFirstDayMonthAsLong( Calendar calendar )
	{
		int month = calendar.get( Calendar.MONTH ) + 1;
		int year = calendar.get( Calendar.YEAR );

		return (long)1 * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getCalendarLastDayMonthAsLong( Calendar calendar )
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );

		int day = cal.get( Calendar.DATE );
		int month = cal.get( Calendar.MONTH ) + 1;
		int year = cal.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)day * 1000000 + (long)month * 100000000L + (long)year * 10000000000L;
	}

	public static long getCalendarFirstDayYearAsLong( Calendar calendar )
	{
		int year = calendar.get( Calendar.YEAR );

		return (long)1 * 1000000 + (long)1 * 100000000L + (long)year * 10000000000L;
	}

	public static long getCalendarLastDayYearAsLong( Calendar calendar )
	{
		int year = calendar.get( Calendar.YEAR );

		return (long)59 + (long)59 * 100 + (long)23 * 10000 + (long)31 * 1000000 + (long)12 * 100000000L + (long)year * 10000000000L;
	}

	public static Calendar getCalendarBefore( Calendar calendar, int field, int offset )
	{
		calendar.add( field, offset * (-1) );

		return calendar;
	}

	public static Calendar getCalendarAfter( Calendar calendar, int field, int offset )
	{
		calendar.add( field, offset );

		return calendar;
	}

	public static Date getDate( Date date, int field, int offset )
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.setTime( date );

		calendar.add( field, offset );

		return calendar.getTime();
	}

	public static long getHowMuchToWaitForFirstMinuteOfNextHourAsLong()
	{
		Calendar calendar = GregorianCalendar.getInstance();

		calendar.add( Calendar.HOUR_OF_DAY, 1 );

		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime().getTime() - GregorianCalendar.getInstance().getTime().getTime();
	}

	public static Date getDateFromLong( long ldate )
	{
		Date date = null;
		try
		{
			date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );
		}
		catch ( ParseException e )
		{
			date = new Date();
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		return cal.getTime();
	}

	public static Date getDateHourFromLong( long ldate )
	{
		Date date = null;
		try
		{
			date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );
		}
		catch ( ParseException e )
		{
			date = new Date();
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis( date.getTime() );

		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );

		return cal.getTime();
	}

	public static Date getDateHourFromLong( long ldate, String horario )
	{
		Date date = null;
		try
		{
			date = new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( Long.toString( ldate ) );
		}
		catch ( ParseException e )
		{
			date = new Date();
		}
		Calendar cal = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
		cal.setTimeInMillis( date.getTime() );

		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );

		return cal.getTime();
	}

	public static int getTodayAsInt()
	{
		return getCalendarDateAsInt( GregorianCalendar.getInstance(), 0 );
	}

	public static Calendar getCalendarNow( String horario )
	{
		return GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
	}

	public static Calendar getCalendarLastHour( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
		
		calendar.add( Calendar.HOUR_OF_DAY, -1 );
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), calendar.get( Calendar.HOUR_OF_DAY ), 0, 0 );
		
		return calendar;
	}

	public static Calendar getCalendarCurrentHour( String horario )
	{
		Calendar calendar = GregorianCalendar.getInstance( TimeZone.getTimeZone( horario ) );
		
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), calendar.get( Calendar.HOUR_OF_DAY ), 0, 0 );
		
		return calendar;
	}

	public static boolean isSameHour( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.DAY_OF_MONTH ) == calNext.get( Calendar.DAY_OF_MONTH ) && 
			calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && 
			calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR ) && 
			calPrev.get( Calendar.HOUR_OF_DAY ) == calNext.get( Calendar.HOUR_OF_DAY );
	}

	public static boolean isSameDay( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return isSameDay(calPrev, calNext);
	}

	public static boolean isSameMonth( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.MONTH ) == calNext.get( Calendar.MONTH ) && calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static boolean isSameYear( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.YEAR ) == calNext.get( Calendar.YEAR );
	}

	public static boolean isSameWeek( Date prev, Date next )
	{
		if ( prev == null || next == null )
			return false;

		Calendar calPrev = GregorianCalendar.getInstance();
		calPrev.setFirstDayOfWeek( Calendar.MONDAY );
		calPrev.setTimeInMillis( prev.getTime() );

		Calendar calNext = GregorianCalendar.getInstance();
		calNext.setFirstDayOfWeek( Calendar.MONDAY );
		calNext.setTimeInMillis( next.getTime() );

		return calPrev.get( Calendar.WEEK_OF_YEAR ) == calNext.get( Calendar.WEEK_OF_YEAR ) 
	}

	public static boolean isSameHour( Calendar from, Calendar to )
	{
		return from.get( Calendar.DATE ) == to.get( Calendar.DATE ) && from.get( Calendar.MONTH ) == to.get( Calendar.MONTH ) && from.get( Calendar.YEAR ) == to.get( Calendar.YEAR ) && from.get( Calendar.HOUR_OF_DAY ) == to.get( Calendar.HOUR_OF_DAY );
	}

	public static Calendar getCalendarDayFromDateLong( long time ) throws BaseException
	{
		Calendar calendar = getCalendarFromDateLong( time );
		
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );
		
		return calendar;
	}

	public static Calendar getCalendarHourFromDateLong( long time, String horario ) throws BaseException
	{
		Calendar calendar = getCalendarFromDateLong( time, horario );
		
		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), calendar.get( Calendar.HOUR_OF_DAY ), 0, 0 );
		
		return calendar;
	}

	public static long getTotalMinutes( Calendar from, Calendar to )
	{
		return (to.getTimeInMillis() - from.getTimeInMillis()) / Utils.ONE_MINUTE;
	}

	public static Calendar getCalendarFromDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		return calendar;
	}

	public static Calendar getCalendarDayFromDate( Date date )
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime( date );

		calendar.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ), 0, 0, 0 );
		
		return calendar;
	}
	*/
}
