package com.demo.demofayechat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.saulpower.fayeclient.FayeClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements FayeClient.FayeListener {

    Handler handler = new Handler(Looper.getMainLooper());
    boolean isEnterName = false;

    TextView textMessage;
    FayeClient publicChannel;
    FayeClient serverPublicChannel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        textMessage = (TextView) findViewById(R.id.textMessage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText editMessage = (EditText) findViewById(R.id.editMessage);
                    String msg = editMessage.getText().toString();
                    editMessage.setText("");

                    JSONObject jsonMsg = new JSONObject();
                    jsonMsg.put("text", msg);

                    if (!isEnterName) {
                        FayeClient registerChannel = new FayeClient(handler, URI.create("http://10.0.2.2:8001/chat"), "/register");
                        registerChannel.setFayeListener(MainActivity.this);
                        registerChannel.connectToServer(null);
                        registerChannel.sendMessage(jsonMsg);
                        isEnterName = true;
                    } else {
                        Log.i("demo-faye", "Send msg");
                        serverPublicChannel.sendMessage(jsonMsg);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Cannot parse to json obj", e);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        publicChannel = new FayeClient(handler, URI.create("http://10.0.2.2:8001/chat"), "/public");
        publicChannel.setFayeListener(this);
        publicChannel.connectToServer(null);

        serverPublicChannel = new FayeClient(handler, URI.create("http://10.0.2.2:8001/chat"), "/server/public");
        publicChannel.setFayeListener(this);
        serverPublicChannel.connectToServer(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void connectedToServer() {

        if (!isEnterName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textMessage.append(Html.fromHtml("<b>Connected</b>"));
                    textMessage.append("\n");
                    textMessage.append(Html.fromHtml("Enter <b>your name</b> to message box."));
                    textMessage.append("\n");
                }
            });
        }
    }

    @Override
    public void disconnectedFromServer() {
        Log.i("demo-faye", "disconnectedFromServer");
    }

    @Override
    public void subscribedToChannel(String subscription) {
        Log.i("demo-faye", "subscribedToChannel: " + subscription);
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        Log.i("demo-faye", "subscriptionFailedWithError: " + error);
    }

    @Override
    public void messageReceived(final JSONObject json) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    textMessage.append(Html.fromHtml(json.getString("text")));
                    textMessage.append("\n");
                    Log.i("demo-faye", "messageReceived: " + json.toString());
                } catch (JSONException e) {
                    Log.e(getClass().getName(), "text attr is not existed", e);
                }
            }
        });
    }
}
