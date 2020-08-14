package es.pryades.erp.common;

public class XmlUtils
{
	public static String getTable( String width, String clazz, String contents )
	{
		return "<table width=\"" + width + "\" class=\"" + clazz + "\">\n" + contents + "</table>\n"; 
	}
	
	public static String getTableRow( String clazz, String contents )
	{
		return "<tr class=\"" + clazz + "\">\n" + contents + "</tr>\n";
	}

	public static String getTableCol( String width, String clazz, String contents )
	{
		return "<td " + (width.isEmpty() ? "" : ("width=\"" + width + "\" ")) + "class=\"" + clazz + "\">\n" + contents + "\n</td>\n";
	}

	public static String getDiv( String clazz, String contents )
	{
		return "<div class=\"" + clazz + "\">\n" + contents + "</div>";
	}

	public static String newline()
	{
		return "<br/>";
	}
}
