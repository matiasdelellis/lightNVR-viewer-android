package ar.com.delellis.lightnvrviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import ar.com.delellis.lightnvrviewer.api.ApiClient;


public class VideoActivity extends AppCompatActivity {
    private final String TAG = VideoActivity.class.getCanonicalName();

    private VlcPlayer vlcPlayer = null;
    private VLCVideoLayout videoLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        vlcPlayer = VlcPlayer.getInstance(this);
        vlcPlayer.setEventListener(event -> {
            if (event.type == MediaPlayer.Event.Buffering) {
                Log.d(TAG, "onEvent: Buffering");
                findViewById(R.id.loading_card).setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "onEvent: Not buffering?");
                findViewById(R.id.loading_card).setVisibility(View.GONE);
            }
        });
        videoLayout = findViewById(R.id.videoLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();

        vlcPlayer.attachView(videoLayout);

        Intent intent = getIntent();
        String streamName = intent.getStringExtra("stream-name");

        TextView textView = findViewById(R.id.streamTitle);
        textView.setText(streamName);

        String videoUrl = ApiClient.getInstance().getVideoUrl(streamName);
        Log.i(TAG, "Playing video url: " + videoUrl);
        vlcPlayer.playUri(Uri.parse(videoUrl));
    }

    @Override
    protected void onStop() {
        super.onStop();

        vlcPlayer.stop();
        vlcPlayer.detachViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        vlcPlayer.release();
    }

}