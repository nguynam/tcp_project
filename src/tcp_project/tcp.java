package tcp_project;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class tcpserver {

	public static void main(String args[]) throws Exception {
		final String DIRECTORY = "src/files";
		System.out.println("Enter a port for the server: ");
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		int listenPort = Integer.parseInt(inFromUser.readLine());
		ServerSocket listenSocket = new ServerSocket(listenPort);
		Map<String,File> fileMap = scanFolder(DIRECTORY);
		//Scan directory for files
		// Does not automatically attempt to connect (since its the server's
		// socket).
		while(true){
			Socket clientSocket = listenSocket.accept();
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String message = inFromClient.readLine();
			System.out.println("The client said " + message);

			// Sending message back
			outToClient.writeBytes(message + '\n');
		}
	}

	private static Map<String, File> scanFolder(String dir) {
		// TODO Auto-generated method stub
		Map<String, File> folderMap = new HashMap<String, File>();
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        folderMap.put(file.getName(), file);
		    }
		}
		return folderMap;
	}



}


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