package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
        shadiList.addAll(Arrays.asList(new String[]{"Shadi Makdissi","Project Leader"}));
        alecList.addAll(Arrays.asList(new String[]{ "Alec Kurkdijan","Developer"}));
        irfanList.addAll(Arrays.asList(new String[]{"Irfan Ahmed","Developer" }));
        tomiList.addAll(Arrays.asList(new String[]{"Olasubulumi Jesutomi","Developer"}));
        cvuList.addAll(Arrays.asList(new String[]{"Cong-Vinh Vu","Technical Lead" }));
//        setting adapter
        shadiAdapter = new ArrayAdapter<String>(this, R.layout.listview2, shadiList);
        alecAdapter = new ArrayAdapter<String>(this, R.layout.listview2, alecList);
        irfanAdapter = new ArrayAdapter<String>(this, R.layout.listview2, irfanList);
        tomiAdapter = new ArrayAdapter<String>(this, R.layout.listview2, tomiList);
        cvuAdapter = new ArrayAdapter<String>(this, R.layout.listview2, cvuList);
//        displaying list view

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


}
