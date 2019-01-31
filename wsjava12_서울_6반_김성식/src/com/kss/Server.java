package com.kss;

import java.io.*;
import java.net.*;

// 할일 3가지

// 서버소켓 생성
// accept 기다렸다가 받고
// waiting

public class Server {

	public ServerSocket svrSocket = null;
	public Socket socket = null;
	public InputStream inputStream = null;
	public OutputStream outputStream = null;
	public DataInputStream dis = null;
	public DataOutputStream dos = null;
	public String message;

	public Server() {
		// ServerSocket생성
		try {
			svrSocket = new ServerSocket(1056);
			System.out.println("waiting client...\n"); // accept();
			socket = svrSocket.accept();
			
			// accept();
			System.out.println("\n클라이언트와 연결!\n");
			
			
			
			// io stream
			inputStream = socket.getInputStream();
			dis = new DataInputStream(inputStream);
			outputStream = socket.getOutputStream();
			dos = new DataOutputStream(outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readSocket() {
		try {
			message = dis.readUTF();
			System.out.println(message);

		} catch (UnknownHostException e) {
			System.out.println("에러 : 서버를 찾을 수 없습니다." + e);

		} catch (IOException e) {
			System.out.println("에러 : I/O 에러가 발생했습니다." + e);
			try {
				dis.close();
				dos.close();
				socket.close();
				svrSocket.close(); // *****
			} catch (Exception a) {
			}
			System.exit(0); // 정상 종료
		}
	}

	public void writeSocket() {
		try {
			dos.writeUTF(message + "\n");
		} catch (UnknownHostException e) {
			System.out.println("에러 : 서버를 찾을 수 없습니다." + e);

		} catch (IOException e) {
			System.out.println("에러 : I/O 에러가 발생했습니다." + e);
		}
	}

	public static void main(String args[]) {
		Server svr;
		svr = new Server();
		for (;;) {
			svr.readSocket();
			svr.writeSocket();
		}
	}
}
