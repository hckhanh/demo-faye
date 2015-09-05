package com.demo.demofayechat.channel;

import android.os.Handler;

import com.saulpower.fayeclient.FayeClient;

import org.json.JSONObject;

import java.net.URI;

/**
 * Created by hckhanh on 05/09/2015.
 */
public class Channel implements FayeClient.FayeListener {

    protected FayeClient fayeClient;
    protected JSONObject extensions;

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
        fayeClient.connectToServer(extensions);
    }

    public void disconnect() {
        fayeClient.closeWebSocketConnection();
    }

    public void sendMessage(JSONObject jsonMsg) {
        fayeClient.sendMessage(jsonMsg);
    }

    @Override
    public void connectedToServer() {
        if (onConnectedListener != null)
            onConnectedListener.onConnected();
    }

    @Override
    public void disconnectedFromServer() {
        if (onDisconnectedListener != null)
            onDisconnectedListener.onDisconnected();
    }

    @Override
    public void subscribedToChannel(String subscription) {
        if (onSubscribedListener != null)
            onSubscribedListener.onSubscribed(subscription);
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        if (onSubscribeFailedListener != null)
            onSubscribeFailedListener.onSubscribeFailed(error);
    }

    @Override
    public void messageReceived(JSONObject message) {
        if (onMessageReceivedListener != null)
            onMessageReceivedListener.onMessageReceived(message);
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
