package es.pryades.erp.dto;

import java.io.Serializable;

import lombok.Data;

/**
*
* @author Dismer Ronda 
* @since 1.0.0.0
*/

@Data
public class Query implements Serializable
{
	private static final long serialVersionUID = 9174870106172767286L;
	
	private Integer pageSize;
	private Long pageNumber;
	private String orderby;
	private String order;
	
	public Query()
	{
		setOrderby( "id" );
		setOrder( "asc" );
	}
}
