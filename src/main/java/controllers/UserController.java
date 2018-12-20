package controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.User;
import utils.Hashing;
import utils.Log;
import utils.Token;

public class UserController {

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  // This method are getting the user based on the ID from the database
  // The method builds an SQL query to get all the information about the user in a resultset
  // we execute prepared statement and form the user to be returned
  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for database
    String sql = "SELECT * FROM user where u_id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
            new User(
                rs.getInt("u_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"));

        // return the create object
        return user;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Get all users in database
   * @return
   */

  // This method are getting the user based on the ID from the database
  // The method builds an SQL query to get all the information about the user in a resultset
  // we execute prepared statement and form the user to be returned. lastly the method adds the users to arrayList
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);

    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
            new User(
                rs.getInt("u_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  /** @param user
   * @return user
   * 1. The createUser() methods takes the User we initialized in the endpoint
   * 2. First we set a created_at for the user
   * 3. Next we build the SQL-prepared statement and insert it to our database
   * 4. The genereated key is set to the user_id, and the user is returned.
   */

  // This method takes the user made in the endpoint. Next the method set a created_at for the user, then building
  // the SQL prepared statement and sending it to the database.
  public static User createUser(User user) {

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    user.setPassword(Hashing.sha(user.getPassword()));
    // Insert the user in the DB
    // TODO: Hash the user password before saving it. FIX
    int userID = dbCon.insert(
        "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
            + user.getFirstname()
            + "', '"
            + user.getLastname()
            + "', '"
            + user.getPassword()
            + "', '"
            + user.getEmail()
            + "', "
            + user.getCreatedTime()
            + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else{
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }



  // This method takes the user made in the endpoint. Next the password is being hashed and the method is building the
  // SQL-prepared statement and updating it in the database.
  public static boolean updateUser(User user) {

    Log.writeLog(UserController.class.getName(), user, "Actually updating a user in DB", 0);

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Hashing the password
    user.setPassword(Hashing.sha(user.getPassword()));

    boolean affected = dbCon.update(
            "UPDATE user SET " +
                    "first_name = " + "'" + user.getFirstname() + "'," +
                    "last_name = " + "'" + user.getLastname() + "'," +
                    "password = " + "'" + user.getPassword() + "'," +
                    "email = " + "'" + user.getEmail() + "'" +
                    "WHERE u_id = " + "'" + user.getId() + "'");

    return affected;
  }


  //The deleteUser() methods deletes the user from the database based on the id of the user and then
  // building the SQL-prepared statement and compare it with our user data in the database.
  public static boolean deleteUser(int idUser) {


    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "Delete FROM user where id=" + idUser;

    boolean deleted = dbCon.delete(sql);
    if (deleted)
      return true;
    else {
      return false;
    }
    // Insert the user in the DB
  }



  //This method takes the user we made in the endpoint and hash the password.
  // Then it builds the SQL-prepared statement and compare it with our user-data in the database
  // and if a match is found a token will be generated and declared to the user
  public static User login(User user){

    //letting the user know that the program is trying to log on, in cause of delay
    Log.writeLog(UserController.class.getName(), user, "Logging you on", 0);
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }
    // TODO: Hash the user password before saving it. FIX
    user.setPassword(Hashing.sha(user.getPassword()));

    try {
      String sql = "SELECT * FROM user WHERE email = ? AND password = ?";

      PreparedStatement preparedStatement = dbCon.getConnection().prepareStatement(sql);
      preparedStatement.setString(1, user.getEmail());
      preparedStatement.setString(2, user.getPassword());

      ResultSet rs = preparedStatement.executeQuery();

      if (rs.next()) {
        user =
                new User (
                        rs.getInt("u_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"));


                        user.setToken(Token.CreateToken(user));


        System.out.println("Logged on");
        return user;

      } else {
        System.out.println("no such user found");
      }


    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return null;

    }


  // initialising, instansiating and declaring a user, to be returned
  public static User setUser(ResultSet rs){

    try{
      User u = new User(rs.getInt("u_id"),
              rs.getString("first_name"),
              rs.getString("last_name"),
              rs.getString("password"),
              rs.getString("email"));

      return u;
    }catch(SQLException e){

    }
    return null;
  }

}
