package es.pryades.erp.tasks;

import java.io.Serializable;

import lombok.Data;

@Data
public class DatabaseUpdateTaskData implements Serializable
{
	private static final long serialVersionUID = -7460651698096459685L;
	
	private String sql;
	private Long ref_user;
}
