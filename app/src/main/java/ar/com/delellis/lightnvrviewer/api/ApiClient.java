package ar.com.delellis.lightnvrviewer.api;

import android.util.Base64;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private String protocol = null;
    private String baseUrl = null;
    private String username = null;
    private String password = null;

    private static ApiService apiService = null;
    private static ApiClient apiClient = null;

    private ApiClient(String url, String username, String password) {
        URL host = null;
        try {
            host = new URL(url);
        } catch (MalformedURLException e) {
            // Nothing...
            return;
        }

        this.protocol = host.getProtocol() + "://";
        this.baseUrl = host.getHost();
        this.username = username;
        this.password = password;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getApiBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiClient getInstance (String url, String username, String password) {
        if (apiClient != null) {
            // TODO: Thrown if initialized or maybe clean it.
        }
        apiClient = new ApiClient(url, username, password);
        return apiClient;
    }

    public static ApiClient getInstance() {
        // TODO: Thrown if not initialized..
        return apiClient;
    }

    public static ApiService getApiService() {
        return apiService;
    }

    public String getLiveUrl(String name) {
        return this.protocol + getCredentials() + "@" + this.baseUrl + "/hls/" + name + "/index.m3u8";
    }

    public String getRecordingUrl(int recordingId) {
        return this.protocol + getCredentials() + "@" + this.baseUrl + "/api/recordings/play/" + recordingId;
    }
    public String getAuthorization() {
        String credentials = getCredentials();
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.URL_SAFE|Base64.NO_WRAP);
    }

    private String getApiBase() {
        return this.protocol + this.baseUrl + "/api/";
    }

    private String getCredentials() {
        return this.username + ":" + this.password;
    }
}
