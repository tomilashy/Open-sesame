package com.project.coen_elec_390;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Credits extends AppCompatActivity {

    ArrayAdapter<String> shadiAdapter,alecAdapter,irfanAdapter,tomiAdapter,cvuAdapter;
    ArrayAdapter adapter2;
    List<String> shadiList = new ArrayList<>();
    List<String> alecList = new ArrayList<>();
    List<String> irfanList = new ArrayList<>();
    List<String> tomiList = new ArrayList<>();
    List<String> cvuList = new ArrayList<>();
    ListView shadiListView,alecListView,irfanListView,tomiListView,cvuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        setTitle("Credits");
        shadiList.addAll(Arrays.asList(new String[]{"Shadi Makdissi","smakdissi@gmail.com"}));
        alecList.addAll(Arrays.asList(new String[]{ "Alec Kurkdijan","Aleckurk@gmail.com"}));
        irfanList.addAll(Arrays.asList(new String[]{"Irfan Ahmed","irfan298@gmail.com" }));
        tomiList.addAll(Arrays.asList(new String[]{"Olasubulumi Jesutomi","tomilashy@gmail.com"}));
        cvuList.addAll(Arrays.asList(new String[]{"Cong-Vinh Vu","congvinhvu98@gmail.com" }));

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting adapter
        shadiAdapter = new ArrayAdapter<String>(this, R.layout.listview2, shadiList);
        alecAdapter = new ArrayAdapter<String>(this, R.layout.listview2, alecList);
        irfanAdapter = new ArrayAdapter<String>(this, R.layout.listview2, irfanList);
        tomiAdapter = new ArrayAdapter<String>(this, R.layout.listview2, tomiList);
        cvuAdapter = new ArrayAdapter<String>(this, R.layout.listview2, cvuList);

        //displaying list view
        shadiListView = findViewById(R.id.shadiListview);
        shadiListView.setAdapter(shadiAdapter);
        alecListView = findViewById(R.id.alecListview);
        alecListView.setAdapter(alecAdapter);
        irfanListView = findViewById(R.id.irfanListview);
        irfanListView.setAdapter(irfanAdapter);
        tomiListView = findViewById(R.id.tomiListview);
        tomiListView.setAdapter(tomiAdapter);
        cvuListView = findViewById(R.id.cvuListview);
        cvuListView.setAdapter(cvuAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish(); // When home button in clicked, end the activity and return to MainActivity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
