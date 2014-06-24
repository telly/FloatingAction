package com.telly.floatingaction;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageButton;

import static android.view.View.OnClickListener;
import static android.widget.AbsListView.OnScrollListener;
import static com.telly.floatingaction.FloatingAction.Utils.checkNotNull;
import static com.telly.floatingaction.FloatingAction.Utils.checkResId;

/**
 * An action stolen from ActionBar which happens to float
 */
public class FloatingAction {
  private final int mMenuItemId;
  private Activity mActivity;
  private ViewGroup mViewGroup;
  private ImageButton mView;
  private Menu mMenu;
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

    mViewGroup = (ViewGroup) mActivity.findViewById(builder.mTargetParentId);
    checkNotNull(mViewGroup, "No parent found with id " + builder.mTargetParentId);

    final View parent = mActivity.getLayoutInflater().inflate(R.layout.fa_action_layout, mViewGroup, true);
    mView = (ImageButton) parent.findViewById(R.id.fa_action_view);

    mMenu = builder.mMenu;
    mMenuItemId = builder.mMenuItemId;

    final MenuItem item = mMenu.findItem(mMenuItemId);
    checkNotNull(item, "No menu item found with id " + mMenuItemId);
    // hide yo kids, hide yo wife
    item.setVisible(false);

    // Setup drawable
    Drawable icon = item.getIcon();
    checkNotNull(icon, "Menu item must provide a drawable");
    icon = icon.mutate();
    icon.setColorFilter(builder.mIconColor, PorterDuff.Mode.MULTIPLY);
    mView.setImageDrawable(icon);
    mView.setOnClickListener(mDelegate);
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
      mAbsListView.setOnScrollListener(mDelegate);
    }
  }

  public void onDestroy() {
    listenTo(null);
    mView.setOnClickListener(null);
    mViewGroup.removeView(mView);
    mViewGroup = null;
    mView = null;
    mMenu = null;
    mActivity = null;
  }

  private void onDirectionChanged(boolean goingDown) {
    if (mHide != goingDown) {
      mHide = goingDown;
      int marginBottom = 0;
      final ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
      if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
        marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
      }
      final int translationY = mHide ? mView.getHeight() + marginBottom : 0;
      mView.animate().setInterpolator(mInterpolator).setDuration(mDuration).translationY(translationY);
    }
  }

  public void hide() {

  }

  public void show() {

  }

  class Delegate implements OnScrollListener, OnClickListener {
    public static final int DIRECTION_CHANGE_THRESHOLD = 1;
    public int mPrevPosition;
    public int mPrevTop;

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
      if (changed) {
        onDirectionChanged(goingDown);
      }
      mPrevPosition = firstVisibleItem;
      mPrevTop = firstViewTop;
    }

    @Override
    public void onClick(View v) {
      mMenu.performIdentifierAction(mMenuItemId, 0);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
      //No-op
    }
  }

  public static class Builder {
    private Activity mActivity;
    private Menu mMenu;
    private int mMenuItemId;
    private int mTargetParentId = android.R.id.content;
    private AbsListView mAbsListView;
    private int mIconColor = 0xff139eff;
    private TimeInterpolator mInterpolator;
    private long mDuration = 200;

    private Builder(Activity activity) {
      checkNotNull(activity, "Invalid Activity provided.");
      mActivity = activity;
    }

    public Builder menu(Menu menu) {
      checkNotNull(menu, "No Menu provided, make sure to provide one.");
      mMenu = menu;
      return this;
    }

    public Builder entree(int menuItemId) {
      checkResId(menuItemId, "Invalid menu item id.");
      mMenuItemId = menuItemId;
      return this;
    }

    public Builder in(int targetParentId) {
      checkResId(targetParentId, "Invalid parent id.");
      mTargetParentId = targetParentId;
      return this;
    }

    public Builder listenTo(int scrollableId) {
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

    public Builder colorResId(int colorResId) {
      checkResId(colorResId, "Invalid color resource provided.");
      final int colorFromRes = mActivity.getResources().getColor(colorResId);
      return color(colorFromRes);
    }

    public void animInterpolator(TimeInterpolator interpolator) {
      mInterpolator = interpolator;
    }

    public void animDuration(long duration) {
      if (duration < 0) {
        throw new IllegalArgumentException("Animation cannot have negative duration: " + duration);
      }
      mDuration = duration;
    }

    public FloatingAction order() {

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
