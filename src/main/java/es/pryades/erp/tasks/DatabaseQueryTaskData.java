package es.pryades.erp.tasks;

import java.io.Serializable;

import lombok.Data;

@Data
public class DatabaseQueryTaskData implements Serializable
{
	private static final long serialVersionUID = -6892229733918627707L;
	
	private String sql;
	private Long ref_user;
}
