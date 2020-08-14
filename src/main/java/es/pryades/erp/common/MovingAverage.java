package es.pryades.erp.common;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1547178812555569278L;
	private Queue<Double> window = new LinkedList<Double>();
	private int period;
	private double sum = 0;

	public MovingAverage( int period ) 
	{
		this.period = period;
	}

	public double newNum( double num ) 
	{
		sum += num;
		window.add(num);
		if (window.size() > period)
			sum -= window.remove();
		
		return window.size() != 0 ? sum / window.size() : 0;
	}
}