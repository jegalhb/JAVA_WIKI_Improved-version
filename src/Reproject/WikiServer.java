package Reproject;

import java.io.*;
import java.net.*;
import java.util.*;

public class WikiServer {
    private static final int PORT = 9999;
    private ConceptRepository repository;
    // [수정] 접속자 명단을 관리할 리스트 (멀티스레드 안전 보장)
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public WikiServer() {
        this.repository = new ConceptRepository();
    }

    // [이유] 서버를 실행하기 위한 진입점은 반드시 외부 클래스에 있어야 합니다!
    public static void main(String[] args) {
        new WikiServer().start();
    }

    // [연산] 모든 접속자에게 메시지 중계 (브로드캐스트)
    private void broadcast(String type, Object data) {
        // [중요] 리스트 순회 시 동기화 블록을 사용하여 충돌을 방지합니다.
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(type, data);
            }
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=== 자바 위키 실시간 서버 가동 (" + PORT + ") ===");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("새로운 접속: " + socket.getInetAddress());

                // [저장] 접속한 클라이언트를 명단에 추가한다.
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("서버 에러: " + e.getMessage());
        }
        repository.save();
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // [출력] 서버가 이 클라이언트에게 개별적으로 메시지를 보내는 메서드
        public void sendMessage(String type, Object data) {
            try {
                out.writeUTF(type);
                if (data != null) {
                    if (data instanceof String) {
                        out.writeUTF((String) data);   // String은 writeUTF로 통일
                    } else {
                        out.writeObject(data);          // 나머지(List 등)는 writeObject
                    }
                }
                out.flush();
                out.reset();
            } catch (IOException e) {
                System.err.println("메시지 전송 실패: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                // [중요] 스트림 생성 순서를 클라이언트와 맞춥니다.
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush(); // 생성 즉시 헤더를 밀어내어 교착상태 방지
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    String command = in.readUTF(); // 1. 명령 수신
                    System.out.println("명령 수신: " + command);

                    if (command.equals("ADD")) {
                        Concept c = (Concept) in.readObject();
                        repository.addConcept(c);
                        broadcast("REFRESH", null);
                    } else if (command.equals("DELETE")) {
                        String id = in.readUTF();
                        repository.deleteConcept(id);
                        broadcast("REFRESH", null);
                    } else if (command.equals("LIST")) {
                        out.writeUTF("LIST_DATA");
                        out.writeObject(repository.findAll());
                        out.flush();
                        out.reset();
                    } else if (command.equals("CHAT")) {
                        String msg = in.readUTF();
                        broadcast("CHAT_MSG", msg); // 모든 접속자에게 채팅 중계
                    }
                }
            } catch (Exception e) {
                System.out.println("클라이언트 퇴장: " + socket.getInetAddress());
            } finally {
                clients.remove(this);
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }
}