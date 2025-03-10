package database;

public class TestDao {
    public static void main(String[] args) {
        Dao dao = new Dao();
        //System.out.println(dao.getRestaurantById(8).getSimId());

        dao.incrementSimId();
        dao.addServicePoint("restaurant", 5, 5, 5, 5);
        dao.addRide(4,2,5,7, 1, 1);
        dao.addRide(1,1,1,1, 1, 1);
        dao.addTicketBooth(2,56,7);
        dao.addRestaurant(1, 1, 1);
        dao.addRestaurant(2, 2, 2);

        System.out.println(dao.getRideById(5).get(0).getAverageQueueTime());
        System.out.println(dao.getRestaurantById(4).get(0).getAverageQueueTime());
        System.out.println(dao.getRideById(4).size());
        System.out.println(dao.getSimId());




    }
}
