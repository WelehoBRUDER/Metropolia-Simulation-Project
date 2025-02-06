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
		while (eventList.getSeuraavanAika() == clock.getAika()){
			suoritaTapahtuma(eventList.poista());
		}
	}

	private double nykytime(){
		return eventList.getSeuraavanAika();
	}
	
	private boolean simuloidaan(){
		return clock.getAika() < simulationTime;
	}

	protected abstract void suoritaTapahtuma(Tapahtuma t);  // Määritellään simu.model-pakkauksessa Moottorin aliluokassa
	protected abstract void yritaCTapahtumat();	// Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	protected abstract void alustukset(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	protected abstract void tulokset(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa
	
}