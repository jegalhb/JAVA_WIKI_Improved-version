package Reproject;

import java.io.*;
import java.net.Socket;
import java.util.List;
import javax.swing.*;

public class WikiClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MainWikiFrame mainFrame;
    // 192.168.0.34 에 접속해야해용
    public WikiClient(MainWikiFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public static void main(String[] args) {
        ConceptRepository repo = new ConceptRepository();
        SearchService search = new SearchService(repo);

        SwingUtilities.invokeLater(() -> {
            MainWikiFrame frame = new MainWikiFrame(search, repo);
            frame.setVisible(true);

            // [입력] 실행 시 접속할 IP와 포트를 사용자에게 물어봅니다.
            String serverIp = JOptionPane.showInputDialog(frame, "접속할 서버 IP를 입력하세요:", "localhost");
            String portStr = JOptionPane.showInputDialog(frame, "접속할 포트 번호를 입력하세요:", "9999");

            if (serverIp != null && portStr != null) {
                int port = Integer.parseInt(portStr);
                WikiClient client = new WikiClient(frame);
                frame.setClient(client);
                // [이동] 입력받은 주소와 포트로 연결 시도
                client.start(serverIp, port);
            } else {
                frame.appendChat(">> 접속 정보가 입력되지 않아 오프라인 모드로 시작합니다.");
            }
        });
    }

    public void start(String ip, int port) {
        new Thread(() -> {
            try {
                // [이동] 지정된 주소와 포트로 소켓을 생성합니다.
                socket = new Socket(ip, port);

                // 스트림 생성 순서 및 헤더 방출 (안정성 확보)
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                mainFrame.appendChat(">> 서버(" + ip + ":" + port + ") 연결 성공!");

                while (true) {
                    String type = in.readUTF();
                    if (type.equals("REFRESH")) {
                        send("LIST", null);
                    } else if (type.equals("CHAT_MSG")) {
                        String msg = in.readUTF();
                        mainFrame.appendChat(msg);
                    } else if (type.equals("LIST_DATA")) {
                        Object data = in.readObject();
                        if (data instanceof List) {
                            mainFrame.updateList((List<Concept>) data);
                        }
                    }
                }
            } catch (EOFException e) {
                mainFrame.appendChat(">> [연결 종료] 서버와 통신이 끊어졌습니다.");
            } catch (Exception e) {
                mainFrame.appendChat(">> 서버 연결 실패 (접속 정보를 확인해주세요)");
            }
        }).start();
    }

    public void send(String command, Object data) {
        try {
            if (out == null) return;
            out.writeUTF(command);
            if (data instanceof Concept) out.writeObject(data);
            else if (data instanceof String) out.writeUTF((String) data);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}