package es.pryades.erp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Parameter extends BaseDto
{
	private static final long serialVersionUID = 1113547474378950069L;
	
	public static final long PAR_LOGIN_FAILS_NEW_PASS		= 1;
	public static final long PAR_LOGIN_FAILS_BLOCK			= 2;
	public static final long PAR_PASSWORD_MIN_SIZE			= 3;
	public static final long PAR_PASSWORD_VALID_TIME		= 4;
	public static final long PAR_MAIL_HOST_ADDRESS			= 5;
	public static final long PAR_MAIL_SENDER_EMAIL			= 6;
	public static final long PAR_MAIL_SENDER_USER			= 7;
	public static final long PAR_MAIL_SENDER_PASSWORD		= 8;
	public static final long PAR_MAIL_HOST_PORT				= 9;
	public static final long PAR_MAIL_AUTH					= 10;

	public static final long PAR_DURATION_LOGIN_LOGOUT					= 21;
	
	public static final long PAR_HTTP_PROXY_HOST						= 31;
	public static final long PAR_HTTP_PROXY_PORT						= 32;
	
	public static final long PAR_SOCKS5_PROXY_HOST						= 41;
	public static final long PAR_SOCKS5_PROXY_PORT						= 42;

	public static final long PAR_STRENGTH_SIZE 							= 51;
	public static final long PAR_STRENGTH_CAPITAL						= 52;
	public static final long PAR_STRENGTH_DIGIT							= 53;
	public static final long PAR_STRENGTH_SYMBOL						= 54;
	public static final long PAR_STRENGTH_LOGIN							= 55;
	public static final long PAR_STRENGTH_REUSE							= 56;

	public static final long PAR_LOG_DEFAULT							= 61;
	public static final long PAR_LOG_TASKS								= 62;
	public static final long PAR_LOG_USERS								= 63;
	public static final long PAR_LOG_USERS_DEFAULTS						= 64;

	public static final long PAR_MAX_ROWS_EXPORTED						= 65;
	public static final long PAR_DEFAULT_PAGE_SIZE						= 66;

	private String description;
	private String value;
	private Integer display_order;
}
