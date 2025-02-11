package simu.framework;

public abstract class Engine {
	
	private double simulationTime = 0;
	
	private Clock clock;
	
	protected EventList eventList;

	public Engine(){

		clock = Clock.getInstance(); // Otetaan clock muuttujaan yksinkertaistamaan koodia
		
		eventList = new EventList();
		
		// Palvelupisteet luodaan simu.model-pakkauksessa Moottorin aliluokassa 
		
		
	}

	public void setSimulationTime(double time) {
		simulationTime = time;
	}
	
	
	public void drive(){
		alustukset(); // luodaan mm. ensimmäinen tapahtuma
		while (simuloidaan()){
			
			Trace.out(Trace.Level.INFO, "\nA-vaihe: clock on " + nykytime());
			clock.setAika(nykytime());
			
			Trace.out(Trace.Level.INFO, "\nB-vaihe:" );
			suoritaBTapahtumat();
			
			Trace.out(Trace.Level.INFO, "\nC-vaihe:" );
			yritaCTapahtumat();

		}
		tulokset();
		
	}
	
	private void suoritaBTapahtumat(){
		while (eventList.getNextTime() == clock.getTime()){
			processEvent(eventList.poista());
		}
	}

	private double currentTime(){
		return eventList.getNextTime();
	}
	
	private boolean simulating(){
		return clock.getAika() < simulationTime;
	}

	protected abstract void runEvents(Event t);  // Määritellään simu.model-pakkauksessa Moottorin aliluokassa
	protected abstract void attemptEvents();	// Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	protected abstract void init(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	protected abstract void results(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa
	
}