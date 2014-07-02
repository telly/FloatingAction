package com.telly.floatingaction.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.telly.floatingaction.FloatingAction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
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

    mFloatingAction = FloatingAction.from(this)
          .listenTo(mListView)
          .icon(R.drawable.ic_action_about)
          .listener(this)
          .build();
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

}
