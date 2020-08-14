package es.pryades.erp.common;

public interface ReportActionQueryEditor
{
	public Object getComponent( String data, boolean readOnly );
	public String getReportQuery() throws BaseException;
	public String isValidInput(); 
}
