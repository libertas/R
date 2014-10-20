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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends Activity {

    private ArrayAdapter<String> aa;
    private ArrayList<String> items;
    private SQLiteDatabase db;

    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("test.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS text (_id INTEGER PRIMARY KEY AUTOINCREMENT, item VARCHAR)"); 
        
        ListView list = (ListView) findViewById(R.id.listView1);
        final EditText text = (EditText) findViewById(R.id.editText1);
        items = new ArrayList<String>();
        
        aa = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1,
        		items);
        list.setAdapter(aa);
        
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
						items.add(0,i);
						db.execSQL("INSERT INTO text VALUES (NULL, ?)",
								new Object[]{i});
						aa.notifyDataSetChanged();;
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