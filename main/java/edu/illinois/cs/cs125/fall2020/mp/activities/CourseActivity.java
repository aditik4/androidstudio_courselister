package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs125.fall2020.mp.R;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;

/** Course activity class. */
public class CourseActivity extends AppCompatActivity implements Client.CourseClientCallbacks {
  private static final String TAG = MainActivity.class.getSimpleName();

  // courseResponse has to update the textview and display the title and the description

  /**
   * Provides courseResponse.
   *
   * @param summary the summary to use
   * @param course the course to use
   */
  @Override
  public void courseResponse(final Summary summary, final Course course) {
    binding.textView.setText(summary.getFull());
    binding.titleView.setText(course.getDescription());
  }

  private Summary sum = new Summary();
  private TextView tv;
  private RatingBar bar;

  /**
   * Handles rating bar.
   *
   * @param rating the rating
   */
  @Override
  public void yourRating(final Summary summary, final Rating rating) {
    binding.rating.setRating((float) rating.getRating());
  }

  private ActivityCourseBinding binding;
  /**
   * handles operations upon creation.
   *
   * @param savedInstanceState the state
   */
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    Log.i(TAG, "course activity started");
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    ObjectMapper mapper = new ObjectMapper();
    String json = intent.getStringExtra("COURSE");
    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);
    tv = findViewById(R.id.textView);
    bar = findViewById(R.id.rating);
    try {
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      sum = mapper.readValue(json, Summary.class);
      // Rating rating = mapper.readValue(json, Rating.class);
      CourseableApplication application = (CourseableApplication) getApplication();
      application.getCourseClient().getCourse(sum, this);
      application.getCourseClient().getRating(sum, application.getClientId(), this);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    bar.setOnRatingBarChangeListener(
        new RatingBar.OnRatingBarChangeListener() {
          @Override
          public void onRatingChanged(final RatingBar ratingBar, final float v, final boolean b) {
            // tv.setText(getString(R.string.rating_string, rating));
            CourseableApplication application = (CourseableApplication) getApplication();
            Rating rating = new Rating(application.getClientId(), v);
            application.getCourseClient().postRating(sum, rating, CourseActivity.this);
            bar.setRating((float) rating.getRating());
          }
        });
  }
}
