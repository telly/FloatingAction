package com.telly.floatingaction;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageButton;

import static android.view.View.OnClickListener;
import static android.view.ViewTreeObserver.OnPreDrawListener;
import static android.widget.AbsListView.OnScrollListener;
import static com.telly.floatingaction.FloatingAction.Utils.checkNotNull;
import static com.telly.floatingaction.FloatingAction.Utils.checkResId;

/**
 * An action stolen from ActionBar which happens to float
 */
public class FloatingAction {
  private Activity mActivity;
  private ViewGroup mViewGroup;
  private ImageButton mView;
  private AbsListView mAbsListView;
  private TimeInterpolator mInterpolator;
  private boolean mHide;

  private Delegate mDelegate = new Delegate();
  private long mDuration;

  public static Builder from(Activity activity) {
    return new Builder(activity);
  }

  private FloatingAction(Builder builder) {
    mActivity = builder.mActivity;

    mInterpolator = builder.mInterpolator;
    mDuration = builder.mDuration;

    if (builder.mParent != null) {
      mViewGroup = builder.mParent;
    } else {
      mViewGroup = (ViewGroup) mActivity.findViewById(builder.mTargetParentId);
      checkNotNull(mViewGroup, "No parent found with id " + builder.mTargetParentId);
    }

    final View parent = mActivity.getLayoutInflater().inflate(R.layout.fa_action_layout, mViewGroup, true);
    mView = (ImageButton) parent.findViewById(R.id.fa_action_view);

    // Setup drawable
    Drawable icon = builder.mIcon;
    checkNotNull(icon, "Menu item must provide a drawable");
    icon = icon.mutate();
    icon.setColorFilter(builder.mIconColor, PorterDuff.Mode.MULTIPLY);
    mView.setImageDrawable(icon);
    mView.setOnClickListener(builder.mClickListener);
    // Start listening if any
    listenTo(builder.mAbsListView);
  }

  public void listenTo(AbsListView absListView) {
    final AbsListView currentAbsListView = mAbsListView;
    if (currentAbsListView instanceof ChattyListView) {
      ((ChattyListView) currentAbsListView).removeOnScrollListener(mDelegate);
    }
    mAbsListView = absListView;
    if (mAbsListView != null) {
      mDelegate.reset();
      mAbsListView.setOnScrollListener(mDelegate);
    }
  }

  public void onDestroy() {
    listenTo(null);
    mView.setOnClickListener(null);
    mViewGroup.removeView(mView);
    mViewGroup = null;
    mView = null;
    mActivity = null;
  }

  private void onDirectionChanged(boolean goingDown) {
    leHide(goingDown, true);
  }

  public void hide() {
    hide(true);
  }

  public void hide(boolean animate) {
    leHide(true, animate);
  }

  public void show() {
    show(true);
  }

  public void show(boolean animate) {
    leHide(false, animate);
  }

  private void leHide(final boolean hide, final  boolean animated) {
    leHide(hide, animated, false);
  }

