package com.telly.floatingaction;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A list view who likes to be listened by many
 */
public class ChattyListView extends ListView {

  private List<OnScrollListener> mScrollListeners;
  private List<OnTouchListener> mTouchListeners;

  public ChattyListView(Context context) {
    super(context);
    listen();
  }

  public ChattyListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    listen();
  }

  public ChattyListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    listen();
  }

  private void listen() {
    super.setOnScrollListener(mInternalListener);
    super.setOnTouchListener(mInternalListener);
  }

  @Override
  public void setOnScrollListener(OnScrollListener listener) {
    addOnScrollListener(listener);
  }

  @Override
  public void setOnTouchListener(OnTouchListener listener) {
    addOnTouchListener(listener);
  }

  public void addOnTouchListener(OnTouchListener listener) {
    if (listener == null) {
      throw new NullPointerException("Invalid listener provided.");
    }
    if (mTouchListeners == null) {
      mTouchListeners = new ArrayList<OnTouchListener>();
    }

    if (!mTouchListeners.contains(listener)) {
      mTouchListeners.add(listener);
    }
  }

  public void addOnScrollListener(OnScrollListener listener) {
    if (listener == null) {
      throw new NullPointerException("Invalid listener provided.");
    }
    if (mScrollListeners == null) {
      mScrollListeners = new ArrayList<OnScrollListener>();
    }

    if (!mScrollListeners.contains(listener)) {
      mScrollListeners.add(listener);
    }
  }

  public void removeOnTouchListener(OnTouchListener listener) {
    if (mTouchListeners == null) {
      return;
    }
    mTouchListeners.remove(listener);
  }

  public void removeOnScrollListener(OnScrollListener listener) {
    if (mScrollListeners == null) {
      return;
    }
    mScrollListeners.remove(listener);
  }

  private final MultiListener mInternalListener = new MultiListener() {
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
      if (mScrollListeners == null) {
        return;
      }
      for (int i = 0, count = mScrollListeners.size(); i < count; i++) {
        mScrollListeners.get(i).onScrollStateChanged(view, scrollState);
      }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
      if (mScrollListeners == null) {
        return;
      }
      for (int i = 0, count = mScrollListeners.size(); i < count; i++) {
        mScrollListeners.get(i).onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
      }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      if (mTouchListeners == null) {
        return false;
      }
      boolean result = false;
      for (int i = 0, count = mTouchListeners.size(); i < count; i++) {
        if (mTouchListeners.get(i).onTouch(v, event)) {
          result = true;
        }
      }
      return result;
    }
  };

  private interface MultiListener extends OnScrollListener, OnTouchListener {

  }
}
