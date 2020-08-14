package es.pryades.erp.common;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

import com.vaadin.ui.Table;

public class CustomTable extends Table 
{
	private static final long serialVersionUID = 2139591712790106556L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( CustomTable.class ); 

	@Setter 
	@Getter 
	private String resourceKey;
	
	@Setter 
	@Getter 
	private AppContext context;

	@Override
	public String getColumnHeader( Object property ) 
	{
		String key = getResourceKey() + ".table.headerHint." + property;
		String tooltip = getContext().getString( key );
		
		if ( tooltip.equals( key ) )
			tooltip = getContext().getString( getResourceKey() + ".table.headerName." + property );
	
		return "<label title=\"" + tooltip + "\">" + super.getColumnHeader( property ) + "</label>";
	}
}