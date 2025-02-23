package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class VisualizationNL extends Canvas implements IVisualization {

    private final GraphicsContext gc;

    private double i = 105;
    private double x = 105;
    private int spacing = 2;
    private double j = spacing;
    private double y = spacing;
    private HashMap<Integer, Integer> rideMap;
    private int lastRide;
    private final int ballSize = 8;
    private HashMap<Integer, Deque<Double>> rideQueues;
    private int maxCustomersPerRow;
    private int rowHeight = ballSize + spacing;
    private int rectHeight = 25;


    public VisualizationNL(int w, int h) {
        super(w, h);
        gc = this.getGraphicsContext2D();
        clearScreen();
        rideMap = new HashMap<>();
        rideQueues = new HashMap<>();
        maxCustomersPerRow = (int) (this.getWidth() - 105) / 10;
    }


    public void clearScreen() {
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

//    public void newCustomer(int id) {
//        if (!rideMap.containsKey(id)){
//            rideMap.put(id, 0);
//            iPositions.put(-1, x);
//            gc.setFill(Color.RED);
//            gc.fillOval(x,y,8,8);
//            x = (x + 10) % (this.getWidth()-105) + 105;
//            if (x==105) y+=25;
//        } else {
//            Trace.out(Trace.Level.INFO, "Asiakas " + id + " on jo listassa");
//        }
//
//    }

    public void newCustomer(int id, int whichRide, boolean wristband) {
        rideQueues.putIfAbsent(whichRide, new ArrayDeque<>());
        if (!rideMap.containsKey(id)) {
            rideMap.put(id, whichRide);
        } else {
            lastRide = rideMap.get(id);
            if (!rideQueues.get(lastRide).isEmpty()) {
                // Remove the **last** customer (not necessarily the current one) from their previous ride
                double lastRideX = rideQueues.get(lastRide).removeLast();
                int lastRideBaseY = spacing + rectHeight * lastRide;
                int lastRideCustomerCount = rideQueues.get(lastRide).size();
                int lastRideRow = lastRideCustomerCount / maxCustomersPerRow;
                double lastRideY = lastRideBaseY + lastRideRow * rowHeight;
                gc.setFill(Color.GREEN);
                gc.fillOval(lastRideX - 1, lastRideY - 1, ballSize + 2, ballSize + 2);
            }
            rideMap.put(id, whichRide);
        }
        Deque<Double> queue = rideQueues.get(whichRide);

        int rideBaseY = spacing + rectHeight * whichRide;
        int customerCount = queue.size();
        int row = customerCount / maxCustomersPerRow;
        int col = customerCount % maxCustomersPerRow;
        double x = 105 + col * (ballSize + spacing);
        double y = rideBaseY + row * rowHeight;
        if (wristband) {
            gc.setFill(Color.BLUE);
        } else {
            gc.setFill(Color.RED);
        }
        gc.fillOval(x, y, ballSize, ballSize);
        queue.addLast(x);
    }

    public void rides(int amount) {
        int totalSections = amount + 3;

        for (int k = 0; k < totalSections; k++) {
            int y = k * (rectHeight);
//            gc.setFill(Color.BLUE);
//            gc.fillRect(0,y,this.getWidth(), rectHeight); //jos haluaa taustavärin
            if (k != totalSections - 1) {
                gc.setStroke(Color.BLACK);
                gc.strokeRect(0, y, this.getWidth(), rectHeight);
            }
            gc.setFill(Color.BLACK);
            if (k == 0) {
                gc.fillText("Lipunmyynti", 15, y + 15);
            } else if (k == totalSections - 2) {
                gc.fillText("Ravintola", 15, y + 15);
            } else if (k == totalSections - 1) {
                gc.fillText("Poistuneet", 15, y + 15);
            } else {
                gc.fillText("Laite " + (k), 15, y + 15);
            }

            gc.strokeLine(100, 0, 100, this.getHeight());
        }
    }

    public void showResults(){
        clearScreen();
        gc.setFill(Color.BLACK);
        gc.fillText("Tulokset", 15, 15);
    }
}

