package ebc.secandroidpost;

/**
 * Created by echeb on 25-Jul-16.
 */

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

import ebc.secandroidpost.interfaces.IAppManager;
import ebc.secandroidpost.services.BankingService;

public class Home extends AppCompatActivity {
    private IAppManager bankingservice;
    private EditText messageText;
    private Button sendMessageButton;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            bankingservice = ((BankingService.IMBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            bankingservice = null;
            Toast.makeText(Home.this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sendMessageButton = (Button) findViewById(R.id.btnsend);
        messageText = (EditText) findViewById(R.id.etSend);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            String message;

            public void onClick(View arg0) {
                message = messageText.getText().toString().trim();
                if (bankingservice.isNetworkConnected()) {
                    if (message.length() > 0) {
                        new sendMessageTask(message).execute();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.enter_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_connected_to_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class sendMessageTask extends AsyncTask<String, Void, String> {
        String message;

        public sendMessageTask(String message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendMessageButton.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            return bankingservice.sendMessage(message);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            sendMessageButton.setEnabled(true);
            if (result == null || result.equals("")) {
                Toast.makeText(getApplicationContext(), R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();
            } else if (result.equals("1")) {
                messageText.setText("");
                Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(Home.this, BankingService.class), mConnection, Context.BIND_AUTO_CREATE);
    }
}
