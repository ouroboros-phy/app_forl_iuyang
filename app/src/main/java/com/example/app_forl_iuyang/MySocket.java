package com.example.app_forl_iuyang;
import android.app.Application;

import java.net.Socket;

public class MySocket extends Application {
    Socket socket = null;

    public Socket getSocket() {
        System.out.println(socket);
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        System.out.println(socket);
    }
    public void print_sock(){
        if (socket == null){
            System.out.println("null");}
        else {
        System.out.println(this.socket);
    }}
}
