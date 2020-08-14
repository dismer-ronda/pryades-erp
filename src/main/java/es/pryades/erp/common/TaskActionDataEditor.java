package es.pryades.erp.common;

public interface TaskActionDataEditor
{
	public Object getComponent( String data, boolean readOnly );
	public String getTaskData() throws BaseException;
	public String isValidInput(); 
}
