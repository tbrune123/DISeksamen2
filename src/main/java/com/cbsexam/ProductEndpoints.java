package com.cbsexam;

import cache.ProductCache;
import com.google.gson.Gson;
import controllers.ProductController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Product;
import utils.Encryption;

@Path("product")
public class ProductEndpoints {

  private static ProductCache productCache = new ProductCache();

  public static boolean forceUpdate=true;

  /**
   * @param idProduct
   * @return Responses
   *
   */


  // This method first checks for a product in the cache, if there is no product or the product needs to be updated, it will
  //collect from the database instead. The product gets encrypted with json and send to the user. If things fail
  //it will give error 404 and some text ;=)
  @GET
  @Path("/{idProduct}")
  public Response getProduct(@PathParam("idProduct") int idProduct) {

    // Call our controller-layer in order to get the order from the DB
    Product product = productCache.getProduct(forceUpdate, idProduct);

    // TODO: Add Encryption to JSON FIX
    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(product);
    //added encryption
    json= Encryption.encryptDecryptXOR(json);


    // return data to the user
    if (product != null){
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
    } else {
      return Response.status(404).entity("Could not find the guy running around with the product").build();
    }

  }

  /** @return Responses */

  // This method first checks for a products in the cache, if there is no products or the products needs to be updated, it will
  //collect from the database instead. The product gets encrypted with json and send to the user. If things fail
  //it will give error 404 and some text ;=)
  @GET
  @Path("/")
  public Response getProducts() {

    // Call our controller-layer in order to get the order from the DB
    ArrayList<Product> products = productCache.getProducts(forceUpdate);

    // TODO: Add Encryption to JSON FIX
    // We convert the java object to json with GSON library imported in Maven
    String json = new Gson().toJson(products);
    //added encryption
    json= Encryption.encryptDecryptXOR(json);

    if (products != null){
      // just created a new cache, so setting forceUpdate to false
      this.forceUpdate = false;
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(404).entity("Could not find the guy running around with the products").build();
    }



  }


  // This method creates a product based on the information provided from the user making it
  // Using the controller to save the new product in the database. The product are then converted to json and send
  // back to the user. If anything goes wrong it will return 404 and a text ;=)
  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createProduct(String body) {

    // Read the json from body and transfer it to a product class
    Product newProduct = new Gson().fromJson(body, Product.class);

    // Use the controller to add the user
    Product createdProduct = ProductController.createProduct(newProduct);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createdProduct);

    // Return the data to the user
    if (createdProduct != null) {
      // Return a response with status 200 and JSON as type
      // just created a new cache, so setting forceUpdate to false
      this.forceUpdate = true;

      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not find the manager responsible for making new products").build();
    }
  }
}
