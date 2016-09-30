package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

class tcpserver {

	public static void main(String args[]) throws Exception {
		boolean on = true;
		// Folder to scan for files
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		// Setup reader for user input (in terminal).
		ServerSocket listenSocket = new ServerSocket();
		// Continue prompting user for new port if binding is unsuccessful
		while (listenSocket.isBound() == false) {
			try {
				System.out.println("Enter a port for the server: ");
				int listenPort = Integer.parseInt(inFromUser.readLine());
				listenSocket = new ServerSocket(listenPort);
			} catch (BindException b) {
				System.out.println("Port is already in use. Try a new port.");
			}

		}
		// Maybe change to check if latest input is /quit
		while (on) {
			// Accept new connections.
			// Create a new serverHandler instance for each connection.
			Socket clientSocket = listenSocket.accept();

			Runnable r = new ServerHandler(clientSocket);
			Thread t = new Thread(r);
			t.start();
		}

	}
}
