package edu.illinois.cs.cs125.fall2020.mp.models;

/** Rating class for storing client ratings for course. */
public class Rating {
  /** Rating indicating that the course has not been rated yet. */
  public static final double NOT_RATED = -1.0;

  private double rating;
  private String id;

  /** Default constructor for rating object. */
  public Rating() {}

  /**
   * Rating constructor.
   *
   * @param setId the UUID to set for this rating
   * @param setRating the rating value given
   */
  public Rating(final String setId, final double setRating) {
    rating = setRating;
    id = setId;
  }

  /**
   * Returns the UUID of the rating.
   *
   * @return the uuid of the rating
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the rating value.
   *
   * @return the rating
   */
  public double getRating() {
    return rating;
  }
}
