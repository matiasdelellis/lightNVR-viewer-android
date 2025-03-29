package ar.com.delellis.lightnvrviewer.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pagination implements Serializable {
    @Expose
    @SerializedName("page")
    private int page;

    @Expose
    @SerializedName("pages")
    private int pages;

    @Expose
    @SerializedName("total")
    private int total;

    @Expose
    @SerializedName("limit")
    private int limit;

    public int getPage() {
        return page;
    }
    public int getPages() {
        return pages;
    }

    public int getTotal() {
        return total;
    }

    public int getLimit() {
        return limit;
    }
}

