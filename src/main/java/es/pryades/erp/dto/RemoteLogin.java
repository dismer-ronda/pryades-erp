package es.pryades.erp.dto;

import java.io.Serializable;

import lombok.Data;

/**
*
* @author Dismer Ronda 
*/

@Data 
public class RemoteLogin implements Serializable
{
	private static final long serialVersionUID = 6782466466568655069L;
	
	private String login;
	private String pwd;
}
