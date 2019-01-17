package com.company.relay;

import java.io.*;

public class RemoteHostTrafficHandler implements Runnable {

    BufferedReader remoteHostReader;
    BufferedWriter agentWriter;

    public RemoteHostTrafficHandler(BufferedReader remoteHostReader, BufferedWriter agentWriter) {
        this.agentWriter = agentWriter;
        this.remoteHostReader = remoteHostReader;
    }

    //catch the traffic from the remote host, forward it to the agent
    @Override
    public void run() {
        try {
            while (true) {
                String received = remoteHostReader.readLine();
                    System.out.println("RESPONSE: [" + received + "]\nforwarding to the agent...");
                    agentWriter.write(received+"\n");
                    agentWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("IOException in the remote host traffic handler within the relay.");
        }
    }
}
