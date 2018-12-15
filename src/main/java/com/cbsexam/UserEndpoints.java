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
import utils.Token;



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
    User user = userCache.getUser(forceUpdate,idUser);

    // TODO: Add Encryption to JSON FIX
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json= Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? FIX
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(404).entity("this user hasen't been created yet").build();
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

    if (users != null) {
      this.forceUpdate = true;
      // Return the users with the status code 200
      return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
    } else {
      return Response.status(400).entity("The guy responsible for getting users is not home atm").build();
    }

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
      this.forceUpdate = true;
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("The guys says you cant't create that user, call my number: 60 15 16 09").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. FIX
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    // Read the json from body and transfer it to a user class
    User userToBe = new Gson().fromJson(body, User.class);

    // Use the email and password to get the user verify the user in the controller which also gives them a token.
    User user = UserController.login(userToBe);

    // Return the user with the status code 200 if succesful or 401 if failed
    if (user != null) {
      //Welcoming the user and providing him/her with the token they need in order to delete or update their user.
      String msg = "Welcome "+user.getFirstname() + "! This is your token, please save it for your time in the session" +
              " ,as you will need it throughout the system. This is your token:\n\n"+user.getToken() + "\n\nIf you lose your token, you can relog and get a new.";
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(msg).build();
    } else {
      return Response.status(401).entity("THere is no user matching, please try again or call support").build();
    }


  }

  // TODO: Make the system able to delete users FIX
  @DELETE
  @Path("/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int idUser, String body) {

    //Setting a user from the information - note the changes to userobject - we have added token as a instance variable
    User userToDelete = new Gson().fromJson(body, User.class);

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Deleting a user", 0);

    // Use the ID and token to first verify the possibly delete the user from the database via controller.
    if (Token.verifyToken(userToDelete.getToken(), userToDelete)) {
      boolean deleted = UserController.deleteUser(idUser);

      //if user was deleted we need to force an update on cache and let the user know it was successfull with status 200
      //and a message
      if (deleted) {
        forceUpdate = true;
        // Return a response with status 200 and a massage
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("User deleted").build();
      } else {
        // Return a response with status 400 and a message
        return Response.status(400).entity("No user found to be deleted - you may try again").build();
      }
    } else {
      //If the token verifier does not check out.
      return Response.status(401).entity("You do not have authorizion for this action - please log in first").build();
    }
  }
  //public Response deleteUser(String x) {

    // Return a response with status 200 and JSON as type
    //return Response.status(400).entity("Endpoint not implemented yet").build();
  //}

  // TODO: Make the system able to update users FIX
  @PUT
  @Path("/update/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("idUser") int idUser, String body) {

    //Setting a user from the information - note the changes to the user object - we have added token as a instance variable
    User userToUpdate = new Gson().fromJson(body, User.class);

    //Writing log to let know we are here.
    Log.writeLog(this.getClass().getName(), this, "Updating a user", 0);

    // Use the ID and token to first verify the possibly update the user in the database via controller.
    if (Token.verifyToken(userToUpdate.getToken(), userToUpdate)) {
      boolean affected = UserController.updateUser(userToUpdate);

      //If we have updated the user, we need to force an update on cache and let the user know it was successfull with status 200
      //and returning the json.
      if (affected) {
        forceUpdate = true;
        String json = new Gson().toJson(userToUpdate);

        //Returning responses to user
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
      } else {
        return Response.status(400).entity("Could not update user").build();
      }
    } else {
      //If the token verifier does not check out.
      return Response.status(401).entity("You're not authorized to do this - please log in").build();
    }
  }
}
