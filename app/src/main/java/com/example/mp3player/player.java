package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class player extends AppCompatActivity {


    static MediaPlayer mp;//assigning memory loc once or else multiple songs will play at once
    int position;
    SeekBar sb;
    ArrayList<File> mySongs;
    Thread updateSeekBar;
    Button pause, next, previous;
    TextView songNameText;

    String sname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        songNameText = (TextView) findViewById(R.id.ctrack);



        pause = (Button) findViewById(R.id.pause);

        previous = (Button) findViewById(R.id.prev);
        next = (Button) findViewById(R.id.next);

        sb = (SeekBar) findViewById(R.id.pos);


        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (IllegalStateException e) {

                    }
                }
            }
        };


        if (mp != null) {
            mp.stop();
            mp.release();
        }
        Intent i = getIntent();
        Bundle b = i.getExtras();


        mySongs = (ArrayList) b.getParcelableArrayList("tracklist");

        sname = mySongs.get(position).getName().toString();

        String SongName = i.getStringExtra("track");
        songNameText.setText(SongName);
        songNameText.setSelected(true);

        position = b.getInt("pos", 0);
        Uri u = Uri.parse(mySongs.get(position).toString());

        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekBar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        sb.setOnSeekBarChangeListener(new
                                              SeekBar.OnSeekBarChangeListener() {
                                                  @Override
                                                  public void onProgressChanged(SeekBar seekBar, int i,
                                                                                boolean b) {
                                                  }

                                                  @Override
                                                  public void onStartTrackingTouch(SeekBar seekBar) {
                                                  }

                                                  @Override
                                                  public void onStopTrackingTouch(SeekBar seekBar) {
                                                      mp.seekTo(seekBar.getProgress());

                                                  }
                                              });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setMax(mp.getDuration());
                if (mp.isPlaying()) {
                    pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mp.pause();

                } else {
                    pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mp.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                // songNameText.setText(getSongName);
                mp = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);

                try {
                    mp.start();
                } catch (Exception e) {
                }

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songNameText.setText(getSongName);
                mp.stop();
                mp.release();

                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);
                mp.start();
            }
        });


    }

}