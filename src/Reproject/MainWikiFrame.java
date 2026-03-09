package Reproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWikiFrame extends JFrame {
    private SearchService searchService;
    private ConceptRepository repository;
    private WikiClient client;

    private JScrollPane scrollPane;
    private JList<Concept> resultList;
    private DefaultListModel<Concept> listModel;
    private JTextField searchField;
    private String currentCategory = "전체";

    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel statusLabel;

    public MainWikiFrame(SearchService searchService, ConceptRepository repository) {
        this.searchService = searchService;
        this.repository = repository;

        setTitle("Java Wiki - 실시간 협업 자바 학습 시스템");
        setSize(1100, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initTopPanel();
        initCenterPanel();
        initStatusBar();

        updateList(repository.findAll());
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                if (client == null) {
                    repository.save();
                }
            }
        });
    }

    public void setClient(WikiClient client) {
        this.client = client;
        if (client != null) {
            setStatusText("오프라인 모드");
        }
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        topPanel.setBackground(new Color(236, 240, 241));

        searchField = new JTextField();
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setOpaque(false);

        JButton addBtn = new JButton("지식 추가/수정");
        addBtn.addActionListener(e -> new ConceptEditFrame(this, repository));

        JButton deleteBtn = new JButton("지식 삭제");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> {
            Concept selected = resultList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "삭제할 항목을 먼저 선택해주세요.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "'" + selected.getTitle() + "' 삭제할까요?", "삭제 확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                repository.deleteConcept(selected.getId());
                if (client != null) client.send("DELETE", selected.getId());
                refreshList();
            }
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);

        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("검색");
        // 버튼 클릭과 Enter 입력이 같은 검색 메서드를 타게 해서 동작을 일관되게 유지한다.
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        // 포커스가 검색창 밖에 있어도 Enter가 검색 버튼 액션으로 연결된다.
        getRootPane().setDefaultButton(searchButton);
        topPanel.add(searchButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    public void appendChat(String msg) {
        SwingUtilities.invokeLater(() -> {
            if (chatArea != null) {
                chatArea.append(msg + "\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        });
    }

    public void refreshList() {
        filterList(currentCategory != null ? currentCategory : "전체");
    }

    private void initCenterPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setMinimumSize(new Dimension(420, 0));
        JPanel filterPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        filterPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        String[] categories = {"전체", "기초", "중급", "고급", "메소드"};
        for (String cat : categories) {
            JButton btn = new JButton(cat);
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 11));
            btn.setMargin(new Insets(2, 6, 2, 6));
            btn.addActionListener(e -> {
                currentCategory = cat;
                filterList(cat);
            });
            filterPanel.add(btn);
        }
        leftPanel.add(filterPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) displayDetail(resultList.getSelectedValue());
        });

        leftPanel.add(new JScrollPane(resultList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        scrollPane = new JScrollPane(new JLabel("카테고리를 선택하거나 검색해 주세요.", SwingConstants.CENTER));
        scrollPane.setBorder(BorderFactory.createTitledBorder("상세 지식"));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(0, 200));
        chatPanel.setBorder(BorderFactory.createTitledBorder("실시간 협업 채팅"));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setText(">> 서버 연결 정보를 입력받는 중입니다...\n");

        chatInput = new JTextField();
        chatInput.addActionListener(e -> {
            String msg = chatInput.getText().trim();
            if (!msg.isEmpty() && client != null) {
                // 닉네임 접두사는 WikiClient에서만 붙인다(중복 [나] 방지).
                client.send("CHAT", msg);
                chatInput.setText("");
            }
        });

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        rightPanel.add(chatPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerLocation(420);

        add(splitPane, BorderLayout.CENTER);
    }

    private void displayDetail(Concept selected) {
        if (selected == null) return;

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        JLabel titleLabel = new JLabel(selected.getTitle());
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(titleLabel);
        detailPanel.add(Box.createVerticalStrut(2));

        JLabel catLabel = new JLabel("분류: " + selected.getCategory());
        catLabel.setForeground(new Color(52, 152, 219));
        catLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        detailPanel.add(catLabel);
        detailPanel.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        detailPanel.add(sep);
        detailPanel.add(Box.createVerticalStrut(10));

        for (String line : selected.getDescriptionLines()) {
            JLabel label = new JLabel();
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (line.startsWith("[H2]")) {
                label.setText(line.replace("[H2] ", ""));
                label.setFont(new Font("맑은 고딕", Font.BOLD, 19));
                label.setForeground(new Color(44, 62, 80));
                detailPanel.add(Box.createVerticalStrut(10));
            } else if (line.startsWith("[코드]")) {
                label.setText(line.replace("[코드] ", ""));
                label.setForeground(Color.BLUE);
                label.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            } else {
                label.setText(line.replace("[설명] ", ""));
                label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            }
            detailPanel.add(label);
            detailPanel.add(Box.createVerticalStrut(7));
        }

        detailPanel.add(Box.createVerticalGlue());
        scrollPane.setViewportView(detailPanel);
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    private void filterList(String category) {
        List<Concept> all = repository.findAll();
        listModel.clear();
        for (Concept c : all) {
            if (category.equals("전체")) {
                if (!c.getCategory().equals("메소드")) listModel.addElement(c);
            } else if (c.getCategory().equals(category)) {
                listModel.addElement(c);
            }
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        List<Concept> results = searchService.search(keyword);
        updateList(results);

        if (!keyword.isEmpty() && results.isEmpty()) {
            Concept best = searchService.getBestMatch(keyword);
            if (best != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "검색 결과가 없습니다. 추천어: " + best.getTitle(),
                        "검색 안내",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    public void updateList(List<Concept> concepts) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Concept c : concepts) listModel.addElement(c);
        });
    }

    public void applyServerData(List<Concept> concepts) {
        // 서버의 최신 전체 목록으로 로컬 저장소를 먼저 맞춰야 이후 검색/필터도 최신 데이터 기준이 된다.
        repository.replaceAll(concepts);
        SwingUtilities.invokeLater(() -> {
            String keyword = searchField != null ? searchField.getText().trim() : "";
            // 사용자가 검색 중이면 검색 결과 상태를 유지, 아니면 카테고리 필터 상태를 유지한다.
            if (!keyword.isEmpty()) {
                updateList(searchService.search(keyword));
            } else {
                refreshList();
            }
        });
    }

    private void initStatusBar() {
        statusLabel = new JLabel("오프라인모드");
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setStatusText(String text) {
        if (statusLabel != null) {
            statusLabel.setText(" " + text);
        }
    }

    public void onDataAdded(Concept c) {
        // 오프라인 상태에서도 즉시 반영되도록 로컬 저장소에 먼저 저장한다.
        repository.addConcept(c);
        // 온라인이면 서버에 동일 이벤트를 보내 다른 클라이언트도 갱신되게 한다.
        if (client != null) client.send("ADD", c);
        refreshList();
    }
}