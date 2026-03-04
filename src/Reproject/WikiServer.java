package Reproject;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * 중앙 데이터 관리를 담당하는 위키 서버이다.
 * [저장] ConceptRepository를 소유하며 모든 클라이언트의 요청을 처리한다.
 */
public class WikiServer {
    private static final int PORT = 9999;
    private ConceptRepository repository;

    public WikiServer() {
        this.repository = new ConceptRepository(); // 서버 전용 저장소 생성
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=== 자바 위키 서버가 " + PORT + "번 포트에서 가동 중이다 ===");

            while (true) {
                // [입력] 클라이언트의 접속을 기다린다.
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 접속: " + socket.getInetAddress());

                // 각 접속을 개별적으로 처리하기 위해 스레드를 생성한다. (멀티스레드 기초)
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.err.println("서버 가동 중 에러 발생: " + e.getMessage());
        }
    }

    // 클라이언트의 명령을 실제로 처리하는 내부 클래스 (연산 단계)
    private class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                // [연산] 클라이언트가 보낸 명령어를 읽는다.
                String command = in.readUTF();
                System.out.println("받은 명령: " + command);

                switch (command) {
                    case "ADD": // 추가 및 수정 처리
                        Concept newConcept = (Concept) in.readObject();
                        repository.addConcept(newConcept); // [저장] 반영
                        out.writeUTF("SUCCESS");
                        break;

                    case "DELETE": // 삭제 처리
                        String id = in.readUTF();
                        repository.deleteConcept(id); // [저장] 제거
                        out.writeUTF("SUCCESS");
                        break;

                    case "LIST": // 전체 목록 요청 처리
                        List<Concept> allData = repository.findAll();
                        out.writeObject(allData); // [출력] 데이터 전송
                        break;
                }
                out.flush();

            } catch (Exception e) {
                System.err.println("통신 중 에러: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }

    public static void main(String[] args) {
        new WikiServer().start();
    }
}