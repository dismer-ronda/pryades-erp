package es.pryades.erp.dal;

import java.util.List;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.User;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface UsersManager extends BaseManager
{
	public void loadUsuario(AppContext ctx, String login) throws BaseException;
	public void validateUser(AppContext ctx, String login, String password, String subject, String text, boolean mail ) throws BaseException;
	public void remoteLogin(AppContext ctx, String login, String password) throws BaseException;
	
	public void setPassword(AppContext ctx, User usuario, String subject, String text, boolean mail ) throws BaseException;
	public void setStatus(AppContext ctx, User usuario) throws BaseException;
	public void sendNewPassword(AppContext ctx, String email, String subject, String text, boolean mail ) throws BaseException;
	
	public User createUser( AppContext ctx, User usuario ) throws BaseException;
	public void updateUser( AppContext ctx, User usuario, User newUsuario ) throws BaseException;
	
	public void loadUserContext( AppContext ctx );
}
