package ar.com.delellis.lightnvrviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ar.com.delellis.lightnvrviewer.api.Stream;

public class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.StreamAdapterHolder> implements View.OnClickListener {

    Context context = null;
    View.OnClickListener onClickListener = null;
    List<Stream> streamList = null;

    public StreamAdapter(Context context, List<Stream> streamList) {
        this.context = context;
        this.streamList = streamList;
    }

    @Override
    public StreamAdapter.StreamAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_stream, parent, false);

        view.setOnClickListener(this);
        return new StreamAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamAdapter.StreamAdapterHolder holder, int position) {
        Stream stream = streamList.get(position);

        holder.streamNameView.setText(stream.getName());
        String comment = String.format(context.getString(R.string.stream_comment),stream.getWidth(), stream.getHeight(), stream.getFps(), stream.getCodec());
        holder.streamCommentView.setText(comment);
    }

    @Override
    public int getItemCount() {
        return streamList.size();
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

    public static class StreamAdapterHolder extends RecyclerView.ViewHolder {
        ImageView streamImageView;
        TextView streamNameView, streamCommentView;

        public StreamAdapterHolder(@NonNull View itemView) {
            super(itemView);

            streamImageView = itemView.findViewById(R.id.stream_image);
            streamNameView = itemView.findViewById(R.id.stream_name);
            streamCommentView = itemView.findViewById(R.id.stream_comment);
        }
    }
}
