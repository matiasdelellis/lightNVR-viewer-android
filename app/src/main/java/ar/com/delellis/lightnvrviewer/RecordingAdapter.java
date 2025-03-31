package ar.com.delellis.lightnvrviewer;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ar.com.delellis.lightnvrviewer.api.Recording;
import ar.com.delellis.lightnvrviewer.api.Util;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.RecordingAdapterHolder> implements View.OnClickListener {

    Context context = null;
    View.OnClickListener onClickListener = null;
    List<Recording> recordingList = null;

    public RecordingAdapter(Context context, List<Recording> recordingList) {
        this.context = context;
        this.recordingList = recordingList;
    }

    @Override
    public RecordingAdapter.RecordingAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_recording, parent, false);

        view.setOnClickListener(this);
        return new RecordingAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingAdapter.RecordingAdapterHolder holder, int position) {
        Recording recording = recordingList.get(position);
        if (recording.getId() > 0) {
            holder.recordingNameView.setText(Util.getLocalTime(recording.getStartTime()));
            String length = String.format(context.getString(R.string.seconds_format),recording.getDuration());
            holder.recordingLengthView.setText(length);
        } else {
            holder.recordingNameView.setText(R.string.live_view);
            holder.recordingLengthView.setVisibility(GONE);
            holder.recordingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_play_outline_24));}

    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view) {
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    public static class RecordingAdapterHolder extends RecyclerView.ViewHolder {
        ImageView recordingImageView;
        TextView recordingNameView, recordingLengthView;

        public RecordingAdapterHolder(@NonNull View itemView) {
            super(itemView);

            recordingImageView = itemView.findViewById(R.id.recording_icon);
            recordingNameView = itemView.findViewById(R.id.recording_name);
            recordingLengthView = itemView.findViewById(R.id.recording_lenght);
        }
    }
}
