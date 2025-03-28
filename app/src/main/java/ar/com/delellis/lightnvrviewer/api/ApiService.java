package ar.com.delellis.lightnvrviewer.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {
    @GET("streams")
    Call<List<Stream>> streamsList(@Header("Authorization") String authorization);
}
