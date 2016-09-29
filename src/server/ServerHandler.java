package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerHandler implements Runnable {
	Socket clientSocket;
	final String DIRECTORY = "src/files";

	ServerHandler(Socket incomingSocket) {
		clientSocket = incomingSocket;
	}

	@Override
	public void run() {
		try {
			Map<String, File> fileMap = scanFolder(DIRECTORY);
			// Scan directory for files

			// Client now connected
			System.out.println("Client connected.");
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (true) {
				String requestedFile = inFromClient.readLine();
				System.out.println("The client requested " + requestedFile);
				byte[] byteArray = new byte[1];
				if (fileMap.containsKey(requestedFile)) {
					File fileToSend = fileMap.get(requestedFile);
					// Determine specified file if it exists
					byteArray = new byte[(int) fileToSend.length() + 1];
					BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend));
					fileIn.read(byteArray, 1, (int) fileToSend.length());
					byteArray[0] = 1;
					// Signifies successful search for file.
					outToClient.write(byteArray);
					// Send files
					fileIn.close();
					outToClient.flush();
					clientSocket.close();
				} else {
					byteArray[0] = 2;
					// Signal an error occurred
					outToClient.write(byteArray);
					// Send byte array (with error)
					System.out.println("File not found.");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private static Map<String, File> scanFolder(String dir) {
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