  private void leHide(final boolean hide, final  boolean animated, final boolean deferred) {
    if (mHide != hide || deferred) {
      mHide = hide;
      final int height = mView.getHeight();
      if (height == 0 && !deferred) {
        // Dang it, haven't been drawn before, defer! defer!
        final ViewTreeObserver vto = mView.getViewTreeObserver();
        if (vto.isAlive()) {
          vto.addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
              // Sometimes is not the same we used to know
              final ViewTreeObserver currentVto = mView.getViewTreeObserver();
              if (currentVto.isAlive()) {
                currentVto.removeOnPreDrawListener(this);
              }
              leHide(hide, animated, true);
              return true;
            }
          });
          return;
        }
      }
      int marginBottom = 0;
      final ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
      if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
        marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
      }
      final int translationY = mHide ? height + marginBottom : 0;
      if (animated) {
        mView.animate()
            .setInterpolator(mInterpolator)
            .setDuration(mDuration)
            .translationY(translationY);
      } else {
        mView.setTranslationY(translationY);
      }
    }
  }

  class Delegate implements OnScrollListener {
    private static final int DIRECTION_CHANGE_THRESHOLD = 1;
    private int mPrevPosition;
    private int mPrevTop;
    private boolean mUpdated;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
      final View topChild = view.getChildAt(0);
      int firstViewTop = 0;
      if (topChild != null) {
        firstViewTop = topChild.getTop();
      }
      boolean goingDown;
      boolean changed = true;
      if (mPrevPosition == firstVisibleItem) {
        final int topDelta = mPrevTop - firstViewTop;
        goingDown = firstViewTop < mPrevTop;
        changed = Math.abs(topDelta) > DIRECTION_CHANGE_THRESHOLD;
      } else {
        goingDown = firstVisibleItem > mPrevPosition;
      }
      if (changed && mUpdated) {
        onDirectionChanged(goingDown);
      }
      mPrevPosition = firstVisibleItem;
      mPrevTop = firstViewTop;
      mUpdated = true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
      //No-op
    }

    public void reset() {
      mPrevPosition = 0;
      mPrevTop = 0;
      mUpdated = false;
    }
  }

  public static class Builder {
    private Activity mActivity;
    private int mTargetParentId = android.R.id.content;
    private ViewGroup mParent;
    private AbsListView mAbsListView;
    private int mIconColor = 0xff139eff;
    private TimeInterpolator mInterpolator;
    private long mDuration = 200;
    private OnClickListener mClickListener;
    private Drawable mIcon;

    private Builder(@NonNull Activity activity) {
      checkNotNull(activity, "Invalid Activity provided.");
      mActivity = activity;
    }

    public Builder in(@IdRes int targetParentId) {
      checkResId(targetParentId, "Invalid parent id.");
      mTargetParentId = targetParentId;
      return this;
    }

    public Builder in(ViewGroup parent) {
      checkNotNull(parent, "Invalid parent provided.");
      mParent = parent;
      return this;
    }

    public Builder listenTo(@IdRes int scrollableId) {
      checkResId(scrollableId, "Invalid view id.");
      final View view = mActivity.findViewById(scrollableId);
      if (!(view instanceof AbsListView)) {
        throw new IllegalArgumentException("Provided view can't be listened to.");
      }
      listenTo((AbsListView)view);
      return this;
    }

    public Builder listenTo(AbsListView absListView) {
      checkNotNull(absListView, "Invalid AbsListView provided.");
      mAbsListView = absListView;
      return this;
    }

    public Builder color(int color) {
      mIconColor = color;
      return this;
    }

    public Builder colorResId(@ColorRes int colorResId) {
      checkResId(colorResId, "Invalid color resource provided.");
      final int colorFromRes = mActivity.getResources().getColor(colorResId);
      return color(colorFromRes);
    }

    public Builder animInterpolator(TimeInterpolator interpolator) {
      mInterpolator = interpolator;
      return this;
    }

    public Builder animDuration(long duration) {
      if (duration < 0) {
        throw new IllegalArgumentException("Animation cannot have negative duration: " + duration);
      }
      mDuration = duration;
      return this;
    }

    public Builder icon(@DrawableRes int drawableResId) {
      checkResId(drawableResId, "Invalid icon resource provided.");
      final Drawable drawable = mActivity.getResources().getDrawable(drawableResId);
      return icon(drawable);
    }

    public Builder icon(Drawable drawable) {
      checkNotNull(drawable, "Invalid icon drawable provided.");
      mIcon = drawable;
      return this;
    }

    public Builder listener(OnClickListener listener) {
      checkNotNull(listener, "Invalid click listener provided.");
      mClickListener = listener;
      return this;
    }

    public FloatingAction build() {

      if (mInterpolator == null) {
        mInterpolator = new AccelerateDecelerateInterpolator();
      }
      return new FloatingAction(this);
    }
  }

  static class Utils {
    static void checkNotNull(Object object, String msg) {
      if (object == null) {
        throw new NullPointerException(msg);
      }
    }

    static void checkResId(int resId, String msg) {
      if (resId < 0) {
        throw new IllegalArgumentException(msg);
      }
    }
  }
}
