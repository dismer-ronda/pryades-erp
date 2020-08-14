package es.pryades.erp.vto;

import es.pryades.erp.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UserVto extends GenericVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5463239683286721818L;
	private String name;
	private String login; 
	private String email; 
	private String pwd;
	private Long ref_profile;
	private Long ref_region;
	private Long ref_plant;
	private String tester;

	private String profile_name;
	private String region_name;
	private String plant_name;
	
	public UserVto()
	{
	}
}
