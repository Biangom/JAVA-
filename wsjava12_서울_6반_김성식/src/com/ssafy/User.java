package com.ssafy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class User {
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	
	
	public User(Socket s, ObjectInputStream ois, ObjectOutputStream oos) {
		this.socket = s;
		this.ois = ois;
		this.oos = oos;
	}
	public Socket getS() {
		return socket;
	}
	public void setS(Socket s) {
		this.socket = s;
	}
	public ObjectInputStream getOis() {
		return ois;
	}
	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}
	public ObjectOutputStream get() {
		return oos;
	}
	public void set(ObjectOutputStream oos ) {
		this.oos = oos;
	}
}
