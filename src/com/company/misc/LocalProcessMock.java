package com.company.misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class LocalProcessMock {

    public static void main(String[] args) throws Exception {

        //TODO: listen for incoming data from the relay traffic handler along with sending

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Which port does the agent run on?");
        int port = Integer.parseInt(reader.readLine());
        DatagramSocket socket = new DatagramSocket();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        byte[] receive = new byte[1000];
                        DatagramPacket packet = new DatagramPacket(receive, receive.length);
                        socket.receive(packet);


                        System.out.println("Response: " + Arrays.toString(packet.getData()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("Enter some string to be sent over the tunnel...");
        while (true) {
            String data = reader.readLine();
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getLocalHost(), port);
            socket.send(packet);
        }

    }
}
