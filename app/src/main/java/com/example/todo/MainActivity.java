package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.todo.Adaptor.ToDoAdapter;
import com.example.todo.Model.ToDoModel;
import com.example.todo.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        db= new DatabaseHandler(this);
        db.openDataBase();

        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db,this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        fab =findViewById(R.id.fab);


        long time=System.currentTimeMillis()-1000*60*60*24*7;
        db.deleteTaskOnTime(time);



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        refresh();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask ant = AddNewTask.newInstance();
                ant.setListener(new AddNewTask.OnSaveClickListener() {
                    @Override
                    public void onSaveClicked() {
                        refresh();
                    }
                });
                ant.show(getSupportFragmentManager(),AddNewTask.TAG);
            }
        });
    }
     public Date currentDate = new Date();
    DateFormat df = new SimpleDateFormat("HH", Locale.getDefault());
    String timeText = df.format(currentDate);



    public void refresh() {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
    }
    @Override
    public void handleDialogClose(DialogInterface dialog){
        refresh();
    }
}