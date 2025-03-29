package ar.com.delellis.lightnvrviewer.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Recordings implements Serializable {
    @Expose
    @SerializedName("recordings")
    List<Recording> recordings;

    @Expose
    @SerializedName("pagination")
    private Pagination pagination;

    public List<Recording> getRecordings() {
        return recordings;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
