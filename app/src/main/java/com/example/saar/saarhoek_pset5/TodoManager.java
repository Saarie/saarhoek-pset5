package com.example.saar.saarhoek_pset5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saar on 1-12-2016.
 */

public class TodoManager extends SQLiteOpenHelper {
    // private singleton instance
    private static TodoManager sInstance;

    // database properties
    static final String NAME = "ToDoLists.db";
    static final int VERSION = 2;

    // declaring public strings
    public static final String LIST = "LIST";
    public static final String ITEM = "ITEM";

    // list properties
    public static final String CONTENT = "content";

    // item properties
    public static final String _ID = "_id";
    public static final String TASK = "task";
    public static final String CHECKER = "checker";
    public static final String PARENT = "parent";

    // table creation
    private static final String CREATE_LIST = "create table " + LIST + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONTENT + " text not null);";
    private static final String CREATE_TODO = "create table " + ITEM + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CHECKER + " integer not null, " + TASK + " text not null, " + PARENT + " integer default 0);";

    // database needs to be accessible from every activity
    public static synchronized TodoManager getInstance(Context context) {

        // stay within context to avoid leakage
        if (sInstance == null) {
            sInstance = new TodoManager(context.getApplicationContext());
        }
        return sInstance;
    }

    // construct, create, upgrade
    public TodoManager(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LIST);
        db.execSQL(CREATE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LIST);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM);
        onCreate(db);
    }

    // creation functions
    public TodoList createList (String content){
        // store content
        ContentValues values = new ContentValues();
        values.put(CONTENT, content);

        // put content in database
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(LIST, null, values);

        // finally, create list locally
        TodoList list = new TodoList(content);
        return list;
    }

    public TodoItem createItem (int checker, String task, long parent){
        // store content
        ContentValues values = new ContentValues();
        values.put(CHECKER, checker);
        values.put(TASK, task);
        values.put(PARENT, parent);

        // put content in database
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(ITEM, null, values);

        // finally, create item locally
        TodoItem item = new TodoItem(task, checker, parent);
        return item;
    }

    // deletion functions
    public void deleteList (long _id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LIST, _ID + "=" + _id, null);
    }

    public void deleteItem (long _id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ITEM, _ID + "=" + _id, null);
    }

    // if we delete a list, we also want to delete its content
    public void deleteAll (long _id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ITEM, PARENT + "=" + _id, null);
    }

    // closing
    public void close(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen() && db != null){db.close();}
    }

    // updating
    public boolean update(long _id, int checker, String task) {
        // get renewed data
        ContentValues values = new ContentValues();
        values.put(CHECKER, checker);
        values.put(TASK, task);

        // change it in database
        SQLiteDatabase db = this.getWritableDatabase();
        String compare = _ID + "=" + _id;
        return db.update(ITEM, values, compare, null) != 0;
    }

    // navigation functions
    public Cursor getListRow (long _id){
        // workable variables
        String[] data = new String[] {_ID, CONTENT};
        String compare = _ID + "=" + _id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, LIST, data, compare,
                null, null, null, null, null);

        // moving
        if (cursor != null){cursor.moveToFirst();}
        return cursor;
    }

    public Cursor getItemRow (long _id){
        // workable variables
        String[] data = new String[]{_ID, CHECKER, TASK};
        String compare = _ID + "=" + _id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, ITEM, data, compare,
                null, null, null, null, null);

        // moving
        if (cursor != null){cursor.moveToFirst();}
        return cursor;
    }

    public Cursor fetchList () {
        // defining search area
        List<TodoList> oversight = new ArrayList<TodoList>();
        String select = "SELECT * FROM " + LIST;

        // selecting in database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);

        // setting list data
        if (cursor.moveToFirst()) {
            do {
                TodoList list = new TodoList();
                list.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                list.setList(cursor.getString(cursor.getColumnIndex(CONTENT)));
                oversight.add(list);
            } while (cursor.moveToNext());
        }

        // define new cursor
        String[] data = new String[]{_ID, CONTENT};
        Cursor strider = db.query(LIST, data, null, null, null, null, null);

        // return
        if (strider != null) {strider.moveToFirst();}
        return strider;
    }

    public Cursor fetchItems (long parent_id){
        // mostly same but for items
        List<TodoItem> oversight = new ArrayList<TodoItem>();
        String select = "SELECT * FROM " + ITEM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()){
            do {
                TodoItem item = new TodoItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
                item.setChecker(cursor.getInt(cursor.getColumnIndex(CHECKER)));
                item.setParent(cursor.getLong(cursor.getColumnIndex(PARENT)));
                item.setTask(cursor.getString(cursor.getColumnIndex(TASK)));
                oversight.add(item);
            }while (cursor.moveToNext());
        }

        String[] data = new String[]{_ID, CHECKER, TASK};
        String compare = PARENT + "=" + parent_id;
        Cursor strider = db.query(true, ITEM, data, compare,
                null, null, null, null, null);

        if (strider != null) {cursor.moveToFirst();}
        return strider;
    }
}
