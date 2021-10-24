package edu.illinois.cs.cs125.fall2020.mp.models;

import androidx.annotation.NonNull;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Model holding the course summary information shown in the course list.
 *
 * <p>You will need to complete this model for MP0.
 */
@SuppressWarnings({"checkstyle:JavadocStyle", "CheckStyle"})
public class Summary implements SortedListAdapter.ViewModel {
  private String year;

  /**
   * Get the year for this Summary.
   *
   * @return the year for this Summary
   */
  public final String getYear() {
    return year;
  }

  private String semester;

  /**
   * Get the semester for this Summary.
   *
   * @return the semester for this Summary
   */
  public final String getSemester() {
    return semester;
  }

  private String department;

  /**
   * Get the department for this Summary.
   *
   * @return the department for this Summary
   */
  public final String getDepartment() {
    return department;
  }

  private String number;

  /**
   * Get the number for this Summary.
   *
   * @return the number for this Summary
   */
  public final String getNumber() {
    return number;
  }

  private String title;

  /**
   * Get the title for this Summary.
   *
   * @return the title for this Summary
   */
  public final String getTitle() {
    return title;
  }

  /**
   * Gets the full title for course.
   *
   * @return the full title
   */
  public final String getFull() {
    return String.format("%s %s: %s", department, number, title);
  }
  /** Create an empty Summary. */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public Summary() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setYear the year for this Summary
   * @param setSemester the semester for this Summary
   * @param setDepartment the department for this Summary
   * @param setNumber the number for this Summary
   * @param setTitle the title for this Summary
   */
  public Summary(
      final String setYear,
      final String setSemester,
      final String setDepartment,
      final String setNumber,
      final String setTitle) {
    year = setYear;
    semester = setSemester;
    department = setDepartment;
    number = setNumber;
    title = setTitle;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Summary)) {
      return false;
    }
    Summary course = (Summary) o;
    return Objects.equals(year, course.year)
        && Objects.equals(semester, course.semester)
        && Objects.equals(department, course.department)
        && Objects.equals(number, course.number);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(year, semester, department, number);
  }

  /** {@inheritDoc} */
  @Override
  public <T> boolean isSameModelAs(@NonNull final T model) {
    return equals(model);
  }

  /** {@inheritDoc} */
  @Override
  public <T> boolean isContentTheSameAs(@NonNull final T model) {
    return equals(model);
  }

  /**
   * Compares two course models in order to order the list properly according to department, number,
   * and title.
   *
   * @return -1 if courseModel1 should be lower on the list 0 if the two courses are equal 1 if
   *     courseModel1 should be higher on the list
   */
  public static final Comparator<Summary> COMPARATOR =
      (courseModel1, courseModel2) -> {
        int num = Integer.compare(courseModel1.number.compareTo(courseModel2.number), 0);
        int dep =
            Integer.compare(
                courseModel1.getDepartment().compareTo(courseModel2.getDepartment()), 0);
        int title = Integer.compare(courseModel1.getTitle().compareTo(courseModel2.getTitle()), 0);
        if (dep != 0) {
          return dep;
        } else if (num == 0) {
          return title;
        }
        return num;
      };
  /**
   * Filters the courses visible by the given string.
   *
   * @param courses the list of courses to examine
   * @param text the text to filter
   * @return a list of Summary courses that contain the string
   */
  public static List<Summary> filter(
      @NonNull final List<Summary> courses, @NonNull final String text) {
    // return courses;
    List<Summary> filtered = new ArrayList<Summary>();
    for (int i = 0; i < courses.size(); i++) {
      String curr =
          String.format(
              "%s %s: %s", courses.get(i).department, courses.get(i).number, courses.get(i).title);
      if (curr.toLowerCase().contains(text.toLowerCase())) {
        filtered.add(courses.get(i));
        // System.out.println(courses.get(i).title);
      }
    }
    return filtered;
  }
}
