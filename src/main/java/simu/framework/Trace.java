package simu.framework;

/**
 * Trace is a class for managing the trace output of the simulation.
 */
public class Trace {

	public enum Level{INFO, WAR, ERR}
	
	private static Level traceLevel;
	
	public static void setTraceLevel(Level lvl){
		traceLevel = lvl;
	}
	public static void out(Level lvl, String txt){
		if (lvl.ordinal() >= traceLevel.ordinal()){
			System.out.println(txt);
		}
	}
	
	
	
}