package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Product;
import utils.Log;

public class ProductController {

  private static DatabaseController dbCon;

  public ProductController() {
    dbCon = new DatabaseController();
  }



  // This method finds the product based on the ID
  // Then building an SQL query to get all the information about the product in a resultset
  // Excicuting the prepared statement and form the product to be returned
  public static Product getProduct(int id) {

    // check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the SQL query for the DB
    String sql = "SELECT * FROM product where id=" + id;

    // Run the query in the DB and make an empty object to return
    ResultSet rs = dbCon.query(sql);
    Product product = null;

    try {
      // Get first row and create the object and return it
      String gg = "Dette skal fjernes";
      if (rs.next()) {
        product =
            new Product(
                rs.getInt("p_id"),
                rs.getString("product_name"),
                rs.getString("sku"),
                rs.getFloat("price"),
                rs.getString("description"),
                rs.getInt("stock"));

        // Return the product
        return product;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return empty object
    return product;
  }


  // This method finds the product based on the sku
  // Then building an SQL query to get all the information about the product in a resultset
  // Excicuting the prepared statement and form the product to be returned
  public static Product getProductBySku(String sku) {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "SELECT * FROM product where sku='" + sku + "'";

    ResultSet rs = dbCon.query(sql);
    Product product = null;

    try {
      if (rs.next()) {
        product =
            new Product(
                rs.getInt("p_id"),
                rs.getString("product_name"),
                rs.getString("sku"),
                rs.getFloat("price"),
                rs.getString("description"),
                rs.getInt("stock"));

        return product;
      } else {
        System.out.println("No user found");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    return product;
  }

  /**
   * Get all products in database
   *
   * @return
   */


  // This method takes all the products from the database
  // Then building an SQL query to get all the information about our product in a resultset
  // then exicuting the prepared statement and adding the prodcuts to the arrayList
  public static ArrayList<Product> getProducts() {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // TODO: Use caching layer. FIX
    //I use the caching layer in ProductEndpoints
    String sql = "SELECT * FROM product";

    ResultSet rs = dbCon.query(sql);
    ArrayList<Product> products = new ArrayList<Product>();

    try {
      while (rs.next()) {
        Product product =
            new Product(
                rs.getInt("p_id"),
                rs.getString("product_name"),
                rs.getString("sku"),
                rs.getFloat("price"),
                rs.getString("description"),
                rs.getInt("stock"));

        products.add(product);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    return products;
  }


  // This method takes the product made in the endpoint
  // Setting a created_at for the product
  // Building the SQL prepared statement and sending it to the database
  // The genereated key is set to the product_id
  public static Product createProduct(Product product) {

    // Write in log that we've reach this step
    Log.writeLog(ProductController.class.getName(), product, "Actually creating a product in DB", 0);

    // Set creation time for product.
    product.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the product in the DB
    int productID = dbCon.insert(
        "INSERT INTO product(product_name, sku, price, description, stock, product_created_at) VALUES('"
            + product.getName()
            + "', '"
            + product.getSku()
            + "', '"
            + product.getPrice()
            + "', '"
            + product.getDescription()
            + "', '"
            + product.getStock()
            + "', '"
            + product.getCreatedTime()
            + "')");

    if (productID != 0) {
      //Update the productid of the product before returning
      product.setId(productID);
    } else{
      // Return null if product has not been inserted into database
      return null;
    }

    // Return product
    return product;
  }



  // Initialising, instantiating and declaring a product
  public static Product setProduct(ResultSet rs) {
    try {
      Product product = new Product(rs.getInt("p_id"),
              rs.getString("product_name"),
              rs.getString("sku"),
              rs.getFloat("price"),
              rs.getString("description"),
              rs.getInt("stock"));

      return product;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
