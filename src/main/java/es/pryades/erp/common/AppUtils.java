package es.pryades.erp.common;

import java.awt.Dimension;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("rawtypes")
public class AppUtils  
{
	private static final Logger LOG = Logger.getLogger( AppUtils.class );

	public AppUtils()
	{
	}

	public static Label createLabel( String text, String width, Layout layout )
	{
		DialogLabel label1 = new DialogLabel( text, width );
		
		layout.addComponent( label1 );
		
		return label1;
	}
	
	public static TextField createInput( String value, String width, boolean readOnly, Layout layout, String debugId)
	{
		TextField text1 = new TextField();
		
		text1.setImmediate( true );
		text1.setWidth( width );
		text1.setHeight( "-1px" );
		text1.setValue( value );
		text1.setReadOnly( readOnly );
		if ( debugId != null )
			text1.setId( debugId );

		layout.addComponent( text1 );
		
		return text1;
	}

	public static TextField createInput( Property property, String width, boolean readOnly, Layout layout, String debugId)
	{
		TextField text1 = new TextField( property );
		
		text1.setImmediate( true );
		text1.setWidth( width );
		text1.setHeight( "-1px" );
		text1.setReadOnly( readOnly );
		if ( debugId != null )
			text1.setId( debugId );
		text1.setNullRepresentation( "" );

		layout.addComponent( text1 );
		
		return text1;
	}

	public static TextArea createTextArea( String value, String width, boolean readOnly, Layout layout, String debugId  )
	{
		TextArea text1 = new TextArea();
		
		text1.setImmediate( true );
		text1.setWidth( width );
		text1.setHeight( "-1px" );
		text1.setValue( value );
		text1.setReadOnly( readOnly );
		if ( debugId != null )
			text1.setId( debugId );
		
		layout.addComponent( text1 );
		
		return text1;
	}

	public static TextArea createTextArea( Property property, String width, boolean readOnly, Layout layout, String debugId  )
	{
		TextArea text1 = new TextArea( property );
		
		text1.setImmediate( true );
		text1.setWidth( width );
		text1.setHeight( "-1px" );
		text1.setReadOnly( readOnly );
		if ( debugId != null )
			text1.setId( debugId );
		
		layout.addComponent( text1 );
		
		return text1;
	}

	public static PasswordField createPassword( Layout layout, String debugId )
	{
		PasswordField password1 = new PasswordField();
		
		password1.setImmediate( false );
		password1.setWidth( "-1px" );
		password1.setHeight( "-1px" );
		if ( debugId != null )
			password1.setId( debugId );
		
		layout.addComponent( password1 );
	
		return password1;
	}
	
	public static Button createButton( String text, String tooltip, String debugId, Layout layout )
	{
		Button button1 = new Button();
		
		button1.setCaption( text );
		button1.setDescription( tooltip );
		button1.setImmediate(true);
		button1.setWidth("-1px");
		button1.setHeight("-1px");
		if ( debugId != null )
			button1.setId( debugId );
		
		layout.addComponent( button1 );

		return button1;
	}
	
	public static CheckBox createCheckbox( String text, String tooltip, String debugId, Layout layout)
	{
		CheckBox cb = new CheckBox();
		
		cb.setCaption( text );
		cb.setDescription( tooltip );
		cb.setImmediate(true);
		cb.setWidth("-1px");
		cb.setHeight("-1px");
		if ( debugId != null )
			cb.setId( debugId );
		
		layout.addComponent( cb );

		return cb ;
	}

	public static HorizontalLayout createRow( boolean margin, boolean spacing )
	{
		HorizontalLayout row = new HorizontalLayout();

		row.setMargin( margin );
		row.setSpacing( spacing );
		
		return row;
	}
	
	public static VerticalLayout createColumn( boolean margin, boolean spacing )
	{
		VerticalLayout col = new VerticalLayout();

		col.setMargin( margin );
		col.setSpacing( spacing );
		
		return col;
	}

	public static GridLayout createGrid( boolean margin, boolean spacing, int cols )
	{
		GridLayout grid = new GridLayout();

		grid.setMargin( margin );
		grid.setSpacing( spacing );
		grid.setColumns( cols );
		
		return grid;
	}

    public static boolean isLocaleSupported( Locale locale, String langs ) 
    {
		if ( locale == null )
			return false;
		
		String languages[] = langs.split( "," );

		for ( String l : languages )
		{
			if ( l.equals( locale.getLanguage() ) )
				return true;
		}
    	
		return false;
    }
    
