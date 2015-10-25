package com.waol.trackermirror.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class TcpClient extends AsyncTask<String, String, Void> {

    private OnMessageReceived messageListener = null;
    private OnSocketConnect connectionListener = null;

    private Socket socket;
    private SocketAddress socketAddress;
    private PrintWriter bufferOut;
    private BufferedReader bufferIn;
    private String incoming;

    private boolean startedConnect = false;
    private boolean isRunning = false;

    public void setMessageListener(OnMessageReceived messageListener){
        this.messageListener = messageListener;
    }

    public void setConnectionListener(OnSocketConnect connectionListener){
        this.connectionListener = connectionListener;
    }

    public boolean isConnected() {
        if(socket == null)
            return false;

        return socket.isConnected();
    }

    public boolean sendData(String data) {
        if(bufferOut != null && isRunning && isConnected() && !bufferOut.checkError()){
            Log.d("TCPClient", "Send data");
            bufferOut.println(data);
            bufferOut.flush();
            return true;
        }

        return false;
    }

    public void closeSocket() {
        isRunning = false;

        if(bufferOut != null){
            bufferOut.flush();
            bufferOut.close();
        }

        bufferIn = null;
        bufferOut = null;
    }

    public void connect(String ip, int port){
        if(startedConnect){
            return;
        }

        startedConnect = true;
        this.socket = new Socket();
        this.socketAddress = new InetSocketAddress(ip, port);

        // Keep try connect to server until successful connection
//        while (isRunning && !socket.isConnected()) {
//            try{
//                this.socket.connect(socketAddress, 2000);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }

        try{
            this.socket.connect(socketAddress, 60000);
        } catch (Exception e){
            e.printStackTrace();
            startedConnect = false;
            return;
        }

        // Connected to server!
        Log.d("TCPClient", "Connect to server: " + socketAddress.toString());
        if(connectionListener != null){
            this.connectionListener.successfulConnection();
        }

        startedConnect = false;
    }

    @Override
    protected Void doInBackground(String... params) {
        this.isRunning = true;
        Log.d("TCPClient", "DoInBackground");

        try{
            connect(params[0], Integer.parseInt(params[1]));

            try{

                this.bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                this.bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (isRunning && !socket.isClosed()){
                    try{
                        if(bufferIn != null){
                            incoming = bufferIn.readLine();
                            if(incoming != null && messageListener != null){
                                Log.d("TCPClient", "Message received");
                                messageListener.messageReceived(incoming);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }
            finally {
                Log.d("TCPClient", "Close socket");
                this.socket.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    public interface OnSocketConnect {
        void successfulConnection();
    }
}
