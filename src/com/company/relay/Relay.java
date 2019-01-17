package com.company.relay;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Relay {

    private static final int AGENT_LISTENING_PORT = 12345;

    private static BufferedWriter agentWriter;
    private static BufferedReader agentReader;

    private static ServerSocket serverSocket;
    private static Socket agentSocket;
    private static Socket remoteHostSocket;

    private static BufferedWriter remoteHostWriter;
    private static BufferedReader remoteHostReader;

    public static void main(String[] args) {
        init();
        setUpTCPTunnel();
        listenForAgentTraffic();
        listenForRemoteHostTraffic();
    }

    //Starts the server socket on a hardcoded port, accepts connection from the agent
    //initializes i/o streams
    private static void init() {
        try {
            serverSocket = new ServerSocket(AGENT_LISTENING_PORT);
            System.out.println("Waiting for incoming agent connection...");
            agentSocket = serverSocket.accept();
            System.out.println("Successfully connected with an agent!");
        } catch (IOException e) {
            System.out.println("Error while establishing connection with an agent.");
            System.exit(-1);
        }
    }
    //Accepts remote host address, initiates the connection with remote host (i.e. sets up the actual TCP tunnel)
    private static void setUpTCPTunnel() {
        try {
            agentWriter = new BufferedWriter(new OutputStreamWriter(agentSocket.getOutputStream()));
            agentReader = new BufferedReader(new InputStreamReader(agentSocket.getInputStream()));

            System.out.println("Waiting for parameters to accept...");
            String parameters = agentReader.readLine();
            System.out.println("parameters: " + parameters);

            String[] addressAndPorts = parameters.split(" ");
            System.out.println("Initializing the TCP tunnel with " + addressAndPorts[0] + " on port " + addressAndPorts[1]);
            remoteHostSocket = new Socket(InetAddress.getByName(addressAndPorts[0]), Integer.parseInt(addressAndPorts[1]));
            remoteHostSocket.setTcpNoDelay(true);
            remoteHostWriter = new BufferedWriter(new OutputStreamWriter(remoteHostSocket.getOutputStream()));
            remoteHostReader = new BufferedReader(new InputStreamReader(remoteHostSocket.getInputStream()));
            System.out.println("TCP tunnel successfully opened.");
        } catch (Exception e) {
            System.out.println("Error while initializing the TCP tunnel: could not connect to the remote host.");
        }
    }

    //Listens for incoming traffic from the remote host, forwards it to the agent
    private static void listenForRemoteHostTraffic() {
        new Thread(new RemoteHostTrafficHandler(remoteHostReader, agentWriter)).start();
    }

    //Listens for incoming traffic from the agent, forwards it to the remote host
    private static void listenForAgentTraffic() {
        new Thread(new AgentTrafficHandler(agentReader, remoteHostWriter)).start();
    }



}
