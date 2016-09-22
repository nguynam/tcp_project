package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

class tcpclient {

	public static void main(String args[]) throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter server address: ");
		String address = inFromUser.readLine();
		System.out.println("Enter server port: ");
		int port = Integer.parseInt(inFromUser.readLine());

		Socket clientSocket = new Socket(address, port);
		// creates a socket with address and port and attempts to make
		// connection.
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		System.out.println("Enter a message: ");
		String message = inFromUser.readLine();
		outToServer.writeBytes(message + '\n');

		// Recieve message from server:
		System.out.println("Server replied with: " + inFromServer.readLine());
		clientSocket.close();

	}

}