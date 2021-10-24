package edu.illinois.cs.cs125.fall2020.mp.models;

/** provides course info. */
public class Course extends Summary {
  private String description;
  /** Constructor for Course to run JSON. */
  public Course() {}
  /**
   * Takes in a description.
   *
   * @param setDesc the description to set
   */
  @SuppressWarnings("checkstyle:RegexpSingleline")
  public Course(final String setDesc) {
    description = setDesc;
  }
  /**
   * Returns a description of course.
   *
   * @return the description
   */
  public final String getDescription() {
    return description;
  }
}
