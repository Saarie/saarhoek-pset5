package com.example.saar.saarhoek_pset5;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // variables
    private String list;
    private String listname;
    private SimpleCursorAdapter adapter;
    private ListView mainlistview;
    private TodoManager db;
    private Cursor cursor;

    // from and to
    final String[] from = new String[] {TodoManager.CONTENT};
    final int[] to = new int[] {R.id.listItem};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // using custom font
        TextView title = (TextView)findViewById(R.id.title);
        Typeface custom = Typeface.createFromAsset(getAssets(),  "fonts/custom.ttf");
        title.setTypeface(custom);
        // setting list
        db = TodoManager.getInstance(this);
        ListSetter();
    }

    private void ListSetter() {
        // get cursor and adapter
        cursor = db.fetchList();
        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);

        // find xml view
        mainlistview = (ListView) findViewById(R.id.listview);

        // and set
        mainlistview.setAdapter(adapter);

        // when clicked, we want to see the content
        mainlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor row = db.getListRow(l);

                if (row.moveToFirst()) {
                    listname = row.getString(1);
                }
                row.close();

                Intent viewContent= new Intent(MainActivity.this, ItemView.class);
                viewContent.putExtra("item", l);
                viewContent.putExtra("title", listname);
                startActivity(viewContent);
            }
        });

        // when we longclick an item, we want it deleted
        // in both our UI and our SQL database
        mainlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // delete in database
                db.deleteAll(l);
                db.deleteList(l);

                // update cursor
                cursor.requery();

                // delete in UI
                adapter.notifyDataSetChanged();

                // let the user know and finish up
                Toast deleted = Toast.makeText(MainActivity.this, "Deletion complete", Toast.LENGTH_SHORT);
                deleted.show();
                return true;
            }
        });
    }

    public void addList(View view){
        // transfer user input to string
        EditText newlist = (EditText) findViewById(R.id.field);
        list = newlist.getText().toString();
        newlist.setText("");

        // make sure user isn't idiot
        if(list.length() == 0){
            Toast error = Toast.makeText(MainActivity.this, "Insert title, please", Toast.LENGTH_SHORT);
            error.show();
        }

        // if not, create new list
        else{
            db.createList(list);
            cursor.requery();
            adapter.notifyDataSetChanged();

            Toast succes = Toast.makeText(MainActivity.this, "Mission succesful", Toast.LENGTH_SHORT);
            succes.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
