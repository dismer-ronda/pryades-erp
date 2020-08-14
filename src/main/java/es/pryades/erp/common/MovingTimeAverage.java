package es.pryades.erp.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovingTimeAverage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2407975467641749755L;
	private List<TimedValue> window = new ArrayList<TimedValue>();
	private long deltaTime;
	private double sum = 0;

	public MovingTimeAverage( int deltaTime ) 
	{
		this.deltaTime = deltaTime;
	}

	private long getWindowTime()
	{
		return Utils.getDurationInSeconds( window.get(0).getTime(), window.get( window.size() - 1 ).getTime() );
	}
	
	public double newNum( TimedValue num ) 
	{
		sum += num.getValue();
		window.add(num);
		
		while ( !window.isEmpty() && getWindowTime() > deltaTime )
		{
			sum -= window.get(0).getValue();
			window.remove(0);
		}
		
		return window.size() != 0 ? sum / window.size() : 0;
	}
}