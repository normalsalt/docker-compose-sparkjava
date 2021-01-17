package sparkjava;

import static spark.Spark.options;
import static spark.Spark.before;

import static spark.Spark.staticFiles;
import static spark.Spark.post;
import javax.servlet.MultipartConfigElement;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
      // this may or may not be necessary in your particular application
      response.type("application/json");
    });
  }

  public static void main(String[] args) {
    // root is 'src/main/resources', so put files in 'src/main/resources/public'
    staticFiles.location("/public");
    // http://sparkjava.com/documentation#examples-and-faq
    post("/upload", (request, response) -> {
      response.type("text/plain");
      request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
      String text = "";
      try(InputStream is = request.raw().getPart("uploaded_file").getInputStream()){
        text = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
      }
      return text;
    });

    get("/hello", (req, res) -> {
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
