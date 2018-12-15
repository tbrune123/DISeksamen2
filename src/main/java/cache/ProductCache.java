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

  // Sets when the cache has been created
  private static long created;

  public ProductCache() {
    this.lifeTimeOfCache = Config.getProductTtl();
  }

  public ArrayList<Product> getProducts(Boolean forceUpdate) {

    // If we whis to clear cache, we can set force update.
    // Otherwise we look at the age of the cache and figure out if we should update.
    // If the list is empty we also check for new products
    if (forceUpdate
        || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis() ))
        || this.products==null) {

      // Get products from controller, since we wish to update.
      ArrayList<Product> products = ProductController.getProducts();

      // Set products for the instance and set created timestamp
      this.products = products;
      this.created = System.currentTimeMillis();
    }

    // Return the documents
    return this.products;
  }

  public Product getProduct(boolean forceUpdate, int productID) {
    Product product = new Product();

    if (forceUpdate
            || ((this.created + this.lifeTimeOfCache) <= (System.currentTimeMillis())) || this.products==null) {

      // Get product from controller based on id
      product = ProductController.getProduct(productID);

      return product;
    } else {
      // If the cache is alright, go through arraylist till right product_ID is found
      for (Product p : products){
        if (productID==p.getId())
          product = p;
        return product;
      }
    }
    return null;
  }
}
