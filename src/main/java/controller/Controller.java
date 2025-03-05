package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.OwnEngine;
import view.ISimulatorUI;

import java.util.HashMap;

public class Controller implements ISettingsControllerForM, ISettingsControllerForV {   // UUSI
	
	private IEngine engine;
	private ISimulatorUI ui;
	
	public Controller(ISimulatorUI ui) {
		this.ui = ui;
		
	}

	
	// Moottorin ohjausta:
		
	@Override
	public void startSimulation() {
//		engine = new OwnEngine(this, ui.getRideCount()); // luodaan uusi moottorisäie jokaista simulointia varten
//		engine.setSimulationTime(ui.getTime());
//		engine.setDelay(ui.getDelay());
//		ui.getVisualization().clearScreen();
//		((Thread)engine).start();
		//((Thread)moottori).run(); // Ei missään tapauksessa näin. Miksi?		
	}
	
	@Override
	public void slowDown() { // hidastetaan moottorisäiettä
		engine.setDelay((long)(engine.getDelay()*1.10));
	}


	@Override
	public void speedUp() { // nopeutetaan moottorisäiettä
		engine.setDelay((long)(engine.getDelay()*0.9));
	}
	
	// Simulointitulosten välittämistä käyttöliittymään.
	// Koska FX-ui:n päivitykset tulevat moottorisäikeestä, ne pitää ohjata JavaFX-säikeeseen:
		

	@Override
	public void showEndTime(double time) {
		Platform.runLater(()->ui.setEndTime(time));
	}

	
	@Override
	public void visualizeCustomer(int id, int rideid, boolean wristband) {
		Platform.runLater(new Runnable(){
			public void run(){
				ui.getVisualization().newCustomer(id, rideid, wristband);
			}
		});
	}

	public void visualizeResults(){
		Platform.runLater(new Runnable(){
			public void run(){
				ui.getVisualization().showResults();
			}
		});
	}

	public void setWristbandChance(double amount) {
		((OwnEngine)engine).setWristbandChance(amount);
	}

	public double getWristbandChance() {
		return ((OwnEngine)engine).getWristbandChance();
	}

	public int getRideCount() {
		return ui.getRideCount();
	}

	public HashMap<String, Double> getResults() {
		return ((OwnEngine)engine).getResults();
	}

}
