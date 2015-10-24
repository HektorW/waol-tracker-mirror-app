package com.waol.trackermirror.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class TcpClient extends Thread {

    private String remoteIpAddress;
    private int port;
    private OnMessageReceived messageListener = null;

    private Socket socket;
    private PrintWriter bufferOut;
    private BufferedReader bufferedReader;
    private String incoming;

    private boolean isRunning = false;

    public TcpClient(String remoteIpAddress, int port, OnMessageReceived messageListener) {
        this.remoteIpAddress = remoteIpAddress;
        this.port = port;
        this.messageListener = messageListener;
    }

    public void sendData(String data) throws IOException {
        if(bufferOut != null && isRunning){
            bufferOut.println(data);
            bufferOut.flush();
        }
    }

    public void closeSocket() throws IOException {
        isRunning = false;

        if(bufferOut != null){
            bufferOut.flush();
            bufferOut.close();
        }

        bufferOut = null;
        this.socket.close();
    }

    public void run() {
        this.isRunning = true;

        try{
            this.socket = new Socket(remoteIpAddress, port);
            this.bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (isRunning){
                try{
                    incoming = bufferedReader.readLine();
                    if(incoming != null && messageListener != null){
                        messageListener.messageReceived(incoming);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}
