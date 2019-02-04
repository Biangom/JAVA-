package com.kss;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// User정보를 담는 클래스
public class User {
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public User(Socket s, ObjectInputStream ois, ObjectOutputStream oos) {
		this.socket = s;
		this.ois = ois;
		this.oos = oos;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}
}
