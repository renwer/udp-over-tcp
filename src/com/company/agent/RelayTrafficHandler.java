package com.company.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RelayTrafficHandler implements Runnable {

    BufferedReader relayReader;
    DatagramSocket sendingSocket;

    public RelayTrafficHandler(BufferedReader relayReader) {
        this.relayReader = relayReader;
        try {
            sendingSocket = new DatagramSocket();
        } catch (IOException e) {
            //...
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String responseString;
                if ((responseString = relayReader.readLine()) != null) {
                    String[] portAndData = responseString.split("&");
                    int port = Integer.parseInt(portAndData[0].split(":")[1]);
                    String data = portAndData[1];
                    DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getLocalHost(), port);
                    sendingSocket.send(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
