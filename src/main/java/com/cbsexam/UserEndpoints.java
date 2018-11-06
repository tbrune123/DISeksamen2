package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

  private static UserCache userCache = new UserCache();
  public static boolean forceUpdate=true;

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON FIX
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json= Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? FIX
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("this user has not yet been created :-(").build();
    }
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(forceUpdate);

    // TODO: Add Encryption to JSON FIX
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);
    json= Encryption.encryptDecryptXOR(json);

    this.forceUpdate = true;

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();

  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not find user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to delete users FIX
  @DELETE
  @Path("/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int idUser) {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Deleting a user", 0);

    // Use the ID to delete the user from the database via controller.
    boolean deleted = UserController.deleteUser(idUser);

    if (deleted) {

      /** Spørg om hvordan man får JSON "Bruger slettet" **/
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Bruger slettet").build();
      // return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity("Bruger slettet").build();
    }else {
      // Return a response with status 200 and JSON as type
      return Response.status(400).entity("Could not delete user").build();
    }
  }
  //public Response deleteUser(String x) {

    // Return a response with status 200 and JSON as type
    //return Response.status(400).entity("Endpoint not implemented yet").build();
  //}

  // TODO: Make the system able to update users FIX

  //public Response updateUser(String x) {

    // Return a response with status 200 and JSON as type
    //return Response.status(400).entity("Endpoint not implemented yet").build();
  //}
    @PUT
    @Path("/{idUser}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("idUser") int idUser, String body) {

      // Read the json from body and transfer it to a user class
      User changeUser = new Gson().fromJson(body, User.class);

      // Use the controller to add the user
      User updatedUser = UserController.updateUser(changeUser);

      // Get the user back with the added ID and return it to the user
      String json = new Gson().toJson(updatedUser);

      // Return the data to the user
      if (updatedUser != null) {
        // Return a response with status 200 and JSON as type

        this.forceUpdate = true;
        
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
      } else {
        return Response.status(400).entity("Could not update user").build();
      }
    }
}
