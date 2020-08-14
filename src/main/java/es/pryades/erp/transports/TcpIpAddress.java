package es.pryades.erp.transports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper=true)
@Data
@AllArgsConstructor
public class TcpIpAddress extends Address
{
	private static final long serialVersionUID = 5628829461060115706L;
	
	String ip;
	Integer port;
}
