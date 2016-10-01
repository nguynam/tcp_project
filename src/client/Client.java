package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;

class tcpclient {

	public static void main(String args[]) throws Exception {
		String correctAddress = "127.0.0.1";
		boolean on = true;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter server address: ");
		String address = inFromUser.readLine();
		System.out.println("Enter server port: ");
		int port = Integer.parseInt(inFromUser.readLine());
		if (port > 65535) {
			System.out.println("Invalid port number.");
		}
		if (!address.equals(correctAddress)) {
			System.out.println("Invalid address.");
		}
		Socket clientSocket = new Socket(address, port);
		// Create socket with new connection to server.
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
		// Setup input and output streams
		while (on) {
			try {

				System.out.println("Enter a file name or exit: ");
				String fileName = inFromUser.readLine();
				// Capture expected file name.
				if (fileName.equals("Exit")) {
					on = false;
					clientSocket.close();
					break;
				}
				outToServer.writeBytes(fileName + '\n');
				// send request for file

				byte[] sizeBuffer = new byte[4];
				bis.read(sizeBuffer, 0, 4);
				int sizeCheck = ByteBuffer.wrap(sizeBuffer).getInt();
				// Capture first for bytes as an int - will determine size of
				// file.

				// sizeCheck == -1 signifies error(not found).
				// SizeCheck > 0 signifies size of file.
				if (sizeCheck != -1) {
					byte[] buffer = new byte[1024];
					FileOutputStream fileIn = new FileOutputStream(fileName);
					int totalCount = 0;
					while (totalCount < sizeCheck - 1) {
						int localCount = bis.read(buffer);
						totalCount = totalCount + localCount;
						fileIn.write(buffer, 0, localCount);
					}
					// fill buffer with 1024 bytes and save to
					// file until total bytes read equals file size.

					fileIn.close();
				} else {
					System.out.println("File not found");

				}

			} catch (Exception e) {
				System.out.println("Could not connect to server.");
				break;
			}
		}
	}

}