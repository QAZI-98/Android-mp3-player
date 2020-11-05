package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView files_listView;
    String[] items;
    String track_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        files_listView = findViewById(R.id.item_list);
        storage_access_permission();
    }

    public ArrayList<File> search_file(File file) {

        ArrayList<File> list = new ArrayList<>();
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.isDirectory() && !f.isHidden()) {
                list.addAll(search_file(f));
            } else {
                if (f.getName().endsWith(".mp3") || f.getName().endsWith(".wav")) {
                    list.add(f);
                }
            }

        }
        return list;
    }


    void display() {

        final ArrayList<File> my_files = search_file(Environment.getExternalStorageDirectory());
        items = new String[my_files.size()];

        for (int i = 0; i < my_files.size(); i++) {

            items[i] = my_files.get(i).getName().toString().replace(".mp3", "");
        }

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        files_listView.setAdapter(stringArrayAdapter);

        files_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                track_name = files_listView.getItemAtPosition(i).toString();

                startActivity(new Intent(MainActivity.this, player.class)
                        .putExtra("track", track_name).putExtra("tracklist", my_files).putExtra("pos", i)
                );
            }
        });

    }


    public void storage_access_permission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


}