package com.kss;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient implements ActionListener {
	OutputStream outputStream = null;	// 
	InputStream inputStream = null;		// 
	ObjectOutputStream oos = null;		// oos
	ObjectInputStream ois = null;		// 
	Socket socket = null;
	String uId;			// 유저 이름
	int port = 1056;

	// 채팅창 Frame
	JFrame f; // 채팅창 Frame
	JPanel p;			
	JTextArea ta;		// 채팅창 내용 출력
	JTextField tf;		// 채팅 메시지 입력란
	
	// 이름 입력 Frame
	JFrame inputFrame; 	// 이름 입력 Frame
	JLabel inLabel; 	// 상태메시지 출력 label
	JTextField intf; 	// 이름입력란
	JButton inBt; 		// 등록 버튼
	
	public ChatClient() {
		insertNameGUI();
	}
	
	public void insertNameGUI() {
		inputFrame = new JFrame("사용자 이름 입력");
		inputFrame.setBounds(600,100,350,150);
		inputFrame.setResizable(false); // 사이즈 수정 못하게 막는다.
		inputFrame.setLayout(new GridLayout(3,1));
		JPanel p1 = new JPanel(new FlowLayout());
		JPanel p2 = new JPanel(new FlowLayout());
		JPanel p3 = new JPanel(new FlowLayout());
		
		inLabel = new JLabel("~  사용자 이름 입력   ~");
		intf = new JTextField(8);
		inBt = new JButton("입장하기");
		
		p1.add(inLabel);
		p2.add(intf);
		p3.add(inBt);
		
		inputFrame.add(p1);
		inputFrame.add(p2);
		inputFrame.add(p3);
		inputFrame.setVisible(true); // 화면
	
		inputFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) { // 창 닫기 버튼 클릭 시 호출되는 콜백 메서드
				inputFrame.dispose();
				System.exit(0);
			}
		});
		
		inBt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uId = intf.getText();
				if(uId.equals("")) {
					inLabel.setText("이름을 입력해주셔야죠  !");
					return;
				}
				inputFrame.dispose();
				chatGUI();
				connect();
			}
		});
		
		intf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uId = intf.getText();
				if(uId.equals("")) {
					inLabel.setText("이름을 입력해주셔야죠  !");
					return;
				}
				inputFrame.dispose();
				chatGUI();
				connect();
			}
		});
	} // end of insertNameGUI

	public void chatGUI() {
		f = new JFrame("카카오톡");
		f.setBounds(600, 100, 400, 400);
		f.setLayout(new BorderLayout()); // 원래 Frame의 기본 매니저가 BorderLayout임
		f.setResizable(false);

		ta = new JTextArea(); 
		ta.setEditable(false);			// TextArea 수정못하게 막음
		f.add(ta, BorderLayout.CENTER); // 중앙에 배치
		
		tf = new JTextField();
		f.add(tf, BorderLayout.SOUTH);	// 남쪽에 배치
		
		p = new JPanel();				// 동쪽 영역에 들어갈 묶음(bSend, bExit);
		p.setLayout(new FlowLayout());
		
		Button bSend = new Button("Send");
		Button bExit = new Button("Exit");
		p.add(bSend);
		p.add(bExit);
		
		f.add(p, BorderLayout.EAST);
		
		f.setVisible(true); // 화면에 보여주는 작업을 마지막에 한다
		
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // 텍스트 필드에서 엔터키 입력시 호출되는 콜백 메서드
				String str = tf.getText(); // 문자열을 읽어옴
				String sendStr;
				
				// 서버에 전송할 메시지 만들기
				sendStr = uId + ">>" + str;
				try {
					tf.setText(""); // 글자 지우기
					oos.writeObject((Object)sendStr); // 서버로 전송
				} catch (Exception e1) {
					ta.append("\n서버와 연결이 끊어져 채팅을 할 수 없습니다.");
				}	
			}
		});
		
		// send 버튼을 클릭했을 때 실행되는 메서드
		bSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = tf.getText(); // 문자열 읽어옴
				String sendStr;
				
				// 서버 전송
				sendStr = uId + ">>" + str;
				try {
					oos.writeObject((Object)sendStr); // 전송하다.서버로 
					tf.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}
		});
		
		// bExit버튼을 클릭했을 때 실행되는 메소드
		bExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				f.dispose(); // 창을 종료한다.
				System.exit(0);
			}
		});
		
		// 우측상단 창닫기 버튼을 클릭했을 때 실행되는 메서드
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					String str = uId+"퇴장@";
					oos.writeObject(str);
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally { // 무조건 실행하는 부분
					f.dispose(); // 창 닫기
					System.exit(0); // 종료
				}
			}
		});
	} // end of chatGUI

	public void connect() {
		System.out.println("connect 시도");
		try {
			socket = new Socket("localhost", port);
			outputStream = socket.getOutputStream();
			oos = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			ois = new ObjectInputStream(inputStream);
			oos.writeObject(uId+"님이 입장하였습니다@");
			System.out.println("connect 완료");
		} catch (ConnectException e) {
			ta.setText("서버와 연결을 실패하였습니다.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 사건이 일어날때 자동 실행되는 메소드
	public void actionPerformed(ActionEvent e) {
		try {
			oos.writeObject(tf.getText());			// tf의 내용 서버쪽으로 보내기
			ta.append((String) ois.readObject());	// 서버에서 되돌아 온 내용을 자신의 ta(채팅창)에 붙이기
			tf.setText("");
		} catch (Exception except) {
			ta.append("서버와 연결이 끊어져 채팅을 할 수 없습니다.\n");
		}
	}
	
	// ois 으로 부터 read한다.
	public void readSocket() {
		try {
			String message = (String) ois.readObject();
			ta.append(message);
			System.out.print(message);
		} catch (Exception e) {
			ta.append("서버 수신 실패");
		}
	}

	public static void main(String argv[]) {
		ChatClient o = new ChatClient(); // 클라이언트 생성
		for(;;) {
			o.readSocket(); // 서버에서 오는 정보를 계속 읽어들인다.
		}
	} // end of main
} // end of class
