package simu.framework;

public interface IEngine { // UUSI
		
	// Kontrolleri käyttää tätä rajapintaa
	
	public void setSimulationTime(double time);
	public void setDelay(long time);
	public long getDelay();
}
