package com.company.agent;

import com.company.relay.RemoteHostTrafficHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class Agent {

    private static Socket agentSocket;

    private static BufferedReader relayReader;
    private static BufferedWriter relayWriter;

    private static String[] listeningPorts;


    public static void main(String[] args) throws Exception {

        listeningPorts = Arrays.copyOfRange(args, 2, args.length);

        initRelay(args[0]);
        initListeners(listeningPorts);
        Thread.sleep(200);
        sendParameters(args[1]);
        listenForRelayResponces();
    }

    //Attempts to connect to the relay with it's address given as the first command line argument
    //and port number known beforehand
    private static void initRelay(String relayAddress) {
        try {
            System.out.println("Establishing connection with the relay...");
            agentSocket = new Socket(InetAddress.getByName(relayAddress), 12345);
            agentSocket.setTcpNoDelay(true);

            relayWriter = new BufferedWriter(new OutputStreamWriter(agentSocket.getOutputStream()));
            relayReader = new BufferedReader(new InputStreamReader(agentSocket.getInputStream()));

            System.out.println("Connection with the relay established successfully.");
        } catch (IOException e) {
            System.out.println("Error while establishing connection with the relay on a given address.");
            System.exit(-1);
        }
    }

    //starts the listener processes on port numbers provided as command line args
    private static void initListeners(String[] portNumbers) {
        for(int i = 0; i < portNumbers.length; i++) {
            new Thread(new LocalProcessListener(agentSocket, Integer.parseInt(portNumbers[i]))).start();
        }
    }


    //Provides the remote host's address and the port for receiving data back as parameters to the relay
    private static void sendParameters(String remoteHostAddress) {
        try {
            System.out.println("Sending the remote host address to the relay..." + remoteHostAddress);
            relayWriter.write(remoteHostAddress + " " + 12433+"\n");
            relayWriter.flush();
            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println("Error sending the remote host address to the relay.");
            System.exit(-1);
        }
    }

    private static void listenForRelayResponces() {
        new Thread(new RelayTrafficHandler(relayReader)).start();
    }
}