    public static Locale getDefaultLocale()
    {
		return new Locale( "en" );
    }

    public static Locale getLocaleFromBrowser( UI ui, String langs )
    {
		Locale locale = ui.getSession().getLocale();
        
		LOG.info( "Locale from browser = " + locale.getDisplayName() );

		if ( isLocaleSupported( locale, langs ) )
			return locale;
		
		return null;
    }
    
	public static ComboBox createCombobox( String debugId, Layout layout )
	{
		ComboBox cb = new ComboBox();
		
		cb.setImmediate(true);
		cb.setWidth("-1px");
		cb.setHeight("-1px");
		
		layout.addComponent( cb );

		return cb ;
	}

	public static Panel createPanel( String text, Layout layout )
	{
		Panel cb = new Panel();
		
		cb.setImmediate(true);
		cb.setWidth("-1px");
		cb.setHeight("-1px");
		cb.setCaption( text );
		
		layout.addComponent( cb );

		return cb ;
	}
	
    public static String getExceptionMessage( BaseException e, AppContext context )
    {
    	switch ( e.getErrorCode() )
    	{
    		case BaseException.REFLECTION_ERROR:
    			return context.getString( "error.reflection" );
    	}
    	
    	return "";
    }
    
    public static String getString( ResourceBundle resources, String key )
    {
		try 
		{
			return resources.getString( key );
		}
		catch ( Throwable e )
		{
			return key;
		}
    }	
	
    
    public static String getHourOfDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return Integer.toString( calendar.get( Calendar.HOUR_OF_DAY ) );
	}

    public static String getDayOfWeek(ResourceBundle resources, Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int dayweek = 7;
		if (calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
			dayweek = calendar.get(Calendar.DAY_OF_WEEK) - 1; 
		}
		int daymonth= calendar.get(Calendar.DAY_OF_MONTH);
		
		return AppUtils.getString( resources, "day.nemonico."+dayweek)+" "+daymonth;
	}

    public static String getWeek(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return Integer.toString( calendar.get(Calendar.WEEK_OF_YEAR) );
	}

    public static String getMonth(ResourceBundle resources, Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int month 	= calendar.get(Calendar.MONTH)+1;
		
		return AppUtils.getString( resources, "month.nemonico."+month);
	}
    
    public static String getFechaByLong(String date){
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String value = null;
		try {
			Date dateformatted = df.parse(date);
			value = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateformatted);
			
		} catch (ParseException e) {
			System.err.println("can not read date");
		}
		return value;
	}
	public static String getFechaBy(String date){
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String value = null;
		try {
			Date dateformatted = df.parse(date);
			value = new SimpleDateFormat("dd/MM/yyyy").format(dateformatted);
			
		} catch (ParseException e) {
			System.err.println("can not read date");
		}
		return value;
	}
	
	public static OptionData createOption( String id, String caption, String tooltip, String icon, Boolean enabled, String right, Class clazz  )
	{
		OptionData option = new OptionData();
		
		option.setId( id );
		option.setCaption( caption );
		option.setToolTip( tooltip );
		option.setIcon( icon );
		option.setClazz( clazz );
		option.setEnabled( enabled );
		option.setRight( right );
		
		return option;
	}	

	public static Dimension getImageDimension( StreamResource resource) throws IOException
	{
		ImageInputStream in = ImageIO.createImageInputStream( resource.getStreamSource().getStream() );

		final Iterator readers = ImageIO.getImageReaders(in);
        
		if (readers.hasNext()) 
		{
            ImageReader reader = (ImageReader) readers.next();
        
            try 
            {
                reader.setInput(in);

                return new Dimension( reader.getWidth(0), reader.getHeight(0) );
            } 
            finally 
            {
            	reader.dispose();
            }
        }
		
		return null;
	}
	
	public static void addSummaryRow( AppContext ctx, Layout layout, int i, String title, String value, String icon, String width )
	{
		HorizontalLayout row = new HorizontalLayout();
		row.setMargin( true );
		row.addStyleName( "alternated_row_" + ((i % 2) == 0 ? "even" : "odd") );
		row.setHeight( "48px" );
		row.setWidth( "100%" );

		Component c;
		row.addComponent( c = new Image( null, new ThemeResource( icon ) ) );
		row.setComponentAlignment( c, Alignment.MIDDLE_LEFT );
		
		row.addComponent( c = new DialogLabel( ctx.getString( title ), width, "left" ) );
		row.setComponentAlignment( c, Alignment.MIDDLE_LEFT );
		c.addStyleName( "margin_left" );
		
		DialogLabel valueLabel = new DialogLabel( value, "100%", "left" );
		row.addComponent( valueLabel );
		row.setComponentAlignment( valueLabel, Alignment.MIDDLE_LEFT );

		Image image = new Image( null, new ThemeResource( "images/question-16.png" ) );
		image.setDescription( ctx.getString( title + ".hint" ) );
		row.addComponent( image );
		row.setComponentAlignment( image, Alignment.MIDDLE_RIGHT );
		
		row.setExpandRatio( valueLabel, 1.0f );
		layout.addComponent( row );
	}

	public static void addUsedFlowRow( AppContext ctx, Layout layout, int i, String title, String value, String hint, String width )
	{
		HorizontalLayout row = new HorizontalLayout();
		row.setMargin( true );
		row.addStyleName( "alternated_row_" + ((i % 2) == 0 ? "even" : "odd") );
		row.setHeight( "48px" );
		row.setWidth( "100%" );

		Component c;
		row.addComponent( c = new DialogLabel( title, width, "left" ) );
		row.setComponentAlignment( c, Alignment.MIDDLE_LEFT );
		c.addStyleName( "margin_left" );
		
		DialogLabel valueLabel = new DialogLabel( value, "100%", "left" );
		row.addComponent( valueLabel );
		row.setComponentAlignment( valueLabel, Alignment.MIDDLE_LEFT );

		if ( hint != null )
		{
			Image image = new Image( null, new ThemeResource( "images/question-16.png" ) );
			image.setDescription( hint );
			row.addComponent( image );
			row.setComponentAlignment( image, Alignment.MIDDLE_RIGHT );
		}
		
		row.setExpandRatio( valueLabel, 1.0f );
		layout.addComponent( row );
	}

	public static void addEventErrorRow( AppContext ctx, Layout layout, int i, ThemeResource icon, String title, String value, String width )
	{
		HorizontalLayout row = new HorizontalLayout();
		row.setMargin( true );
		row.addStyleName( "alternated_row_" + ((i % 2) == 0 ? "even" : "odd") );
		row.setHeight( "48px" );
		row.setWidth( "100%" );

		Component c;
		row.addComponent( c = new Image( null, icon ) );
		row.setComponentAlignment( c, Alignment.MIDDLE_LEFT );
		
		row.addComponent( c = new DialogLabel( ctx.getString( title ), width, "left" ) );
		row.setComponentAlignment( c, Alignment.MIDDLE_LEFT );
		c.addStyleName( "margin_left" );
		
		DialogLabel valueLabel = new DialogLabel( value, "100%", "left" );
		row.addComponent( valueLabel );
		row.setComponentAlignment( valueLabel, Alignment.MIDDLE_LEFT );

		/*Image image = new Image( null, new ThemeResource( "images/question-16.png" ) );
		image.setDescription( ctx.getString( title + ".hint" ) );
		row.addComponent( image );
		row.setComponentAlignment( image, Alignment.MIDDLE_RIGHT );*/
		
		row.setExpandRatio( valueLabel, 1.0f );
		layout.addComponent( row );
	}

	public static void addRowHint( AppContext ctx, HorizontalLayout row, String hint )
	{
		String text = ctx.getString( hint );
		
		if ( !text.equals( "UNDEFINED" ) )
		{
			Image image = new Image( null, new ThemeResource( "images/question-16.png" ) );
			image.setDescription( text );
			row.addComponent( image );
			row.setComponentAlignment( image, Alignment.MIDDLE_RIGHT );
		}
	}
	
	public static HorizontalLayout rowComponent( AppContext ctx, Component component, String captioncode, String hintcode )
	{
		HorizontalLayout row = new HorizontalLayout(component);
		row.setCaption( ctx.getString( captioncode ) );
		row.setWidth( "100%" );
		row.setSpacing( true );
		row.setExpandRatio( component, 1.0f );
		
		if (hintcode != null )
		{
			AppUtils.addRowHint( ctx, row, hintcode );
		}
		
		return row;
	}
}
