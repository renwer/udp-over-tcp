package com.company.relay;

import java.io.*;

public class AgentTrafficHandler implements Runnable {

    private BufferedReader agentReader;
    private BufferedWriter remoteHostWriter;

    public AgentTrafficHandler(BufferedReader agentReader, BufferedWriter remoteHostWriter) {
            this.remoteHostWriter = remoteHostWriter;
            this.agentReader = agentReader;
    }

    //catch the traffic from the agent, forward it to the remote host
    @Override
    public void run() {
        try {
            while (true) {
                String received;
                if ((received = agentReader.readLine()) != null) {
                    System.out.println("Received from the agent: {" + received + "}\nforwarding to the remote host...");
                    remoteHostWriter.write(received+"\n");
                    remoteHostWriter.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException in the agent traffic handler within the relay.");
        }
    }
}
