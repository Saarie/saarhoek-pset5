package com.example.saar.saarhoek_pset5;

/**
 * Created by Saar on 1-12-2016.
 */

public class TodoList {

    private int id;
    private String listname;

    // construct
    public TodoList () {}

    // to create list
    public TodoList(String listname) {
        this.listname = listname;
    }

    // to set list id
    public void setId (int id) {
        this.id = id;
    }

    // to get list id
    public int getId (){
        return this.id;
    }

    // to set list name
    public void setList (String newname){
        this.listname = newname;
    }

    // to get list name
    public String getList () {
        return this.listname;
    }
}
