package ar.com.delellis.lightnvrviewer.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Recording implements Serializable {
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("stream")
    private String stream;

    @Expose
    @SerializedName("start_time")
    private String start_time;

    @Expose
    @SerializedName("duration")
    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getStartTime() {
        return start_time;
    }

    public int getDuration() {
        return duration;
    }
}
