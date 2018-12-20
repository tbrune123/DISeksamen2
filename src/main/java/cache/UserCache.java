package cache;

import java.util.ArrayList;
import controllers.UserController;
import model.User;
import utils.Config;

// TODO: add UserCache FIX

public class UserCache {

    // List of products
  private static ArrayList<User> userList;

    // Time cache should live
    private long lifeTimeOfCache;

    // Sets a stamp when the cache has been created
    private static long created;

    // If there is no update, it will get orders from arrayList
    public UserCache() {
        this.lifeTimeOfCache = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {

        // Clear cache if we want to
        // The method is checking if the time-stamp is out of date
        // We will check for update if the list is empty
        if (forceUpdate
                || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis()/100L))
                || this.userList ==null) {

            // Getting the products from the controller, because we want to update
            ArrayList<User> users = UserController.getUsers();


            // Setting the products for the instance and set timestamp
            this.userList = users;
            this.created = System.currentTimeMillis();
        }

        // Return the users
        return this.userList;
    }

    public User getUser(boolean forceUpdate, int userID) {
        User user = new User();

        // ForceUpdate if the time og cachce is out or userList is empty
        if (forceUpdate
                || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis())) || this.userList ==null) {

            // Get user from controller.
            user = UserController.getUser(userID);

            return user;
        } else {
            // If the cache is up to date, use  arraylist untill the correct product_ID is found
            for (User u : userList){
                if (userID==u.getId())
                    user = u;
                return user;
            }
        }

        return null;
    }
}
