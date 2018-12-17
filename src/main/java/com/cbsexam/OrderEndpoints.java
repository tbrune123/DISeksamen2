package com.cbsexam;

import cache.OrderCache;
import com.google.gson.Gson;
import controllers.OrderController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Order;
import utils.Encryption;

@Path("orders")
public class OrderEndpoints {

  /** Gemmer alle ordre i cache **/
  private static OrderCache orderCache = new OrderCache();

  /** Tjekker om der er brug for en update til cache **/
  public static boolean forceUpdate=true;

  /**
   * @param idOrder
   * @return Responses
   */
  @GET
  @Path("/{idOrder}")
  public Response getOrder(@PathParam("idOrder") int idOrder) {

    // Call our controller-layer in order to get the order from the DB
    Order order = orderCache.getOrder(forceUpdate,idOrder);

    // TODO: Add Encryption to JSON FIX
    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(order);
    json= Encryption.encryptDecryptXOR(json);

    if (order != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
    } else {
      // Return a response with status 400 and a message in text
      return Response.status(404).entity("It seems that the order-guy went home for today, no orders to be found").build();
    }


  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getOrders() {

    // Call our controller-layer in order to get the order from the DB
    ArrayList<Order> orders = orderCache.getOrders(forceUpdate);

    // TODO: Add Encryption to JSON FIX
    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(orders);
    json= Encryption.encryptDecryptXOR(json);




    if (orders != null) {
      this.forceUpdate = false;
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      // Return a response with status 400 and a message in text
      return Response.status(404).entity("It seems that the order-guy went home for today, no orders to be found").build();
    }


  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createOrder(String body) {

    // Read the json from body and transfer it to a order class
    Order newOrder = new Gson().fromJson(body, Order.class);

    // Use the controller to add the user
    Order createdOrder = OrderController.createOrder(newOrder);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createdOrder);

    // Return the data to the user
    if (createdOrder != null) {
      // Return a response with status 200 and JSON as type

      this.forceUpdate = false;

      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {

      // Return a response with status 400 and a message in text
      return Response.status(400).entity("It seems that the order-guy went home for today, cannot create any order").build();
    }
  }
}