package ar.com.delellis.lightnvrviewer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ar.com.delellis.lightnvrviewer.api.ApiClient;
import ar.com.delellis.lightnvrviewer.api.ApiService;
import ar.com.delellis.lightnvrviewer.api.Stream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText hostText = null;
    private EditText usernameText = null;
    private EditText passwordText = null;
    private ProgressBar progressBar = null;
    private Button logingButton = null;

    SecureStore secureStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hostText = (EditText) findViewById(R.id.editServer);
        usernameText = (EditText) findViewById(R.id.editUsername);
        passwordText = (EditText) findViewById(R.id.editPassword);

        secureStore = SecureStore.getInstance(this);
        hostText.setText(secureStore.getConfigHost());
        usernameText.setText(secureStore.getConfigUsername());
        passwordText.setText(secureStore.getConfigPassword());

        progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(GONE);

        logingButton = (Button) findViewById(R.id.buttonLogin);
        logingButton.setOnClickListener(view -> {
            List<Stream> streamsList = null;
            String host, username, password;

            host = hostText.getText().toString();
            username = usernameText.getText().toString();
            password = passwordText.getText().toString();

            if (!validate_credentials(host, username, password))
                return;

            performLogin(host, username, password);
        });
    }
    private boolean validate_credentials(String host, String username, String password) {
        if (host.isEmpty()) {
            hostText.setError(getString(R.string.required_configuration));
            return false;
        }
        if (username.isEmpty()) {
            usernameText.setError(getString(R.string.required_configuration));
            return false;
        }
        if (password.isEmpty()) {
            passwordText.setError(getString(R.string.required_configuration));
            return false;
        }

        try {
            new URL(host);
        } catch (MalformedURLException e) {
            hostText.setError(e.getLocalizedMessage());
            return false;
        }

        return true;
    }
    private void performLogin(String host, String username, String password) {
        progressBar.setVisibility(VISIBLE);
        logingButton.setEnabled(false);

        ApiClient apiClient = ApiClient.getInstance(host, username, password);
        ApiService apiService = ApiClient.getApiService();

        Call<List<Stream>> streamsListCall = apiService.streamsList(apiClient.getAuthorization());
        streamsListCall.enqueue(new Callback<List<Stream>>() {
            @Override
            public void onResponse(Call<List<Stream>> call, Response<List<Stream>> response) {
                List<Stream> streamList = response.body();

                Log.i(TAG, "Valid login: Saving credentials");
                secureStore.setConfigHost(host);
                secureStore.setConfigUsername(username);
                secureStore.setConfigPassword(password);

                Log.i(TAG, "Go to stream list");
                Intent intent = new Intent(LoginActivity.this, StreamsActivity.class);
                intent.putExtra("streams-list", (Serializable) streamList);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<List<Stream>> call, Throwable throwable) {
                // TODO: Show dialog with message
                Log.e(TAG, throwable.toString());
                progressBar.setVisibility(GONE);
                logingButton.setEnabled(true);
            }
        });
    }
}