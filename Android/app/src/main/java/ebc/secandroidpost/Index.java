package ebc.secandroidpost;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ebc.secandroidpost.interfaces.IAppManager;
import ebc.secandroidpost.services.BankingService;

public class Index extends AppCompatActivity {
    public static final String AUTHENTICATION_FAILED = "0";
    public static Context context;
    private IAppManager bankingservice;
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Index.context = getApplicationContext();
        startService(new Intent(Index.this, BankingService.class));
        setContentView(R.layout.activity_index);
        setTitle("Login");
        loginButton = (Button) findViewById(R.id.btnLogin);
        usernameText = (EditText) findViewById(R.id.etUsername);
        passwordText = (EditText) findViewById(R.id.etPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (bankingservice == null) {
                    Toast.makeText(getApplicationContext(), R.string.not_connected_to_service, Toast.LENGTH_LONG).show();
                } else if (!bankingservice.isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), R.string.not_connected_to_network, Toast.LENGTH_LONG).show();
                } else if (usernameText.length() >= 3 && passwordText.length() >= 3) {
                    new loginTask().execute();
                }
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            bankingservice = ((BankingService.IMBinder) service).getService();
            if (bankingservice.isUserAuthenticated()) {
                startActivity(new Intent(Index.this, Home.class));
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            bankingservice = null;
            Toast.makeText(Index.this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
        }
    };

    private class loginTask extends AsyncTask<String, Void, String> {
        String usernametext, passwordtext;

        public loginTask() {
            this.usernametext = usernameText.getText().toString();
            this.passwordtext = passwordText.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginButton.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(passwordtext.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            passwordtext = sb.toString();
            return bankingservice.authenticateUser(usernametext, passwordtext);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loginButton.setEnabled(true);
            if (result == null || result.equals(AUTHENTICATION_FAILED) || result.equals("")) {
                Toast.makeText(getApplicationContext(), R.string.make_sure_combo_correct, Toast.LENGTH_LONG).show();
            } else if (result.equals("success")) {
                startActivity(new Intent(Index.this, Home.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        unbindService(mConnection);
        super.onPause();
    }

    @Override
    protected void onResume() {
        bindService(new Intent(Index.this, BankingService.class), mConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }
}
