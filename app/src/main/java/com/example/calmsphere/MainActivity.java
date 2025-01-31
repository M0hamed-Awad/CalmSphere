package com.example.calmsphere;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    ImageView playButton;
    ImageView loopButton;
    ImageView backwardButton;
    ImageView forwardButton;

    SeekBar seekBar;
    Timer timer;
    TextView soundNameTv;

    ArrayList<Integer> soundsList;

    int currentSoundIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialization
        initializeViews();
        initializeSoundsList();

        // Display the Sound Name
        updateSoundName();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), soundsList.get(currentSoundIndex));
        seekBar.setMax(mediaPlayer.getDuration());

        // Seek Bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Play Button
        playButton.setOnClickListener(v -> { handleClickingOnPlayButton(); });

        // Forward Button
        forwardButton.setOnClickListener(v -> { handleClickOnForwardButton(); });

        // Backward Button
        backwardButton.setOnClickListener(v -> { handleClickOnBackwardButton(); });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void initializeSoundsList() {
        soundsList = new ArrayList<>();
        soundsList.add(R.raw.dinosaur_forest);
        soundsList.add(R.raw.ambient_nature_sounds);
    }

    private void initializeViews() {
        playButton = findViewById(R.id.main_activity_play_btn);
        loopButton = findViewById(R.id.main_activity_loop_btn);
        backwardButton = findViewById(R.id.main_activity_go_backward_btn);
        forwardButton = findViewById(R.id.main_activity_go_forward_btn);

        soundNameTv = findViewById(R.id.main_activity_sound_name_tv);

        seekBar = findViewById(R.id.seekBar);
    }

    private void handleClickOnBackwardButton() {
        // if we didn't pass 10 seconds then go to the previous sound
        if (mediaPlayer.getCurrentPosition() < 10000){
            currentSoundIndex = currentSoundIndex == 0 ? soundsList.size() -1 : ((currentSoundIndex - 1) % soundsList.size());
            int soundResId = soundsList.get(currentSoundIndex % soundsList.size());
            changeCurrentSound(soundResId);
        }
        // else go to the start of the sound
        else {
            try {
                releaseMediaPlayer();
                seekBar.setProgress(0);
                playSound(soundsList.get(currentSoundIndex));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClickOnForwardButton() {
        currentSoundIndex = (currentSoundIndex + 1) % soundsList.size();
        int soundResId = soundsList.get(currentSoundIndex % soundsList.size());
        changeCurrentSound(soundResId);
    }

    private void handleClickingOnPlayButton() {
        try {
            int soundResId = soundsList.get(currentSoundIndex % soundsList.size());
            if ( mediaPlayer != null && mediaPlayer.isPlaying() ) {
                pauseSound();
            } else {
                // update seek bar progress
                changeSeekBarProgress();
                playSound(soundResId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSoundName(int soundIndex) {
        String soundName;
        switch (soundIndex) {
            case 0:
                soundName = "Dinosaur Forest";
                break;
            case 1:
                soundName = "Ambient Nature Sounds";
                break;
            default:
                soundName = "";
        }
        return soundName;
    }

    private void updateSoundName() {
        String soundName = getSoundName(currentSoundIndex);
        soundNameTv.setText(soundName);
    }

    private void changeCurrentSound(int soundResId) {
        releaseMediaPlayer();
        playSound(soundResId);
        seekBar.setMax(mediaPlayer.getDuration());
        updateSoundName();
    }

    private void playSound(int soundResId) {
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), soundResId);
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            playButton.setImageResource(R.drawable.play_button);
        });

        playButton.setImageResource(R.drawable.pasue_button);
        mediaPlayer.start();

    }

    private void pauseSound(){
        mediaPlayer.pause();
        playButton.setImageResource(R.drawable.play_button);
    }

    public void changeSeekBarProgress() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}