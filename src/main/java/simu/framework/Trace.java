package simu.framework;

/**
 * Trace is a class for managing the trace output of the simulation.
 */
public class Trace {
	/**
	 * Enumeration for the trace levels.
	 */
	public enum Level{INFO, WAR, ERR}
	
	private static Level traceLevel;

	/**
	 * Constructor for the Trace class.
	 */
	public static void setTraceLevel(Level lvl){
		traceLevel = lvl;
	}

	/**
	 * Outputs a trace message.
	 * @param lvl The level of the trace message
	 * @param txt The text of the trace message
	 */
	public static void out(Level lvl, String txt){
		if (lvl.ordinal() >= traceLevel.ordinal()){
			System.out.println(txt);
		}
	}
	
	
	
}