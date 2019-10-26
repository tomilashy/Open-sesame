package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreditsActivity extends AppCompatActivity {

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
        Log.d("Etienne is bawss","in credits");
        shadiList.addAll(Arrays.asList(new String[]{"Project Leader", "Shadi Makdissi"}));
        alecList.addAll(Arrays.asList(new String[]{"Developer", "Shadi Makdissi"}));
        irfanList.addAll(Arrays.asList(new String[]{"Developer", "Shadi Makdissi"}));
        tomiList.addAll(Arrays.asList(new String[]{"Developer", "Shadi Makdissi"}));
        cvuList.addAll(Arrays.asList(new String[]{"Developer", "Shadi Makdissi"}));
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
