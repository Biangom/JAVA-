package com.kss;

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
	Socket socket = null;				// 클라이언트 소켓 생성
	String uId;							// 유저 이름
	int port = 1056;					// 포트 번호

	// 채팅창 Frame
	JFrame f; // 채팅창 Frame
	JPanel p;			
	JTextArea ta;		// 채팅창 내용 출력
	JTextField tf;		// 채팅 메시지 입력란
	JLabel lbName;
	
	// 이름 입력 Frame
	JFrame inputFrame; 	// 이름 입력 Frame
	JLabel inLabel; 	// 상태메시지 출력 label
	JTextField intf; 	// 이름입력란
	JButton inBt; 		// 등록 버튼
	
	boolean connectCheck = false;
	
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
				entrance();
			}
		});
		
		intf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				entrance();
			}
		});
	} // end of insertNameGUI
	
	// 채팅방에 입장하기 전에 실행할 메서드
	public void entrance() {
		uId = intf.getText();
		if(uId.equals("")) {
			inLabel.setText("이름을 입력해주셔야죠  !");
			return;
		}
		inputFrame.dispose();
		chatGUI();
		connect();
	}

	// 채팅 GUI를 띄운다.
	public void chatGUI() {
		f = new JFrame("카카오톡");
		f.setBounds(600, 100, 400, 400);
		f.setLayout(new BorderLayout()); // 원래 Frame의 기본 매니저가 BorderLayout임
		f.setResizable(false);

		ta = new JTextArea(); 
		ta.setEditable(false);			// TextArea 수정못하게 막음
		
		tf = new JTextField();
		
		p = new JPanel();				// 동쪽 영역에 들어갈 묶음(bSend, bExit);
		p.setLayout(new FlowLayout());
		JScrollPane scrollpane = new JScrollPane(ta);
		
		Button bSend = new Button("Send");
		Button bExit = new Button("Exit");
		lbName = new JLabel(uId);
		p.add(bSend);
		p.add(bExit);
		
		f.add(lbName, BorderLayout.NORTH);	// 북쪽에 배치
		f.add(scrollpane, BorderLayout.CENTER); // 중앙에 배치
		f.add(tf, BorderLayout.SOUTH);	// 남쪽에 배치
		f.add(p, BorderLayout.EAST);	// 동쪽에 배치
		
		f.setVisible(true); // 화면에 보여주는 작업을 마지막에 한다
		
		// 텍스트 입력에 이벤트가 발생했을 때 실행되는 메서드
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
		
		// bExit버튼을 클릭했을 때 실행되는 메서드
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
					String str = "\""+uId+"\"님이 "+"퇴장하였습니다.";
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

	// 서버와 연결을 시도하는 메서드
	public void connect() {
		System.out.println("connect 시도");
		try {
			socket = new Socket("localhost", port);
			outputStream = socket.getOutputStream();
			oos = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			ois = new ObjectInputStream(inputStream);
			oos.writeObject("\""+uId+"\""+"님이 입장하였습니다.");
			System.out.println("connect 완료");

		} catch (ConnectException e) {
			ta.setText("서버와 연결을 실패하였습니다.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 사건이 일어날때 자동 실행되는 메서드
	public void actionPerformed(ActionEvent e) {
		try {
			oos.writeObject(tf.getText());			// tf의 내용 서버쪽으로 보내기
			ta.append((String) ois.readObject());	// 서버에서 되돌아 온 내용을 자신의 ta(채팅창)에 붙이기
			tf.setText("");
		} catch (Exception except) {
			ta.append("서버와 연결이 끊어져 채팅을 할 수 없습니다.\n");
		}
	}
	
	// 서버로 부터 메시지를 read한다.
	public boolean readSocket() {
		try {
			String message = (String) ois.readObject();
			ta.append(message);
			System.out.print(message);
		} catch (NullPointerException e) {
		} catch (SocketException e) {
			ta.append("서버와 연결이 끊어짐\n");
			return false;
		} catch (Exception e) {
		}
		return true;
	}
	
	// 반복적으로 실행할 메서드
	public void go() {
		while(true) {
			if(!this.readSocket()) break; // 서버에서 오는 정보를 계속 읽어들인다.
		}
	}

	public static void main(String argv[]) {
		ChatClient o = new ChatClient(); // 클라이언트 생성
		o.go();
	} // end of main
} // end of class
