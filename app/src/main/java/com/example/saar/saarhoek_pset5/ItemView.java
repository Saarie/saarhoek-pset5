package com.example.saar.saarhoek_pset5;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Saar on 2-12-2016.
 */

public class ItemView extends AppCompatActivity {
    // store amazingly designed checkmark images
    int[] imageIDs = {
            R.drawable.uncheck,
            R.drawable.check,
    };

    private Bundle extras;
    private String item;
    private SimpleCursorAdapter itemadapter;
    private ListView itemlistview;
    private TodoManager db;
    private Cursor itemcursor;
    private Long parent_id;
    String viewtitle;

    // from and to (image and task)
    final String[] from = new String[] {TodoManager.CHECKER, TodoManager.TASK};
    final int[] to = new int[] {R.id.checker, R.id.todoItem};

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemview);

        // get extras and such
        extras = getIntent().getExtras();
        parent_id = extras.getLong("item");
        viewtitle = extras.getString("title");
        db = TodoManager.getInstance(this);

        // use custom font and set title
        TextView title = (TextView)findViewById(R.id.viewTitle);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/custom.ttf");
        title.setTypeface(custom);
        title.setText(viewtitle);

        // and there we go again
        ListSetter(parent_id);
    }

    private void ListSetter(long parent_id){
        // get cursor and adapter
        itemcursor = db.fetchItems(parent_id);
        itemadapter = new SimpleCursorAdapter(this, R.layout.todo_item, itemcursor, from, to, 0);

        // find xml view
        itemlistview = (ListView) findViewById(R.id.viewListview);

        // and set
        itemlistview.setAdapter(itemadapter);

        // when clicked, we want to check
        itemlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor row = db.getItemRow(l);

                if (row.moveToFirst()) {
                    // get only image and task
                    int image = row.getInt(1);
                    String task = row.getString(2);

                    // change the checkmark
                    if (image == imageIDs[0]) {
                        image = imageIDs[1];
                    }
                    else {
                        image = imageIDs[0];
                    }
                    // Update the row with the new image
                    db.update(l, image, task);
                }
                row.close();

                // change UI
                itemcursor.requery();
                itemadapter.notifyDataSetChanged();
            }
        });

        // when we longclick an item, we want it deleted
        // in both our UI and our SQL database
        itemlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // delete in database
                db.deleteItem(l);

                // update cursor
                itemcursor.requery();

                // delete in UI
                itemadapter.notifyDataSetChanged();

                // let the user know and finish up
                Toast deleted = Toast.makeText(ItemView.this, "Deletion complete", Toast.LENGTH_SHORT);
                deleted.show();
                return true;
            }
        });
    }

    public void addItem(View view){
        // transfer user input to string
        EditText newitem = (EditText) findViewById(R.id.viewField);
        item = newitem.getText().toString();
        newitem.setText("");

        // make sure user isn't idiot
        if(item.length() == 0){
            Toast error = Toast.makeText(ItemView.this, "Insert task, please", Toast.LENGTH_SHORT);
            error.show();
        }

        // if not, create new task
        else {
            db.createItem(imageIDs[0], item, parent_id);
            itemcursor.requery();
            itemadapter.notifyDataSetChanged();

            Toast succes = Toast.makeText(ItemView.this, "Mission succesful", Toast.LENGTH_SHORT);
            succes.show();
        }
    }
}
