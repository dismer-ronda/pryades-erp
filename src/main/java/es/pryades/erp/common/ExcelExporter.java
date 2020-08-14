package es.pryades.erp.common;

import java.io.InputStream;

public interface ExcelExporter
{
	InputStream getExcelStream();
	String getFileName();
}
