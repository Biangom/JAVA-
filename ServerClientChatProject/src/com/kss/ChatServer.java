package com.kss;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ChatServer {
	ArrayList<User> users = new ArrayList<User>();
	int port = 1056;

	public ServerSocket svrSocket = null;		// 서버 소켓 변수
	public InputStream inputStream = null;		// 클라이언트 소켓의 입력 스트림 변수
	public OutputStream outputStream = null;	// 클라이언트 소켓의 출력 스트림 변수
	public ObjectInputStream ois = null;		// 객체 전송 스트림
	public ObjectOutputStream oos = null;		// 객체 수신 스트림
	static public String message;				// 메시지 저장 변수
	
	public ChatServer() { }
	
	// 서버 스레드 정의
	class ChatServerThread extends Thread {
		public ObjectInputStream ois;
		User user;
		
		ChatServerThread(User u) {
			this.user = u;		
		}
		
		public int readSocket() {
			// read에서 Exception이 나면
			// 채팅방 사용자가 나갔다는 의미이다. 따라서 -1을 반환함으로써
			// 서버 쓰레드를 종료시키는 조건을 만든다.
			try {
				// 해당 클라이언트로 부터 메시지를 읽는다.
				message = (String) this.user.getOis().readObject();
				System.out.println(message);
				return 0; // 제대로 보냈다면 0을 반환
			} 
			catch (Exception e) {
				try {
					System.out.println("클라이언트 퇴장!");
					this.user.getSocket().close();
					
					// remove client
					for (int i = 0; i < users.size(); i++) {
						if( users.get(i).equals(this.user)) users.remove(this.user);
					}
					System.out.println("--------- 남은 인원 :" + users.size());
					return -1; // Exception 발생하면 -1
				} catch (Exception a) {
					a.printStackTrace();
				}
				return -1;
			}
		} // end of readSocket()
		
		// 쓰레드 실행 부분
		public void run() {
			for(;;) {
				if( readSocket() == -1) break;
				broadcast();
			}
		} // end of run
		
		
	} // end of Thread class
	
	// 모든 클라이언트에게 메시지를 전달한다.
	public void broadcast() {
		int index = 0;
		try {
			// 등록된 클라이언트들에게 메시지를 뿌려준다.
			for (int i = 0; i < users.size(); i++) {
				users.get(i).getOos().writeObject(message + "\n");
				index = i;
			}
		} catch (UnknownHostException e) {
			System.out.println("에러 : 서버를 찾을 수 없습니다." + e);
			
		} catch (IOException e) {
			// 클라이언트 제거
			removeClient(users.get(index).getOis());
		}
	}
	
	public void go() {
		try {
			this.svrSocket = new ServerSocket(this.port);
			
			while(true) {
				System.out.println("waiting client...\n"); // accept();
				Socket socket;
				socket = this.svrSocket.accept();
				System.out.println("connect client!!!\n");
				
				InputStream inputStream;
				OutputStream OutputStream;
				inputStream = socket.getInputStream();
				OutputStream = socket.getOutputStream();
				ObjectInputStream ois = new ObjectInputStream(inputStream);
				ObjectOutputStream oos = new ObjectOutputStream(OutputStream);
				
				User u = new User(socket,ois,oos);
				users.add(u);
				System.out.println("--------- 유저 인원 :" + users.size());
				
				// 클라이언트가 접속하면 새로운 쓰레드를 만든다.
				ChatServerThread t = new ChatServerThread(u);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} // 클라이언트 소켓 생성
	} // end of Go
	
	void removeClient(ObjectInputStream ois) {
		
		for (User user : users) {
			System.out.println(user.getOis());
			
			// ois와 같다면
			if(user.getOis().equals(ois)) {
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
