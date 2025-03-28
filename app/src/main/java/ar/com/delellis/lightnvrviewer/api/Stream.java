package ar.com.delellis.lightnvrviewer.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Stream implements Serializable {
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("width")
    private int width;
    @Expose
    @SerializedName("height")
    private int height;
    @Expose
    @SerializedName("fps")
    private int fps;
    @Expose
    @SerializedName("codec")
    private String codec;

    public String getName() {
        return name;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getFps() {
        return fps;
    }
    public String getCodec() {
        return codec;
    }
}
