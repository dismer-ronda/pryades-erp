package es.pryades.erp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author Dismer Ronda 
*/

@EqualsAndHashCode(callSuper=true)
@Data 
public class User extends BaseDto 
{
	private static final long serialVersionUID = -6358464591240794085L;

	public static final int PASS_OK			= 0;
	public static final int PASS_NEW 		= 1;
	public static final int PASS_FORGET 	= 2;
	public static final int PASS_EXPIRY 	= 3;
	public static final int PASS_CHANGED 	= 4;
	public static final int PASS_BLOCKED 	= 5;
	
	private String login;
	private String email;
	private String pwd;
	private Integer changed;
	private Integer retries;
	private Integer status;
	private String name;

	private Long ref_profile;
	
	private byte[] signature;

	private String profile_name;
}
