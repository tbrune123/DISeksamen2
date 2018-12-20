package cache;

import controllers.ProductController;
import java.util.ArrayList;
import model.Product;
import utils.Config;

public class ProductCache {

  // List of products
  private static ArrayList<Product> products;

  // Time cache should live
  private long lifeTimeOfCache;

  // Sets a stamp when the cache has been created
  private static long created;

  // If there is no update, it will get orders from arrayList
  public ProductCache() {
    this.lifeTimeOfCache = Config.getProductTtl();
  }

  public ArrayList<Product> getProducts(Boolean forceUpdate) {

    // Clear cache if we want to
    // The method is checking if the time-stamp is out of date
    // We will check for update if the list is empty
    if (forceUpdate
        || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis()/100L))
        || this.products==null) {

      // Getting the products from the controller, because we want to update
      ArrayList<Product> products = ProductController.getProducts();

      System.out.println("virker");

      // Setting the products for the instance and set timestamp
      this.products = products;
      this.created = System.currentTimeMillis();
    }

    // Return the products
    return this.products;
  }

  public Product getProduct(boolean forceUpdate, int productID) {
    Product product = new Product();

    // ForceUpdate if the time og cachce is out or userList is empty
    if (forceUpdate
            || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis())) || this.products==null) {

      // If cache needs update: Use the ProductController to get data from database
      product = ProductController.getProduct(productID);

      return product;
    } else {
      // If the cache is up to date, use  arraylist untill the correct product_ID is found
      for (Product p : products){
        if (productID==p.getId())
          product = p;
        return product;
      }
    }
    return null;
  }
}
