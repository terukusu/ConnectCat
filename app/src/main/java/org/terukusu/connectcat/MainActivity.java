package org.terukusu.connectcat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.terukusu.connectcat.model.ConnectCat;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "CONNECT_CAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConnectButtonClick(View view) throws IOException {

        // 接続先のフォーマットの確認
        String host = ((EditText)findViewById(R.id.textAddress)).getText().toString();

        // ホストとポートに分割
        String portString = ((EditText)findViewById(R.id.textPort)).getText().toString();
        Integer port = null;
        try {
            port = Integer.valueOf(portString);
        } catch(NumberFormatException e) {
            // nothing to do
        }

        if (host.isEmpty() || portString.isEmpty() || port == null) {
            Toast.makeText(MainActivity.this, R.string.error_format_activity_main, Toast.LENGTH_SHORT).show();
            return;
        }

        // メインスレッドで名前解決(=ネットワークアクセス)は出来ないのでここでは未解決状態にしておく
        InetSocketAddress addr = InetSocketAddress.createUnresolved(host, port);

        new AsyncTask<InetSocketAddress, Integer, Integer>() {

            final
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            private boolean isSuccess = true;

            @Override
            protected void onPreExecute() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getResources().getString(R.string.connecting_activity_main));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Integer doInBackground(InetSocketAddress... addrs) {
                try {
                    if (addrs[0].isUnresolved()) {
                        addrs[0] = new InetSocketAddress(addrs[0].getHostName(), addrs[0].getPort());
                    }
                    ConnectCat.getInstance().connect(addrs[0]);
                } catch (IOException e) {
                    Log.e(TAG, "Connection failed.", e);
                    this.isSuccess = false;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                progressDialog.hide();
                if (this.isSuccess) {
                    startActivity(new Intent(MainActivity.this, WorkActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Connection failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(addr);
    }
}
