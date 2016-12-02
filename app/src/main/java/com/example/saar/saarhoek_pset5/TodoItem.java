package com.example.saar.saarhoek_pset5;

/**
 * Created by Saar on 1-12-2016.
 */

public class TodoItem {
    // id and task are the identifiers
    // checker denotes image, parent denotes list item is in
    private int id;
    private String task;
    private int checker;
    private long parent;

    // construct
    public TodoItem(){}

    public TodoItem(String task, int checker, long parent){
        this.task = task;
        this.checker = checker;
        this.parent = parent;
    }

    // id
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    // name of the task
    public void setTask(String task){
        this.task = task;
    }

    public String getTask(){
        return this.task;
    }

    // parent list
    public void setParent(long parent){
        this.parent = parent;
    }

    public long getParent(){
        return this.parent;
    }

    // image of empty or checked box
    public void setChecker(int checker){
        this.checker = checker;
    }

    public int getChecker(){
        return this.checker;
    }
}
