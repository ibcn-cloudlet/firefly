package be.iminds.iot.things.api.sensor;

import java.text.DecimalFormat;

public class SensorValue {

	public final double value;
	public final String unit; // TODO make a class for units, for now use simple String
	
	public SensorValue(double v, String u){
		this.value = v;
		this.unit = u;
	}
	
	public double getValue(){
		return value;
	}
	
	public String getUnit(){
		return unit;
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		final DecimalFormat format = new DecimalFormat("#.00");
		b.append(format.format(this.value));
		b.append(" ");
		b.append(unit);
		return b.toString();
	}
}
