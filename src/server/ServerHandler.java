package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
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
			boolean on = true;
			Map<String, File> fileMap = scanFolder(DIRECTORY);
			// Scan directory for files

			// Client now connected
			System.out.println("Client connected.");
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			while (on) {
				//Get new instance of outputStream to client since it must close to signal end of stream.
				String requestedFile = inFromClient.readLine();
				
				if(requestedFile.equals("Exit")){
					on = false;
					break;
				}
				
				System.out.println("The client requested " + requestedFile);
				if (fileMap.containsKey(requestedFile)) {
					File fileToSend = fileMap.get(requestedFile);
					// Determine specified file if it exists
					int fileSize = (int)fileToSend.length();
					byte[] byteArray = ByteBuffer.allocate(fileSize + 4).putInt(0, fileSize).array();
					BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend));
					fileIn.read(byteArray, 4, fileSize);
					
					// Signifies successful search for file.
					outToClient.write(byteArray);
					outToClient.flush();
					//outToClient.writeChar('\n');
					//Send end of stream signal
					//outToClient.writeBytes("\r\n");
					// Send files
					fileIn.close();
				} else {
					byte[] byteArray = ByteBuffer.allocate(4).putInt(-1).array();
					// Signal an error occurred
					outToClient.write(byteArray);
					outToClient.close();
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
