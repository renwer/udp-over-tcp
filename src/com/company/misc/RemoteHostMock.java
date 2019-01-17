package com.company.misc;


import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

//A simulation of a remote host, running over TCP on port, lets say, 12433
public class RemoteHostMock {

    private static ServerSocket serverSocket;
    private static Socket relaySocket;

    private static BufferedReader relayReader;
    private static BufferedWriter relayWriter;

    public static void main(String[] args) {
        init();
        handleIncomingTraffic();
    }

    private static void init() {
        try {
            serverSocket = new ServerSocket(12433);
            System.out.println("Waiting for a relay to connect...");
            relaySocket = serverSocket.accept();
            relaySocket.setTcpNoDelay(true);
            relayReader = new BufferedReader(new InputStreamReader(relaySocket.getInputStream()));
            relayWriter = new BufferedWriter(new OutputStreamWriter(relaySocket.getOutputStream()));
            System.out.println("Relay connected successfully.");
        } catch (IOException e) {
            System.out.println("Error while trying to run the remote host");
        }
    }

    //The data is being sent perfectly valid. The issues might arise right here, on the supposedly consuming
    //side, which is beyond the scope of the task.
    private static void handleIncomingTraffic() {
        try {
            while (true) {
                String received;
                String[] portAndData;
                if ((received = relayReader.readLine()) != null) {
                    StringBuilder sb = new StringBuilder();
                    portAndData = received.split("&");
                    String[] charCodes = portAndData[1].replace("[", "").replace("]", "").split(", ");

                    for (String s: charCodes) {
                        int i = Integer.parseInt(s);
                        if (i==0) {
                            break;
                        }
                        sb.append((char)i);
                    }

                    System.out.println("Received from the relay: {" + sb.toString() + "},\nsending an acknowledgement back to the relay");

                    //write the response to the relay
                    relayWriter.write(portAndData[0] + "&" + Arrays.toString("Yes, I've received that".getBytes()) + "\n");
                    relayWriter.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while handling traffic in the remote host");
            e.printStackTrace();
        }
    }
}
