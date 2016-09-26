package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class tcpserver {

	public static void main(String args[]) throws Exception {
		final String DIRECTORY = "src/files";
		// Folder to scan for files
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		// Setup reader for user input (in terminal).
		ServerSocket listenSocket = new ServerSocket();
		while (listenSocket.isBound() == false) {
			try {
				System.out.println("Enter a port for the server: ");
				int listenPort = Integer.parseInt(inFromUser.readLine());
				listenSocket = new ServerSocket(listenPort);
			} catch (BindException b) {
				System.out.println("Port is already in use. Try a new port.");
			}

		}

		Map<String, File> fileMap = scanFolder(DIRECTORY);
		// Scan directory for files
		// Does not automatically attempt to connect (since its the server's
		// socket).
		Socket clientSocket = listenSocket.accept();
		// Accept 1 client connection.
		// Client now connected
		System.out.println("Client connected.");
		DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		String message = inFromClient.readLine();
		System.out.println("The client requested " + message);
		byte[] byteArray = new byte[1];
		if (fileMap.containsKey(message)) {
			File fileToSend = fileMap.get(message);
			// Determine specified file if it exists
			byteArray = new byte[(int) fileToSend.length() + 1];
			BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend));
			fileIn.read(byteArray, 1, (int) fileToSend.length());
			byteArray[0] = 1;
			// Signifies successful search for file.
			outToClient.write(byteArray);
			// Send files
			outToClient.flush();
			clientSocket.close();
		} else {
			byteArray[0] = 2;
			// Signal an error occured
			outToClient.write(byteArray);
			// Send byte array (with error)
			System.out.println("File not found.");
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
