package cache;

import java.util.ArrayList;
import controllers.UserController;
import model.User;
import utils.Config;

public class UserCache {

    // List of products
  private ArrayList<User> userList;

    // Time cache should live
    private long lifeTimeOfCache;

    // Sets when the cache has been created
    private long created;

    public UserCache() {
        this.lifeTimeOfCache = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {

        // If we whis to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new products
        if (forceUpdate
                || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis()))
                || this.userList ==null) {

            // Get products from controller, since we wish to update.
            ArrayList<User> users = UserController.getUsers();


            System.out.println("det virker");


            // Set products for the instance and set created timestamp
            this.userList = users;
            this.created = System.currentTimeMillis();
        }

        // Return the documents
        return this.userList;
    }

    public User getUser(boolean forceUpdate, int userID) {
        User user = new User();

        if (forceUpdate
                || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis())) || this.userList ==null) {

            // Get user from controller.
            user = UserController.getUser(userID);

            return user;
        } else {
            // Get user from already made arraylist by checking against ID
            for (User u : userList){
                if (userID==u.getId())
                    user = u;
                return user;
            }
        }

        return null;
    }
}
