package org.terukusu.connectcat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.terukusu.connectcat.model.ConnectCat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class WorkActivity extends AppCompatActivity {

    private Thread receiverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.receiverThread = new Thread() {
            @Override
            public void run() {
                ConnectCat cc = ConnectCat.getInstance();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try {
                    final byte[] buff = new byte[8192];
                    int len = 0;
                    while(true) {
                        try {
                            len = cc.read(buff);
                        } catch (SocketTimeoutException e) {
                            if (Thread.interrupted()) {
                                break;
                            }

                            continue;
                        }

                        if(len == -1) {
                            Log.d(MainActivity.TAG, "*********** kita---1 ***********");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WorkActivity.this, R.string.connection_closed_activity_work, Toast.LENGTH_SHORT).show();
                                    ((Button) findViewById(R.id.sendButton)).setEnabled(false);
                                }
                            });

                            break;
                        }

                        final int _len = len;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView responseText = (TextView) findViewById(R.id.responseText);
                                responseText.setText(responseText.getText() + new String(buff, 0, _len));
                                final ScrollView scrollView = (ScrollView) findViewById(R.id.responseScrollView);
                                scrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("CONNECT_CAT", "read failed.", e);
                }
            }
        };

        this.receiverThread.start();
    }

    public void onSendButtonClicked(View view) throws IOException {
        final String message = ((EditText)findViewById(R.id.messageText)).getText().toString();

        new AsyncTask<String, Integer, Integer>() {

            final ProgressDialog progressDialog = new ProgressDialog(WorkActivity.this);
            Exception exception = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getResources().getString(R.string.sending_message_activity_work));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Integer doInBackground(String... strings) {
                ByteArrayOutputStream baos = null;

                byte[] response = null;

                try {
                    ConnectCat.getInstance().send(strings[0]);
                } catch (Exception e) {
                    this.exception = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                progressDialog.dismiss();
           }

        }.execute(message);
    }

    public void onDisconnectButtonClicked(View view) throws IOException {
        new AsyncTask<Integer, Integer, Integer>() {

            final ProgressDialog progressDialog = new ProgressDialog(WorkActivity.this);
            boolean isSuccess = true;

            @Override
            protected void onPreExecute() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getResources().getString(R.string.disconnecting_activity_work));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Integer doInBackground(Integer... integers) {
                try {
                    ConnectCat.getInstance().disconnect();
                } catch (IOException e) {
                    Log.e(MainActivity.TAG, "Disconnection failed.", e);
                    synchronized (this) {
                        this.isSuccess = false;
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                progressDialog.dismiss();
                synchronized (this) {
                    if (!this.isSuccess) {
                        Toast.makeText(WorkActivity.this, "Disconnect failed.", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        }.execute();
    }
}