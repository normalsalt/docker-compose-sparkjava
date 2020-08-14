package sparkjava;

import static spark.Spark.options;
import static spark.Spark.before;

import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.internalServerError;

public class Main {
  // Enables CORS on requests. This method is an initialization method and should be called once.
  private static void enableCORS(final String origin, final String methods, final String headers) {
    options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }
      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }
      return "OK";
    });
    before((request, response) -> {
      response.header("Access-Control-Allow-Origin", origin);
      response.header("Access-Control-Request-Method", methods);
      response.header("Access-Control-Allow-Headers", headers);
      // Note: this may or may not be necessary in your particular application
      response.type("application/json");
    });
  }

  public static void main(String[] args) {
    get("/", (req, res) -> {
      res.type("application/json");
      return "{\"message\":\"Welcome to Spark Framework API!\"}";
    });
    notFound((req, res) -> {
      res.type("application/json");
      return "{\"message\":\"404 Not Found\"}";
    });
    internalServerError((req, res) -> {
      res.type("application/json");
      return "{\"message\":\"500 Internal Server Error\"}";
    });
    enableCORS("*", "GET", "*");
  }
}
