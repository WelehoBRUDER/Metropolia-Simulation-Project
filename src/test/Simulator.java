package test;

import sim.framework.Engine;
import sim.framework.Trace;
import sim.framework.Trace.Level;
import sim.model.OwnEngine;

public class Simulator { //Tekstipohjainen

	public static void main(String[] args) {
		
		Trace.setTraceLevel(Level.INFO);
		Engine m = new OwnEngine();
		m.setSimulationTime(1000);
		m.drive();
		///
	}
}
