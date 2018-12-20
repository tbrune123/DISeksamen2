package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.*;
import utils.Log;

public class OrderController {

  private static DatabaseController dbCon;

  public OrderController() {
    dbCon = new DatabaseController();
  }


  // This method takes the order based on the ID
  // Then building an SQL query to get all the information about the orders in a resultset
  // If there is more than one product in the order, its using the else statement
  public static Order getOrder(int orderId) {
    // check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL string to query
    String sql = "SELECT * FROM orders\n" +
            "inner join\n" +
            "user ON orders.user_id = user.u_id\n" +
            "inner join \n" +
            "line_item ON orders.o_id = line_item.order_id \n" +
            "inner join \n" +
            "address AS ba ON orders.billing_address_id = ba.a_id\n" +
            "inner join \n" +
            "address as sa ON orders.shipping_address_id = sa.a_id\n" +
            "inner join \n" +
            "product ON line_item.product_id  = product.p_id \n" +
            "where orders.o_id = " + orderId;

    // Do the query in the database and create an empty object for the results
    ResultSet rs = dbCon.query(sql);
    // New order object
    Order order = null;
    // User object
    User user = null;
    // New lineitem object
    LineItem lineItem = null;
    //New LineitemList
    ArrayList<LineItem> lineItemsList = new ArrayList<>();
    //New productlist
    Product product = null;
    // New adress object
    Address billingAddress = null;
    // New adress object
    Address shippingAddress = null;

    try {
      while (rs.next()) {
        if (order == null) {
          user = UserController.setUser(rs);

          product = ProductController.setProduct(rs);

          lineItem = LineItemController.setLineItem(rs, product);

          lineItemsList.add(lineItem);

          // Creating new billingAddress
          billingAddress = AddressController.setBillingAddress(rs);

          // Creating new Shippingaddress
          shippingAddress = AddressController.setShippingAddress(rs);

          order = setOrder1(rs, user, lineItemsList, billingAddress, shippingAddress);

        }else {
          product = ProductController.setProduct(rs);
          lineItem = LineItemController.setLineItem(rs, product);
          order.getLineItems().add(lineItem);
        }
      }
      // Returns the build order
      return order;

    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }
    return null;
  }


  /**
   * Get all orders in database
   *
   * @return
   */


