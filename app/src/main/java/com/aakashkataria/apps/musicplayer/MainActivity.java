package com.aakashkataria.apps.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView all_songs_list;
    int MY_PERMISSIONS_READ_EXTERNAL_STOREAGE = 1;
    File root_sd = Environment.getExternalStorageDirectory();
    File root_internal = new File("/");
    HashMap<String, String> hash;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    allsongs_db_class db;
    MediaPlayer mp;


    ArrayList<String> songs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = new MediaPlayer();
        all_songs_list = (ListView) findViewById(R.id.all_songs_list);
        requstpermissions();
        songs = new ArrayList<String>();
        hash = new HashMap<>();
        sp = getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        //if(!sp.contains("time_launched")) {
            read_all_songs_from_storage();
            //writetodb(hash);
//            if(songs.size() > 0) {
//                //edit.putString("time_launched", "yes");
//            }

        //}
//        else {
//            read_all_songs_from_db();
//            Log.e("SONGS", songs.get(0).toString());
//        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
        all_songs_list.setAdapter(adapter);
        all_songs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    mp.stop();
                    mp.setDataSource(songs.get(i).toString());
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void writetodb(HashMap<String, String> hash) {
        for (String key:  hash.keySet()){
            db.write_data(key, hash.get(key));
        }
    }

    private void read_all_songs_from_db() {
        hash.clear();
        songs.clear();
        Cursor cursor = db.read_data();
        while (!cursor.isLast()) {
            String loc, name;
            loc = cursor.getString(cursor.getColumnIndex("location"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            hash.put(loc, name);
            songs.add(name);
        }
    }

    private void read_all_songs_from_storage() {
        ArrayList<String> sd_songs = fetchsongs(root_sd);
        //ArrayList<String> internal_songs = fetchsongs(root_internal);
        if(sd_songs != null)
            songs.addAll(fetchsongs(root_sd));
//        if(internal_songs != null)
//            songs.addAll(fetchsongs(root_internal));
    }

    private ArrayList<String> fetchsongs(File root) {
        ArrayList<String> sngs = new ArrayList<String>();
        File list[] = root.listFiles();
        Log.e("TEMP", root.getAbsolutePath().toString());
        if(list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    sngs.addAll(fetchsongs(list[i]));
                } else if (list[i].getName().toLowerCase().endsWith(".mp3") || list[i].getName().toLowerCase().endsWith(".wav")) {
                    sngs.add(list[i].getAbsolutePath());
                    hash.put(list[i].getAbsolutePath(), list[i].getName());
                }
            }
        }
        return sngs;
    }

    private void requstpermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This Permission is needed for fetching songs from storage")
                        .setPositiveButton("OK, Request Permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_READ_EXTERNAL_STOREAGE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Snackbar alert_snak = Snackbar.make(findViewById(android.R.id.content), "Permission Denies, Cannot fetch songs.", Snackbar.LENGTH_LONG);
                                alert_snak.show();
                            }
                        })
                        .create().show();
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STOREAGE);
            }
        }
    }
}
