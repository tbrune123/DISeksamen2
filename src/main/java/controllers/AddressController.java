package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Address;
import utils.Log;

public class AddressController {

  private static DatabaseController dbCon;

  public AddressController() {
    dbCon = new DatabaseController();
  }


  public static Address setAddress(ResultSet rs) {
    try {
      Address address = new Address(rs.getInt("a_id"),
              rs.getString("name"),
              rs.getString("street_address"),
              rs.getString("city"),
              rs.getString("zipcode")
      );

      return address;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Address createAddress(Address address) {

    // Write in log that we've reach this step
    Log.writeLog(ProductController.class.getName(), address, "Adding an address in the DB", 0);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the product in the DB
    int addressID = dbCon.insert(
        "INSERT INTO address(name, city, zipcode, street_address) VALUES('"
            + address.getName()
            + "', '"
            + address.getCity()
            + "', '"
            + address.getZipCode()
            + "', '"
            + address.getStreetAddress()
            + "')");

    if (addressID != 0) {
      //Update the productid of the product before returning
      address.setId(addressID);
    } else{
      // Return null if product has not been inserted into database
      return null;
    }

    // Return product, will be null at this point
    return address;
  }
  
}