  // This method takes all the orders from the database and building an SQL query to get all the information
  // about the orders in a resultset. If there is more then one product in the order, it makes the else if statement.
  // Then inserting the orders in the arrayList
  public static ArrayList<Order> getOrders() {
    // check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }
    // Orders instead of order in sql statement

    String sql = "SELECT * FROM orders\n" +
            "inner join\n" +
            "             user ON orders.user_id = user.u_id\n" +
            "             inner join \n" +
            "             line_item ON orders.o_id = line_item.order_id \n" +
            "             inner join \n" +
            "             address AS ba ON orders.billing_address_id = ba.a_id\n" +
            "             inner join \n" +
            "             address as sa ON orders.shipping_address_id = sa.a_id\n" +
            "             inner join \n" +
            "             product ON line_item.product_id  = product.p_id\n" +
            "             order by orders.o_id";

    ArrayList<Order> orders = new ArrayList<Order>();
    // Do the query in the database and create an empty object for the results
    ResultSet rs = dbCon.query(sql);
    // New order object
    // Order order = null;

    try {
      while(rs.next()) {

        // User object
        User user = null;
        // New lineitem object
        LineItem lineItem = null;
        // New adress object
        Address billingAddress = null;
        // New adress object
        Address shippingAddress = null;
        // new product object
        Product product = null;
        //New LineitemList
        ArrayList<LineItem> lineItemsList = new ArrayList<>();

        if (orders.isEmpty() || rs.getInt("o_id") != orders.get(orders.size()-1).getId()) {

          // Creating new user object
          user = UserController.setUser(rs);

          product = ProductController.setProduct(rs);

          lineItem = LineItemController.setLineItem(rs, product);

          lineItemsList.add(lineItem);

          // Creating new billingAddress
          billingAddress = AddressController.setBillingAddress(rs);
          // Creating new shippingAddress
          shippingAddress = AddressController.setShippingAddress(rs);

          // Creating new order
          Order order = setOrder1(rs, user, lineItemsList, billingAddress, shippingAddress);

          // Adding order to arraylist
          orders.add(order);
        } else if (rs.getInt("o_id") == orders.get(orders.size()-1).getId()){
          product = ProductController.setProduct(rs);

          lineItem = LineItemController.setLineItem(rs, product);
          lineItemsList.add(lineItem);

          orders.get(orders.size()-1).getLineItems().add(lineItem);
        }

      }
      return orders;
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // return the orders
    return orders;

  }



  // This method takes the order made in the endpoint, to add more information to it. First the method sets a -
  // created and update for the order. To make sure all the information needed is right, the method use transactions
  // When the transaction has started it sets the 2 address-IDs with the keys returned from the getAddress methods
  // Then it gets the user who created the order and setting the order customer to this
  public static Order createOrder(Order order) {


    // Write in log that we've reach this step
    Log.writeLog(OrderController.class.getName(), order, "Actually creating a order in DB", 0);

    // Set creation and updated time for order.
    order.setCreatedAt(System.currentTimeMillis() / 1000L);
    order.setUpdatedAt(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    try {

      //We set the autocommit to false, making the way to use transactions
      dbCon.getConnection().setAutoCommit(false);

      //Setting the IDs of billing- and shippingAddress to the order
      //in other word: Save addresses to database and save them back to initial order instance
      order.setBillingAddress(AddressController.createAddress(order.getBillingAddress()));
      order.setShippingAddress(AddressController.createAddress(order.getShippingAddress()));

      //Setting the ID of the user to the order.
      order.setCustomer(UserController.getUser(order.getCustomer().getId()));

      // TODO: Enable transactions in order for us to not save the order if somethings fails for some of the other inserts: FIX
      // Insert the order in the DB
      int orderID = dbCon.insert("INSERT INTO orders(user_id, billing_address_id, shipping_address_id, order_total, order_created_at, order_updated_at) VALUES("
              + order.getCustomer().getId()
              + ", "
              + order.getBillingAddress().getId()
              + ", "
              + order.getShippingAddress().getId()
              + ", "
              + order.calculateOrderTotal()
              + ", "
              + order.getCreatedAt()
              + ", "
              + order.getUpdatedAt()
              + ")");

      if (orderID != 0) {
        //Update the order of the order before returning further down
        order.setId(orderID);
      }

      // Create an empty list in order to go trough items and then save them back with ID
      ArrayList<LineItem> items = new ArrayList<LineItem>();

      // Save line items to database with the respective order id
      for (LineItem item : order.getLineItems()) {
        item = LineItemController.createLineItem(item, order.getId());
        items.add(item);
      }

      //Add line items to the order, commit and return the order
      order.setLineItems(items);
      dbCon.getConnection().commit();
      return  order;

      // adding nullpointerexception, since we are using getUser instead of createUser, we would like people to be
      // logged in before they create an order.
    } catch (SQLException | NullPointerException e) {
      System.out.println(e.getMessage());
      try {
        //If exception was catched, we roll our statements to the database back.
        System.out.println("rolling back");
        dbCon.getConnection().rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
    finally {
      try {
        //Setting the autocommit to true.
        dbCon.getConnection().setAutoCommit(true);

      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return  null;
  }




  public static Order setOrder1(ResultSet rs, User user, ArrayList<LineItem> lineItemsList, Address billingsAddress, Address shippingsAddres) {
    try {
      Order order = new Order(
              rs.getInt("o_id"),
              user,
              lineItemsList,
              billingsAddress,
              shippingsAddres,
              rs.getFloat("order_total"),
              rs.getLong("order_created_at"),
              rs.getLong("order_updated_at"));

      return order;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}