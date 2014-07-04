package com.telly.floatingaction.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.telly.floatingaction.FloatingAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {
  private ListView mListView;
  private FloatingAction mFloatingAction;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mListView = (ListView) findViewById(android.R.id.list);

    final ListAdapter adapter = new LeAdapter(this, generateMockItems());
    mListView.setAdapter(adapter);

    mFloatingAction = FloatingAction.from(this)
          .listenTo(mListView)
          .colorResId(R.color.branding)
          .icon(R.drawable.ic_action_about)
          .listener(this)
          .build();

    getWindow().setBackgroundDrawable(null);
  }

  @Override
  public void onClick(View v) {
    Toast.makeText(this, "Floating Action Clicked", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    mFloatingAction.onDestroy();
  }


  static class LeAdapter extends BaseAdapter {
    private final Activity mActivity;
    private final LayoutInflater mInflater;
    private final List<Item> mItems;

    public LeAdapter(Activity activity, List<Item> items) {
      mActivity = activity;
      mInflater = activity.getLayoutInflater();
      mItems = items;
    }

    @Override
    public int getCount() {
      return mItems.size();
    }

    @Override
    public Item getItem(int position) {
      return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = mInflater.inflate(R.layout.list_item, parent, false);
      }
      final Item item = getItem(position);
      ViewHolder.from(convertView).bind(item);
      return convertView;
    }

    static class ViewHolder {
      final TextView mTxt;

      ViewHolder(View view) {
        mTxt = (TextView)view;
      }

      void bind(Item item) {
        mTxt.setBackgroundResource(item.mBg);
        mTxt.setText(item.mTxt);
      }

      static ViewHolder from(View view) {
        final Object tag = view.getTag();
        if (tag instanceof ViewHolder) {
          return (ViewHolder) tag;
        }
        return new ViewHolder(view);
      }
    }
  }

  static List<Item> generateMockItems() {
    final int n = 100;
    final List<Item> items = new ArrayList<Item>(n);
    int bg = 0;
    for (int i = 0; i < n; i++) {
      bg = nextBg(bg, 0);
      items.add(new Item(bg, "Item " + i));
    }
    return items;
  }

  static final Random sRandom = new Random(System.currentTimeMillis());
  static final int[] sBgs = {
      R.color.turquoise,
      R.color.green_sea,
      R.color.emerland,
      R.color.nephritis,
      R.color.peter_river,
      R.color.belize_hole,
      R.color.amethyst,
      R.color.wisteria,
      R.color.wet_asphalt,
      R.color.midnight_blue,
      R.color.sun_flower,
      R.color.orange,
      R.color.carrot,
      R.color.pumpkin,
      R.color.alizarin,
      R.color.pomegranate
  };
  static int nextBg(int current, int control) {
    final int i = sRandom.nextInt(sBgs.length);
    final int candidate = sBgs[i];
    if (current == candidate && control < 3) {
      return nextBg(current, ++control);
    }
    return candidate;
  }

  static class Item {
    final int mBg;
    final String mTxt;

    Item(int mBg, String mTxt) {
      this.mBg = mBg;
      this.mTxt = mTxt;
    }
  }
}
