package es.pryades.erp.common;

public class Constants 
{
	public static final String CHART_FONT_SIZE 					= "14px";

	public static final int ONE_YEAR 								= 365 * 24 * 60 * 60;
	
	public static final long ID_PROFILE_DEVELOPER					= 0;
	public static final long ID_PROFILE_TECH_ADMIN 					= 1;
	public static final long ID_PROFILE_GLOBAL_ADMIN				= 2;
	public static final long ID_PROFILE_LOCAL_ADMIN 				= 3;
	public static final long ID_PROFILE_SALES_MARKETING				= 4;
	public static final long ID_PROFILE_OPERATIONS					= 5;
	public static final long ID_PROFILE_VALVING						= 6;
	public static final long ID_PROFILE_TECH_OPERATOR				= 7;
	public static final long ID_PROFILE_MANUFACTURING				= 8;

	public static final int AUDIT_TYPES								= 5;
	
	public static final int AUDIT_TYPE_UNDEFINED					= -1;
	
	public static final int AUDIT_TYPE_LOGIN						= 0;
	public static final int AUDIT_TYPE_LOGOUT						= 1;
	
	public static final int AUDIT_TYPE_NEW_TASK						= 2;
	public static final int AUDIT_TYPE_MODIFY_TASK					= 3;
	public static final int AUDIT_TYPE_DELETE_TASK					= 4;

	public static final int DATE_TODAY								= 1;
	public static final int DATE_YESTERDAY							= 2;
	public static final int DATE_LAST_WEEK							= 3;
	public static final int DATE_LAST_MONTH							= 4;
	public static final int DATE_LAST_THREMESTER					= 5;
	public static final int DATE_LAST_SEMESTER						= 6;
	public static final int DATE_LAST_YEAR							= 7;

	public static final int AUDIT_DURATION_DEFAULT					= 365;
	
	public static final int TASK_CLAZZES							= 3;
	public static final int TASK_FIRST								= 100;

	public static final int TASK_CLAZZ_DATABASE_UPDATE				= 100;
	public static final int TASK_CLAZZ_DATABASE_QUERY				= 101;
	public static final int TASK_CLAZZ_QUOTATION_VALIDITY			= 102;
	
	public static final String moneyFormat = "0.00 [$€-C0A];[RED]-0.00 [$€-C0A]";
}
