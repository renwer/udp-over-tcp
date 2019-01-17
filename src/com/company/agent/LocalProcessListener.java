package com.company.agent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Arrays;

public class LocalProcessListener implements Runnable {

    //listen for packets from the local processes
    private DatagramSocket datagramSocket;
    //this will pass plaintext datagram data to the agent
    private BufferedWriter datagramWriter;

    LocalProcessListener(Socket socket, int portNumber) {
        try {
            this.datagramSocket = new DatagramSocket(portNumber);
            this.datagramWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Couldn't start listening on the specified port, exiting the program.");
            System.exit(-1);
        }
    }

    //Listens for incoming UDP packets and passes them to the relay
    @Override
    public void run() {
        System.out.println("Starting a listener process on port " + datagramSocket.getLocalPort());
        byte[] receive = new byte[1000];
        DatagramPacket packet = new DatagramPacket(receive, receive.length);

        while (true) {
            try {
                datagramSocket.receive(packet);
                System.out.println("Received from local process: " + packet.toString() + ", forwarding this data....");
                System.out.println("PORT:" + packet.getPort() + "&" + Arrays.toString(packet.getData()));
                datagramWriter.write("PORT:" + packet.getPort() + "&" + Arrays.toString(packet.getData())+"\n");
                datagramWriter.flush();
            } catch (IOException e) {
                System.out.println("Error while receiving data from the local process");
                e.printStackTrace();
            }

        }
    }
}
