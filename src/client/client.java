package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

class tcpclient {

	public static void main(String args[]) throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter server address: ");
		String address = inFromUser.readLine();
		System.out.println("Enter server port: ");
		int port = Integer.parseInt(inFromUser.readLine());
		if(port > 65535){
			System.out.println("Invalid port number.");
		}

		try{
			Socket clientSocket = new Socket(address, port);
		
			// creates a socket with address and port and attempts to make
			// connection.
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Enter a file name: ");
			String fileName = inFromUser.readLine();
			// Capture expected file name
			outToServer.writeBytes(fileName + '\n');
			// send request for file
			// Create file input stream
			byte[] buffer = new byte[1024];
			InputStream inStream = clientSocket.getInputStream();
			
			int errorCheck = inStream.read();
			//Read first byte to check for error.
			//Error check = 2 signifies unsuccessful file search.
			//Error check = 1 signifies successful file search.
			if(errorCheck != 2){
				//Read input stream into buffer, then write current buffer state to file.
				FileOutputStream fileIn = new FileOutputStream(fileName);
				int count;
				while ((count = inStream.read(buffer, 0, 1024)) != -1) {
					fileIn.write(buffer);
				}
				fileIn.close();
			}else{
				System.out.println("File not found");
			
			}
			inFromServer.close();
			outToServer.close();
			clientSocket.close();
		}
		catch(Exception e){
			System.out.println("Could not connect to server.");
		}

	}

}