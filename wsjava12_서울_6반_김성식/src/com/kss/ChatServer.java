package com.kss;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

// 할일 3가지

// 서버소켓 생성
// accept 기다렸다가 받고
// waiting


public class ChatServer {
	ArrayList<User> users = new ArrayList<User>();
	int port = 1056;

	public ServerSocket svrSocket = null;
	public InputStream inputStream = null;
	public OutputStream outputStream = null;
	public ObjectInputStream ois = null;
	public ObjectOutputStream oos = null;
	static public String message;
	
	public ChatServer() {

	}
	
	class ChatServerThread extends Thread {
		public ObjectInputStream ois;
		User user;
		
		ChatServerThread(User u) {
			this.user = u;		
		}
		
		public int readSocket() {
			// read에서 Exception이 나면
			// 채팅방 사용자가 나갔다는 의미이다.
			try {
				message = (String) this.user.ois.readObject();
				System.out.println(message);
				return 0;
			} 
			catch (Exception e) {
				try {
					//클라이언트 퇴장
					System.out.println("클라이언트 퇴장!");
					
					//ois.close();
					this.user.socket.close();
					
					for (int i = 0; i < users.size(); i++) {
						if( users.get(i).equals(this.user))
								users.remove(this.user);
					}
					
					System.out.println("남은 인원" + users.size());
					return -1;
				} catch (Exception a) {
					a.printStackTrace();
					System.out.println("123");
				}
				return -1;
			}
		}
		
		public void run() {
			for(;;) {
				System.out.println("<< Server >>");
				if( readSocket() == -1) break;
				broadcast();
			}
		}
	} // end of Thread class
	
	
	public void broadcast() {
		int index = 0;
		try {
			for (int i = 0; i < users.size(); i++) {
				users.get(i).oos.writeObject(message + "\n");
				index = i;
			}
		} catch (UnknownHostException e) {
			System.out.println("에러 : 서버를 찾을 수 없습니다." + e);
			
		} catch (IOException e) {
			// 하나를 제거
			// System.out.println("한명 나감!");
			removeClient(users.get(index).ois);

		}
	}
	
	public void go() {
		try {
			this.svrSocket = new ServerSocket(this.port);
			
			while(true) {
				System.out.println("waiting client...\n"); // accept();
				Socket socket;
				socket = this.svrSocket.accept();
				System.out.println("\n클라이언트와 연결!\n");
				
				InputStream inputStream;
				OutputStream OutputStream;
				inputStream = socket.getInputStream();
				OutputStream = socket.getOutputStream();
				ObjectInputStream ois = new ObjectInputStream(inputStream);
				ObjectOutputStream oos = new ObjectOutputStream(OutputStream);
				
				User u = new User(socket,ois,oos);
				users.add(u);
				System.out.println("유저 인원 :" + users.size());
				
				// 클라이언트가 접속하면 새로운 쓰레드를 만든다.
				ChatServerThread t = new ChatServerThread(u);
				t.start();
				System.out.println("쓰레드 생성");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} // 클라이언트 소켓 생성
	} // end of Go
	
	void removeClient(ObjectInputStream ois) {
		
		for (User user : users) {
			System.out.println(user.ois);
			
			// ois와 같다면
			if(user.ois.equals(ois)) {
				System.out.println("123");
				users.remove(user);
			}
		}
	}

	public static void main(String args[])  {
		ChatServer server = new ChatServer();
		server.port = 1056;
		server.go();
	} // end of main
} // end of class
