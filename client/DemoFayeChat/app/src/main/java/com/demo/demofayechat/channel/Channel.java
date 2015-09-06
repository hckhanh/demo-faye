package com.demo.demofayechat.channel;

import android.os.Handler;
import android.util.Log;

import com.saulpower.fayeclient.FayeClient;

import org.json.JSONObject;

import java.net.URI;

/**
 * Created by hckhanh on 05/09/2015.
 */
public class Channel implements FayeClient.FayeListener {

    protected FayeClient fayeClient;
    protected JSONObject extensions;
    protected boolean isConnected = false;

    OnConnectedListener onConnectedListener;

    OnDisconnectedListener onDisconnectedListener;

    OnSubscribedListener onSubscribedListener;

    OnSubscribeFailedListener onSubscribeFailedListener;

    OnMessageReceivedListener onMessageReceivedListener;

    public Channel(Handler handler, String uri, String channel, JSONObject extensions) {
        this.extensions = extensions;

        fayeClient = new FayeClient(handler, URI.create(uri), channel);
        fayeClient.setFayeListener(this);
    }

    public void connect() {
        if (isConnected) return;

        fayeClient.connectToServer(extensions);
        isConnected = true;
    }

    public void disconnect() {
        if (!isConnected) return;

        fayeClient.unsubscribe();
        fayeClient.disconnectFromServer();
        fayeClient.closeWebSocketConnection();
        fayeClient.setFayeListener(null);
        isConnected = false;
    }

    public void sendMessage(JSONObject jsonMsg) {
        fayeClient.sendMessage(jsonMsg);
    }

    public void unsubscribe() {
        fayeClient.unsubscribe();
    }

    @Override
    public void connectedToServer() {
        if (onConnectedListener != null)
            onConnectedListener.onConnected();

        Log.i("Channel", "connectedToServer");
    }

    @Override
    public void disconnectedFromServer() {
        if (onDisconnectedListener != null)
            onDisconnectedListener.onDisconnected();

        Log.i("Channel", "disconnectedFromServer");
    }

    @Override
    public void subscribedToChannel(String subscription) {
        if (onSubscribedListener != null)
            onSubscribedListener.onSubscribed(subscription);

        Log.i("Channel", "subscribedToChannel");
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        if (onSubscribeFailedListener != null)
            onSubscribeFailedListener.onSubscribeFailed(error);

        Log.i("Channel", "subscriptionFailedWithError");
    }

    @Override
    public void messageReceived(JSONObject message) {
        if (onMessageReceivedListener != null)
            onMessageReceivedListener.onMessageReceived(message);

        Log.i("Channel", "messageReceived");
    }

    public void setOnConnectedListener(OnConnectedListener onConnectedListener) {
        this.onConnectedListener = onConnectedListener;
    }

    public void setOnDisconnectedListener(OnDisconnectedListener onDisconnectedListener) {
        this.onDisconnectedListener = onDisconnectedListener;
    }

    public void setOnSubscribedListener(OnSubscribedListener onSubscribedListener) {
        this.onSubscribedListener = onSubscribedListener;
    }

    public void setOnSubscribeFailedListener(OnSubscribeFailedListener onSubscribeFailedListener) {
        this.onSubscribeFailedListener = onSubscribeFailedListener;
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener) {
        this.onMessageReceivedListener = onMessageReceivedListener;
    }

    public interface OnConnectedListener {
        void onConnected();
    }

    public interface OnDisconnectedListener {
        void onDisconnected();
    }

    public interface OnSubscribedListener {
        void onSubscribed(String subscription);
    }

    public interface OnSubscribeFailedListener {
        void onSubscribeFailed(String error);
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(JSONObject message);
    }

}
