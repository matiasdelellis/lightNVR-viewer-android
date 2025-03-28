package ar.com.delellis.lightnvrviewer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

import ar.com.delellis.lightnvrviewer.api.ApiClient;
import ar.com.delellis.lightnvrviewer.api.Stream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SecureStore secureStore = SecureStore.getInstance(this);
        if (!secureStore.hasCredentials()) {
            goToLogin();
        }

        ApiClient apiClient = ApiClient.getInstance(
                secureStore.getConfigHost(),
                secureStore.getConfigUsername(),
                secureStore.getConfigPassword()
        );

        Call<List<Stream>> streamsListCall = ApiClient.getApiService().streamsList(apiClient.getAuthorization());
        streamsListCall.enqueue(new Callback<List<Stream>>() {
            @Override
            public void onResponse(Call<List<Stream>> call, Response<List<Stream>> response) {
                List<Stream> streamsList = response.body();
                goToStreams(streamsList);
            }
            @Override
            public void onFailure(Call<List<Stream>> call, Throwable throwable) {
                // TODO: Show dialog with message
                Log.e(TAG, throwable.toString());
                goToLogin();
            }
        });
    }

    private void goToStreams(List<Stream> streamsList) {
        Intent intent = new Intent(SplashActivity.this, StreamsActivity.class);
        intent.putExtra("streams-list", (Serializable) streamsList);
        startActivity(intent);
        finish();
    }
    private void goToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}