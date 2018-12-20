package cache;

import controllers.OrderController;

import java.util.ArrayList;

import model.Order;
import utils.Config;

// TODO: add OrderCache FIX

public class OrderCache {

    // List of products
    private static ArrayList<Order> orderList;

    // Time cache the should live
    private long lifeTimeOfCache;

    // Sets a stamp when the cache has been created
    private static long created;

    //sette orderCahce til = ttl i config
    public OrderCache() {
        this.lifeTimeOfCache = Config.getOrderTtl();
    }

    // If there is no update, it will get orders from arrayList
    public ArrayList<Order> getOrders(Boolean forceUpdate) {

        // Clear cache if we want to
        // The method is checking if the time-stamp is out of date
        // We will check for update if the list is empty
        if (forceUpdate
                || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis()/100L))
                || this.orderList ==null) {

            // Getting the products from the controller, because we want to update
            ArrayList<Order> orders = OrderController.getOrders();


            // Setting the products for the instance and set timestamp
            this.orderList = orders;
            this.created = System.currentTimeMillis();
        }

        // Return the orders
        return this.orderList;
    }

    public Order getOrder(boolean forceUpdate, int orderID) {
        Order order = new Order();

        // ForceUpdate if the time og cachce is out or userList is empty
        if (forceUpdate
                || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis())) || this.orderList ==null) {

            // If cache needs update: Use the ordercontroller to get data from database
            order = OrderController.getOrder(orderID);

            return order;
        } else {
            /// If the cache is up to date, use arraylist untill the correct product_ID is found
            for (Order o : orderList){
                if (orderID==o.getId())
                    order = o;
                return order;
            }
        }
        return null;
    }
}
