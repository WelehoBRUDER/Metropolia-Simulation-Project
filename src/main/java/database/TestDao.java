package database;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TestDao {
    public static void main(String[] args) {
        Dao dao = new Dao();
        //System.out.println(dao.getRestaurantById(8).getSimId());

        //dao.incrementSimId();
        //dao.addServicePoint(4,4,4,4,4,4,4,4,4,4, 4);
        //dao.addRide(4,2,5,7, 1, 1);
        //dao.addRide(1,1,1,1, 1, 1);
        //dao.addTicketBooth(2,2,56,7);
        //dao.addRestaurant(1, 1, 1);
        //dao.addRestaurant(22, 22, 22);

        //System.out.println(dao.getRideById(1).get(0).getAverageQueueTime());
        //System.out.println(dao.getRideById(1));
        //System.out.println(dao.getRestaurantById(4).get(0).getAverageQueueTime());
        //System.out.println(dao.getRideById(4).size());
        System.out.println(dao.getSimId());
        System.out.println(dao.getSimTimestampById(1));
        System.out.println(dao.getSimTimestamps().get(1));
        /*
        LocalDateTime localDateTime = Instant.ofEpochMilli(dao.getSimTimeStampById(1))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        System.out.println(localDateTime);

         */




    }
}