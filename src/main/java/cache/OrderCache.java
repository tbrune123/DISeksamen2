package cache;

import controllers.OrderController;
import controllers.ProductController;
import java.util.ArrayList;

import model.Order;
import model.Product;
import utils.Config;

public class OrderCache {

    // List of products
    private static ArrayList<Order> orders;

    // Time cache should live
    private long ttl;

    // Sets when the cache has been created
    private static long created;

    public OrderCache() {
        this.ttl = Config.getOrderTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate) {

        // If we whis to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new products
        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis()))
                || this.orders==null) {

            // Get products from controller, since we wish to update.
            ArrayList<Order> orders = OrderController.getOrders();


            // Set products for the instance and set created timestamp
            this.orders = orders;
            this.created = System.currentTimeMillis();
        }

        // Return the documents
        return this.orders;
    }

    public Order getOrder(boolean forceUpdate, int orderID) {
        Order order = new Order();

        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis())) || this.orders==null) {

            // If cache needs update: Using the ordercontroller to get order from database
            order = OrderController.getOrder(orderID);

            return order;
        } else {
            // If the cache is alright, go through arraylist till right ID is found **/
            for (Order o : orders){
                if (orderID==o.getId())
                    order = o;
                return order;
            }
        }
        return null;
    }
}
