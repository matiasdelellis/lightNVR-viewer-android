package ar.com.delellis.lightnvrviewer.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    @GET("streams")
    Call<List<Stream>> streamsList(@Header("Authorization") String authorization);

    @GET("recordings")
    Call<Recordings> recordings(@Query("stream") String stream, @Query("page") int page, @Query("limit") int limit, @Header("Authorization") String authorization);
}
