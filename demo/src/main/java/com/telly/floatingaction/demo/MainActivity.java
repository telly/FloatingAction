package com.telly.floatingaction.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.telly.floatingaction.FloatingAction;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
  private ListView mListView;
  private FloatingAction mFloatingAction;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mListView = (ListView) findViewById(android.R.id.list);

    final int n = 100;
    final List<String> items = new ArrayList<String>(n);

    for (int i = 0; i < n; i++) {
      items.add("Item " + i);
    }

    final ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
    mListView.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);

    mFloatingAction = FloatingAction.from(this)
        .listenTo(android.R.id.list)
        .menu(menu)
        .entree(R.id.action_about)
        .order();
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    final int id = item.getItemId();
    switch (id) {
      case R.id.action_about:
      case R.id.action_settings:
        Toast.makeText(this, "Menu item " + item.getTitle() + " clicked.", Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    mFloatingAction.onDestroy();
  }
}
