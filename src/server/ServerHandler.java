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

	// Directory to scan for files.
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
				String requestedFile = inFromClient.readLine();
				// Capture requested file name.
				if (requestedFile.equals("Exit")) {
					on = false;
					break;
				}

				System.out.println("The client requested " + requestedFile);
				if (fileMap.containsKey(requestedFile)) {
					File fileToSend = fileMap.get(requestedFile);
					// Determine specified file if it exists
					int fileSize = (int) fileToSend.length();
					byte[] byteArray = ByteBuffer.allocate(fileSize + 4).putInt(0, fileSize).array();
					// Create byteArray and allocate enough size for file + 4
					// bytes to send fileSize.
					BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fileToSend));
					fileIn.read(byteArray, 4, fileSize);
					// Read file into byte array skipping first 4 bytes (used
					// for integer)
					outToClient.write(byteArray);
					// Send byteArray on network.
					outToClient.flush();
					fileIn.close();
					// Close file InputStream.
				} else {
					byte[] byteArray = ByteBuffer.allocate(4).putInt(-1).array();
					// Signal an error occurred (file not found).
					outToClient.write(byteArray);
					// Send byteArray (with error).
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
