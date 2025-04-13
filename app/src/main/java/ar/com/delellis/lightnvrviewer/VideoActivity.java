package ar.com.delellis.lightnvrviewer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import ar.com.delellis.lightnvrviewer.api.ApiClient;
import ar.com.delellis.lightnvrviewer.api.ApiService;
import ar.com.delellis.lightnvrviewer.api.Recording;
import ar.com.delellis.lightnvrviewer.api.Recordings;
import ar.com.delellis.lightnvrviewer.api.Stream;
import ar.com.delellis.lightnvrviewer.api.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoActivity extends AppCompatActivity {
    private final String TAG = VideoActivity.class.getCanonicalName();

    private VlcPlayer vlcPlayer = null;
    private VLCVideoLayout vlcVideoLayout = null;

    private Stream currentStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        currentStream = (Stream) intent.getSerializableExtra("stream");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.video_toolbar);
        setSupportActionBar(myToolbar);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);

        int orientation =  getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            myToolbar.setVisibility(VISIBLE);
            findViewById(R.id.recording_list).setVisibility(VISIBLE);

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int videoWidth = currentStream.getWidth();
            int videoHeight = currentStream.getHeight();
            frameLayout.getLayoutParams().height = videoHeight * screenWidth / videoWidth;
        }
        else {
            myToolbar.setVisibility(GONE);
            findViewById(R.id.recording_list).setVisibility(GONE);

            frameLayout.getLayoutParams().height = -1;
        }

        vlcPlayer = VlcPlayer.getInstance(this);
        vlcPlayer.setEventListener(event -> {
            if (event.type == MediaPlayer.Event.Buffering) {
                if (event.getBuffering() == 100f) {
                    findViewById(R.id.loading_progress).setVisibility(GONE);
                } else {
                    findViewById(R.id.loading_progress).setVisibility(VISIBLE);
                }
            }
        });

        vlcVideoLayout = findViewById(R.id.vlc_video_Layout);

        vlcVideoLayout.setOnTouchListener(new VideoTouchListener(vlcVideoLayout));
    }

    @Override
    protected void onStart() {
        super.onStart();

        vlcPlayer.attachView(vlcVideoLayout);

        String streamName = currentStream.getName();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(streamName);
        }

        updateRecordingsList(streamName, 1, 20);

        String videoUrl = ApiClient.getInstance().getLiveUrl(streamName);
        Log.i(TAG, "Playing video url: " + videoUrl);
        vlcPlayer.playUri(Uri.parse(videoUrl));

        FloatingActionButton takeSnapshot = findViewById(R.id.takeSnapshot);
        takeSnapshot.setOnClickListener(v -> takeSnapshot());
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

    private void updateRecordingsList(String streamName, int page, int limit) {
        ApiClient apiClient = ApiClient.getInstance();
        ApiService apiService = ApiClient.getApiService();

        Call<Recordings> recordingsCall = apiService.recordings(streamName, page, limit, apiClient.getAuthorization());
        recordingsCall.enqueue(new Callback<Recordings>() {
            @Override
            public void onResponse(Call<Recordings> call, Response<Recordings> response) {
                Recordings recordings = response.body();
                List<Recording> recordingList = recordings.getRecordings();

                Recording live = new Recording();
                live.setId(-1);
                live.setStream(streamName);
                recordingList.addFirst(live);

                RecyclerView recyclerView = findViewById(R.id.recording_list);

                RecordingAdapter recordingAdapter = new RecordingAdapter(VideoActivity.this, recordingList);
                recordingAdapter.setOnClickListener(view -> {
                    Recording recording = recordingList.get(recyclerView.getChildAdapterPosition(view));
                    String recordingUrl = null;
                    int recordingId = recording.getId();
                    if (recordingId > 0) {
                        recordingUrl = apiClient.getRecordingUrl(recordingId);
                    } else {
                        recordingUrl = apiClient.getLiveUrl(recording.getStream());
                    }

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
    private void takeSnapshot() {
        SurfaceView surfaceView = findViewById(org.videolan.R.id.surface_video);
        Util.getSurfaceBitmap(surfaceView, new Util.PixelCopyListener() {
            @Override
            public void onSurfaceBitmapReady(Bitmap bitmap) {
                File snapshotFile = Util.getSnapshotFile(currentStream.getName());
                try {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(snapshotFile));
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
                    Util.shareImage(VideoActivity.this, Uri.parse(snapshotFile.getPath()), currentStream.getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onSurfaceBitmapError(String errorMsg) {
                Toast.makeText(VideoActivity.this, errorMsg, LENGTH_LONG).show();
            }
        });
    }
}