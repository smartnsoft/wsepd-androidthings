package com.nilhcem.androidthings.driver.wsepd.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.willy.ratingbar.BaseRatingBar;

/**
 * @author David Fournier
 * @since 2018.05.14
 */
public class InformationLayout
    extends FrameLayout
{

  private String title;

  private float note;

  public InformationLayout(@NonNull Context context)
  {
    super(context, null, 0);
    init();
  }

  public InformationLayout(@NonNull Context context, @Nullable AttributeSet attrs)
  {
    super(context, attrs, 0);
    init();
  }

  public InformationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    init();
  }

  public InformationLayout(@NonNull Context context, String title, float note) {
    super(context, null, 0);
    this.title = title;
    this.note = note;
    init();
  }

  private void init()
  {
    inflate(getContext(), R.layout.dummy_rating_layout, this);
    ((TextView)findViewById(R.id.informationTitle)).setText(title);
    ((BaseRatingBar)findViewById(R.id.informationRatingBar)).setRating(note);
  }

}
