package ar.com.delellis.lightnvrviewer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;


import java.util.List;

import ar.com.delellis.lightnvrviewer.api.ApiClient;
import ar.com.delellis.lightnvrviewer.api.ApiService;
import ar.com.delellis.lightnvrviewer.api.Recording;
import ar.com.delellis.lightnvrviewer.api.Recordings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoActivity extends AppCompatActivity {
    private final String TAG = VideoActivity.class.getCanonicalName();

    private VlcPlayer vlcPlayer = null;
    private VLCVideoLayout videoLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.video_toolbar);
        setSupportActionBar(myToolbar);

        vlcPlayer = VlcPlayer.getInstance(this);
        vlcPlayer.setEventListener(event -> {
            if (event.type == MediaPlayer.Event.Buffering) {
                Log.d(TAG, "onEvent: Buffering");
                findViewById(R.id.loading_card).setVisibility(VISIBLE);
            } else {
                Log.d(TAG, "onEvent: Not buffering?");
                findViewById(R.id.loading_card).setVisibility(GONE);
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(streamName);
        }

        getRecordings(streamName, 1, 20);

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getRecordings(String streamName, int page, int limit) {
        ApiClient apiClient = ApiClient.getInstance();
        ApiService apiService = ApiClient.getApiService();

        Call<Recordings> recordingsCall = apiService.recordings(streamName, page, limit, apiClient.getAuthorization());
        recordingsCall.enqueue(new Callback<Recordings>() {
            @Override
            public void onResponse(Call<Recordings> call, Response<Recordings> response) {
                Recordings recordings = response.body();
                List<Recording> recordingList = recordings.getRecordings();
                Log.d(TAG, "Recording count" + recordings.getRecordings().size());
                Log.d(TAG, "Recording count" + recordings.getPagination().getTotal());

                RecyclerView recyclerView = findViewById(R.id.recording_list);

                RecordingAdapter recordingAdapter = new RecordingAdapter(VideoActivity.this, recordingList);
                recordingAdapter.setOnClickListener(view -> {
                    Recording recording = recordingList.get(recyclerView.getChildAdapterPosition(view));
                    String recordingUrl = apiClient.getRecordingUrl(recording.getId());

                    Log.i(TAG, "Playing video url: " + recordingUrl);
                    vlcPlayer.playUri(Uri.parse(recordingUrl));
                });

                recyclerView.setAdapter(recordingAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(VideoActivity.this));
            }

            @Override
            public void onFailure(Call<Recordings> call, Throwable throwable) {
                // TODO
                throwable.printStackTrace();
            }
        });
    }
}