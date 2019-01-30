package com.ssafy;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient implements ActionListener {
	
	
	OutputStream outputStream = null;
	InputStream inputStream = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	Socket socket = null;
	String msg;
	String uId = "김성식";

	
	Frame f;
	Panel p;
	TextArea ta;
	TextField tf;
	
//	class ChatClientThread

	public ChatClient() {
		makeGUI(); // 화면 만들기
		connect(); // network 준비작업
	}

	public void makeGUI() {
		
		f = new Frame("카카오톡");
		f.setBounds(600, 100, 300, 400);
		f.setLayout(new BorderLayout()); // 원래 Frame의 기본 매니저가 BorderLayout임

		ta = new TextArea(); // 여러 줄의 글자를 입력 받기도하고, 출력할 수 도 있는 Component
		ta.setEditable(false); // TextArea 수정못하게
		f.add(ta, BorderLayout.CENTER); // 두번쨰 인자는 어디부분에 넣을 지를 정하는 것이다.
		
		tf = new TextField(); // 한줄에 글자를 입력, 출력하는 컴포넌트
		f.add(tf, BorderLayout.SOUTH);
		
		// 오른쪽에 Panel로 묶어서 넣겠다
		p = new Panel(); // 동쪽 영역에 들어갈 묶음(덩어리)
//		p.setLayout(new BorderLayout());
		p.setLayout(new GridLayout(2,0));
		
		Button bSend = new Button("Send");
//		bSend.setSize(50, 30);
		Button bExit = new Button("Exit");
//		bExit.setSize(100, 20);
		
		p.add(bSend);
		p.add(bExit);
		
		f.add(p, BorderLayout.EAST);
		
		f.setVisible(true); // 화면에 보여주는 작업을 마지막에 한다
		
		
		// 텍스트 필드, 텍스트 입력창
		tf.addActionListener(new ActionListener() {
			// 엔터키 눌렀을 때
			@Override
			public void actionPerformed(ActionEvent e) { // 텍스트 필드에서 엔터키 입력시 호출되는 콜백 메서드
				String str = tf.getText(); // 문자열 읽어옴
				String sendStr;
				
				// 서버 전송
				sendStr = uId + ">>" + str;
				try {
					oos.writeObject((Object)sendStr);
//					ta.append("김성식: "+str+"\n"); // 텍스트 에어리어에 추가
					tf.setText(""); // 글자 지우기
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}
		});
		
		// send 눌렀을 때
		bSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = tf.getText(); // 문자열 읽어옴
				String sendStr;
				
				// 서버 전송
				sendStr = uId + ">>" + str;
				try {
					oos.writeObject((Object)sendStr);
//					ta.append("김성식: "+str+"\n"); // 텍스트 에어리어에 추가
					tf.setText(""); // 글자 지우기
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}
		});
		bExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // 버튼 클릭시 호출되는 콜백 메서드
				f.dispose(); // f는 외부의 지역번수인데 어떻게 호출?
				System.exit(0);
				// 자바 1.7부터 가능했다고함. 늦은실행이 지원이 돼서 외부 지역변수를 임시변수로 저장?
			}
		});
		
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { // 창 닫기 버튼 클릭 시 호출되는 콜백 메서드
				try {
					String str = uId+"퇴장@";
					oos.writeObject(str);
					f.dispose(); // 창을 닫기
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public void connect() {
		System.out.println("connect 시도");
		try {
			// Socket 생성
			socket = new Socket("localhost", 1056);
			outputStream = socket.getOutputStream();
			oos = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			ois = new ObjectInputStream(inputStream);
			oos.writeObject(uId+"입장@");

			// io Stream 생성

			// stream 준비

		} catch (Exception e) {
			System.out.println("에러가 발생했습니다." + e);
		}
		System.out.println("connect 완료");
	}

// 사건이 일어날때 자동 실행되는 메소드
	public void actionPerformed(ActionEvent e) {
		try {
			oos.writeObject(tf.getText()); // tf의 내용 서버쪽으로 보내기
			ta.append((String) ois.readObject()); // 서버에서 되돌아 온 내용을 자신의 ta에 붙이기
			tf.setText("");
		} catch (Exception except) {
		}
	}
	
	public void readSocket() {
		
		try {
			
			//System.out.println("123");
			String message = (String) ois.readObject();
			ta.append(message);
			System.out.print(message);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static void main(String argv[]) {
		ChatClient o = new ChatClient();
		for(;;) {
			o.readSocket();
		}
	}
}


