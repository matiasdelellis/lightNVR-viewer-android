package ar.com.delellis.lightnvrviewer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ar.com.delellis.lightnvrviewer.api.Stream;

public class StreamsActivity extends AppCompatActivity {
    private List<Stream> streamsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streams);

        Intent intent = getIntent();
        streamsList = (List<Stream>) intent.getSerializableExtra("streams-list");

        RecyclerView recyclerView = findViewById(R.id.stream_list);

        StreamAdapter streamAdapter = new StreamAdapter(this, streamsList);
        streamAdapter.setOnClickListener(view -> {
            Stream stream = streamsList.get(recyclerView.getChildAdapterPosition(view));

            Intent videoIntent = new Intent(StreamsActivity.this, VideoActivity.class);
            videoIntent.putExtra("stream-name", stream.getName());
            StreamsActivity.this.startActivity(videoIntent);
        });

        recyclerView.setAdapter(streamAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL, false));
    }
}