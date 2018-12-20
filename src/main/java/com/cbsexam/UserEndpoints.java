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

  // This method first checks for user in the cache, if there is no user or the user needs to be updated, it will
  //collect from the database instead. The user gets encrypted with json and send to the user. If things fail
  //it will give error 404 and some text ;=)
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = userCache.getUser(forceUpdate,idUser);

    // TODO: Add Encryption to JSON FIX
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);
    json= Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200 or 404 if things go wrong
    // TODO: What should happen if something breaks down? FIX
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(404).entity("this user hasen't been created yet").build();
    }
  }

  /** @return Responses */

  // This method first checks for users in the cache, if there is no users or the users needs to be updated, it will
  //collect from the database instead. The users gets encrypted with json and send to the user. If things fail
  //it will give error 404 and some text ;=)
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log know that we are here
    Log.writeLog(this.getClass().getName(), this, "Getting all users", 0);

    // Get a list of users by first checking the cache and therein if there is not cache have the usercontroller contact
    // the database and get them + create a new cache.
    ArrayList<User> users = userCache.getUsers(forceUpdate);


    // TODO: Add Encryption to JSON FIX
    String json = new Gson().toJson(users);
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200 or 404
    if (users != null) {
      // just created a new cache, so setting forceUpdate to false
      this.forceUpdate = false;
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(404).entity("The guy responsible for getting users is not home atm").build();
    }
  }


  // This method creates an user based on the information provided from the user making it
  // Using the controller to save the new user in the database. The user is then converted to json and send
  // back to the user. If anything goes wrong it will return 404 and a text ;=)
  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // just created a new cache, so setting forceUpdate to false
      this.forceUpdate = true;
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("The guys says you cant't create that user, call my number: 60 15 16 09").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. FIX
  // This method takes the information provided by the user and logging in if information matches.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    // Read the json from body and transfer it to a user class
    User userToBe = new Gson().fromJson(body, User.class);

    // Using the email & password to verify the user in the controller and then give them a token.
    User user = UserController.login(userToBe);

    // Return the user with the status code 200 if succesful or 401 if failed
    if (user != null) {
      //Sending a message to the user that he/she is logged in and then giving them a token.
      String msg = "Welcome "+user.getFirstname() + "! Here is your token, save it for your time being in this session" +
              " ,because you will need it while using the system. This is your token:\n\n"+user.getToken() + "\n\nIf you " +
              "can relog if you lose your token, you will then be provited with a new :=) ";
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(msg).build();
    } else {
      return Response.status(401).entity("There is no user matching, please try again or call support, you know my number ;=)").build();
    }


  }



  // TODO: Make the system able to delete users FIX
  // This method takes the information provited from the user. They need to provide their ID and token, so that only the
  // user who have the rights to delete/update are able to do it. The token verify is then used and if its a match it will
  // delete, if not sending a 401 error saying they are not authorised. If it succseeds the use will be deleted from the
  // database and a forceUpdate on the cache.
  @DELETE
  @Path("/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("idUser") int idUser, String body) {

    //Setting a user from the information givin
    User userToDelete = new Gson().fromJson(body, User.class);

    userToDelete.setId(idUser);
    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Deleting a user", 0);

    // Use the ID and token match it will delete from the controller
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


  // TODO: Make the system able to update users FIX


  // This method takes the information provited from the user. They need to provide their ID and token, so that only the
  // user who have the rights to delete/update are able to do it. The token verify is then used and if its a match it will
  // update, if not sending a 401 error saying they are not authorised. If it succseeds the use will be deleted from the
  // database and a forceUpdate on the cache.
  @PUT
  @Path("/update/{idUser}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("idUser") int idUser, String body) {

    //Setting a user from the information - note the changes to the user object - we have added token as a instance variable
    User userToUpdate = new Gson().fromJson(body, User.class);

    userToUpdate.setId(idUser);

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
