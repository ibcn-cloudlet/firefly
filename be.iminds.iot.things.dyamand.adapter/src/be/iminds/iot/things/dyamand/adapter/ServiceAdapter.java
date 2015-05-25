package be.iminds.iot.things.dyamand.adapter;


public interface ServiceAdapter {

	/**
	 * @return The target interfaces to adapt to 
	 */
	public String[] getTargets();
	
	/**
	 * Translate the ServicePojo source to an object that implements all target interfaces 
	 * @param source The source service
	 * @return an object implementing all target interfaces
	 */
	public Object getServiceObject(final Object source) throws Exception;
	
	/**
	 * Translate a variable name and value to a new state variable
	 * @param variable state variable name
	 * @param value state variable value
	 * @return translated state variable
	 */
	public StateVariable translateStateVariable(final String variable,
		    final Object value) throws Exception;
	
}
