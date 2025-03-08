package database;

public class TestDao {
    public static void main(String[] args) {
        Dao dao = new Dao();
        System.out.println(dao.getRestaurantById(8).getSimId());
        dao.testDao();
        /*
        dao.incrementSimId();
        dao.addRestaurant(2, 5, 7);

         */
    }
}
