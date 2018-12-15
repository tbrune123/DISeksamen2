package cache;

import java.util.ArrayList;
import controllers.UserController;
import model.User;
import utils.Config;

public class UserCache {

    // List of products
  private ArrayList<User> users;

    // Time cache should live
    private long ttl;

    // Sets when the cache has been created
    private long created;

    public UserCache() {
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {

        // If we whis to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new products
        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis()))
                || this.users==null) {

            // Get products from controller, since we wish to update.
            ArrayList<User> users = UserController.getUsers();


            // Set products for the instance and set created timestamp
            this.users = users;
            this.created = System.currentTimeMillis();
        }

        // Return the documents
        return this.users;
    }

    public User getUser(boolean forceUpdate, int userID) {
        User user = new User();

        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis())) || this.users==null) {

            // Get user from controller.
            user = UserController.getUser(userID);

            return user;
        } else {
            // Get user from already made arraylist by checking against ID
            for (User u : users){
                if (userID==u.getId())
                    user = u;
                return user;
            }
        }

        return null;
    }
}
