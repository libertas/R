package org.roeg.r;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private ArrayAdapter<String> aa;
    private ArrayList<String> items;
    private SQLiteDatabase db;
    private EditText text;

    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("test.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS text (_id INTEGER PRIMARY KEY AUTOINCREMENT, item VARCHAR)"); 
        
        ListView list = (ListView) findViewById(R.id.listView1);
        text = (EditText) findViewById(R.id.editText1);
        items = new ArrayList<String>();
        
        aa = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1,
        		items);
        list.setAdapter(aa);
        list.setOnItemClickListener(new OnItemClickListener()
        {
          @Override
          public void onItemClick(AdapterView arg0, View arg1, int arg2,long arg3)
          {
              String i = (String)items.get(arg2);
              aa.remove(i);
              db.execSQL("DELETE FROM text WHERE item IS \"%s\"".replace("%s", i));
              aa.notifyDataSetChanged();
          }
      });
        
        list.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View view, final int location, long arg3) {
				String s = ((String) items.get(location));
				aa.remove(s);
				text.setText(s);
				db.execSQL("DELETE FROM text WHERE item IS \"%s\"".replace("%s", s));
				aa.notifyDataSetChanged();
				return true;
			}
		});
        
        Cursor c = db.rawQuery("SELECT * FROM text",null);
        String i;
        while (c.moveToNext()){
        	i = c.getString(c.getColumnIndex("item"));
        	items.add(0,i);
        }
        aa.notifyDataSetChanged();
        text.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN)
					if((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) ||
							(keyCode == KeyEvent.KEYCODE_ENTER)){
						String i = text.getText().toString();
						if (items.contains(i)) {
							db.execSQL("DELETE FROM text WHERE item IS\"%s\"".replace("%s", i));
							aa.remove(i);
						}
						items.add(0,i);
						aa.notifyDataSetChanged();;
						db.execSQL("INSERT INTO text VALUES (NULL, ?)",
								new Object[]{i});
						text.setText("");
						return true;
					}
				return false;
			}
		});
        
    }
    
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
