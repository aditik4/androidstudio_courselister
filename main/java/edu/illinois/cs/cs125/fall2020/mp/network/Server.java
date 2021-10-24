package edu.illinois.cs.cs125.fall2020.mp.network;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Development course API server.
 *
 * <p>Normally you would run this server on another machine, which the client would connect to over
 * the internet. For the sake of development, we're running the server right alongside the app on
 * the same device. However, all communication between the course API client and course API server
 * is still done using the HTTP protocol. Meaning that eventually it would be straightforward to
 * move this server to another machine where it could provide data for all course API clients.
 *
 * <p>You will need to add functionality to the server for MP1 and MP2.
 */
public final class Server extends Dispatcher {
  @SuppressWarnings({"unused", "RedundantSuppression"})
  private static final String TAG = Server.class.getSimpleName();

  private final Map<String, String> summaries = new HashMap<>();

  private MockResponse getSummary(@NonNull final String path) {
    String[] parts = path.split("/");
    if (parts.length != 2) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    String summary = summaries.get(parts[0] + "_" + parts[1]);
    if (summary == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(summary);
  }

  private String theString;

  private MockResponse testPost(@NonNull final RecordedRequest request) {
    if (request.getMethod().equals("GET")) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(theString);
    } else if (request.getMethod().equals("POST")) {
      theString = request.getBody().readUtf8();
      return new MockResponse()
          .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
          .setHeader("Location", "/test/");
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
  }

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private final Map<Summary, String> courses = new HashMap<>();

  private MockResponse getCourse(@NonNull final String path) {
    String[] parts = path.split("/");
    final int size = 4;
    if (parts.length != size) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    Summary mySum = new Summary(parts[0], parts[1], parts[2], parts[3], "");
    String course = courses.get(mySum);
    if (course == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(course);
  }

  private final Map<Summary, Map<String, Rating>> ratings = new HashMap<>();

  private MockResponse getRating(final String path) {

    ObjectMapper objectMapper = new ObjectMapper();
    String[] parts = path.split("/");
    final int size = 4;
    int clidex = parts[3].indexOf("?client=");
    if (parts.length != size || clidex == -1) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    final int eight = 8;
    final int thirtysix = 36;
    String id = parts[3].substring(clidex + eight);
    System.out.println(id);
    System.out.println(parts[3].substring(0, clidex));
    Summary mySum = new Summary(parts[0], parts[1], parts[2], parts[3].substring(0, clidex), "");
    if (!courses.containsKey(mySum)) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    if (id.length() != thirtysix) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    Map<String, Rating> inner;
    String ser = "";
    Rating rating = new Rating(id, Rating.NOT_RATED);
    if (ratings.containsKey(mySum)) {
      inner = ratings.get(mySum);
      if (inner.get(id) != null) {
        rating = inner.get(id);
      }
    }
    try {
      ser = objectMapper.writeValueAsString(rating);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(ser);
  }

  private MockResponse postRating(final RecordedRequest request) {
    System.out.println("1");
    ObjectMapper objectMapper = new ObjectMapper();
    String path = request.getPath();
    System.out.println(path);
    path = path.replaceFirst("/rating/", "");
    String[] parts = path.split("/");
    final int size = 4;
    int clidex = parts[3].indexOf("?client=");
    if (parts.length != size || clidex == -1) {
      System.out.println("dead");
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    System.out.println("2");
    final int eight = 8;
    final int thirtysix = 36;
    String id = parts[3].substring(clidex + eight);
    System.out.println(id);
    Summary mySum = new Summary(parts[0], parts[1], parts[2], parts[3].substring(0, clidex), "");
    if (!courses.containsKey(mySum)) {
      System.out.println("not contains");
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    // System.out.println("3");
    // if (id.length() != thirtysix) {
    //  return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    // }
    theString = request.getBody().readUtf8();
    System.out.println(theString);
    Rating rating;
    try {
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      rating = objectMapper.readValue(theString, Rating.class);
      // System.out.println(rating.getRating());
      // if (!(rating.getId().equals(id))) {
      // System.out.println("rating id dead");
      // return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
      // }
      System.out.println("4");
      Map<String, Rating> inner = ratings.getOrDefault(mySum, new HashMap<>());
      System.out.println(id);
      inner.put(id, rating);
      System.out.println(inner.get(rating));
      ratings.put(mySum, inner);
      return new MockResponse()
          .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
          .setHeader("Location", "/rating/" + path);
      // System.out.println(inner.get(rating).getRating());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    // return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(theString);
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
  }

  @NonNull
  @Override
  public MockResponse dispatch(@NonNull final RecordedRequest request) {
    try {
      String path = request.getPath();
      if (path == null || request.getMethod() == null) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
      } else if (path.equals("/") && request.getMethod().equalsIgnoreCase("HEAD")) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK);
      } else if (path.startsWith("/summary/")) {
        return getSummary(path.replaceFirst("/summary/", ""));
      } else if (path.equals("/test/")) {
        return testPost(request);
      } else if (path.startsWith("/course/")) {
        return getCourse(path.replaceFirst("/course/", ""));
      } else if (path.startsWith("/rating/")) {
        if (request.getMethod().equals("GET")) {
          return getRating(path.replaceFirst("/rating/", ""));
        }
        if (request.getMethod().equals("POST")) {
          return postRating(request);
        }
      }
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    } catch (Exception e) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
  }

  private static boolean started = false;

  /**
   * Start the server if has not already been started.
   *
   * <p>We start the server in a new thread so that it operates separately from and does not
   * interfere with the rest of the app.
   */
  public static void start() {
    if (!started) {
      new Thread(Server::new).start();
      started = true;
    }
  }

  private final ObjectMapper mapper = new ObjectMapper();

  private Server() {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    loadSummary("2020", "fall");
    loadCourses("2020", "fall");

    try {
      MockWebServer server = new MockWebServer();
      server.setDispatcher(this);
      server.start(CourseableApplication.SERVER_PORT);

      String baseUrl = server.url("").toString();
      if (!CourseableApplication.SERVER_URL.equals(baseUrl)) {
        throw new IllegalStateException("Bad server URL: " + baseUrl);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  @SuppressWarnings("SameParameterValue")
  private void loadSummary(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + "_summary.json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    summaries.put(year + "_" + semester, json);
  }

  @SuppressWarnings("SameParameterValue")
  private void loadCourses(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + ".json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    try {
      JsonNode nodes = mapper.readTree(json);
      for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {
        JsonNode node = it.next();
        Summary course = mapper.readValue(node.toString(), Summary.class);
        courses.put(course, node.toPrettyString());
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
