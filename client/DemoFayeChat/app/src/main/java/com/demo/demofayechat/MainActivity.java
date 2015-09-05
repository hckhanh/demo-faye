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

import com.demo.demofayechat.channel.Channel;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity /*implements FayeClient.FayeListener*/ {

    Handler handler = new Handler(Looper.getMainLooper());
    boolean isEnterName = false;

    TextView textMessage;
    Button btnSend;

    Channel registerChannel;
    Channel joinChannel;
    Channel publicChannel;
    Channel publicServerChannel;
    Channel registerWithIdChannel;

    String userId;

    Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainThreadHandler = new Handler();

        btnSend = (Button) findViewById(R.id.btnSend);
        textMessage = (TextView) findViewById(R.id.textMessage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText editMessage = (EditText) findViewById(R.id.editMessage);
                    String msg = editMessage.getText().toString();
                    editMessage.setText("");

                    if (!isEnterName) {
                        final JSONObject jsonMsg = new JSONObject();
                        String sessionId = String.valueOf(System.currentTimeMillis());
                        jsonMsg.put("sessionId", sessionId);
                        jsonMsg.put("username", msg);
                        registerChannel = new Channel(mainThreadHandler, "http://10.0.2.2:8001/chat", "/register", null);
                        registerChannel.setOnSubscribedListener(new Channel.OnSubscribedListener() {
                            @Override
                            public void onSubscribed(String subscription) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        registerChannel.sendMessage(jsonMsg);
                                        isEnterName = true;
                                    }
                                });
                                Log.i("demo-faye", "Sent register information");
                            }
                        });
                        registerChannel.connect();

                        registerWithIdChannel = new Channel(mainThreadHandler, "http://10.0.2.2:8001/chat", "/register/" + sessionId, null);
                        registerWithIdChannel.setOnMessageReceivedListener(new Channel.OnMessageReceivedListener() {
                            @Override
                            public void onMessageReceived(final JSONObject message) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            userId = message.getString("userId");
                                            publicServerChannel = new Channel(mainThreadHandler, "http://10.0.2.2:8001/chat", "/public/server", null);

                                            joinChannel = new Channel(mainThreadHandler, "http://10.0.2.2:8001/chat", "/join", null);
                                            joinChannel.setOnMessageReceivedListener(new Channel.OnMessageReceivedListener() {
                                                @Override
                                                public void onMessageReceived(final JSONObject message) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                textMessage.append(Html.fromHtml(message.getString("text")));
                                                            } catch (JSONException e) {
                                                                Log.e(getClass().getName(), "text attr is not existed", e);
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                            publicServerChannel.connect();
                                            joinChannel.connect();
                                            Log.i("demo-faye", "Received register information");
                                        } catch (JSONException e) {
                                            Log.e(getClass().getName(), "Cannot parse to json obj", e);
                                        }
                                    }
                                });
                            }
                        });
                        registerWithIdChannel.connect();
                    } else {
                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg
                                .put("userId", userId)
                                .put("text", msg);
                        publicServerChannel.sendMessage(jsonMsg);
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

        publicChannel = new Channel(mainThreadHandler, "http://10.0.2.2:8001/chat", "/public", null);
        publicChannel.setOnSubscribedListener(new Channel.OnSubscribedListener() {
            @Override
            public void onSubscribed(String subscription) {
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
        });
        publicChannel.setOnMessageReceivedListener(new Channel.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(final JSONObject message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            textMessage.append(Html.fromHtml(message.getString("text")));
                            textMessage.append("\n");
                            Log.i("demo-faye", "messageReceived: " + message.toString());
                        } catch (JSONException e) {
                            Log.e(getClass().getName(), "text attr is not existed", e);
                        }
                    }
                });
            }
        });

        publicChannel.connect();
        /*publicChannel = new FayeClient(handler, URI.create("http://10.0.2.2:8001/chat"), "/public");
        publicChannel.setFayeListener(this);
        publicChannel.connectToServer(null);

        serverPublicChannel = new FayeClient(handler, URI.create("http://10.0.2.2:8001/chat"), "/server/public");
        publicChannel.setFayeListener(this);
        serverPublicChannel.connectToServer(null);*/
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

    /*@Override
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
    }*/
}
