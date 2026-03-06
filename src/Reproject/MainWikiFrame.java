package Reproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    public MainWikiFrame(SearchService searchService, ConceptRepository repository) {
        this.searchService = searchService;
        this.repository = repository;

        setTitle("Java Wiki - 실시간 협업 자바 학습 시스템");
        setSize(1100, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initTopPanel();
        initCenterPanel();
        initStatusBar();

        updateList(repository.findAll());
        setLocationRelativeTo(null);
    }

    public void setClient(WikiClient client) {
        this.client = client;
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
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // 엔터로 검색시 가능하도록
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
        JPanel filterPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        filterPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        String[] categories = {"전체", "기초", "중급", "고급", "메소드"};
        for (String cat : categories) {
            JButton btn = new JButton(cat);
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
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
                client.send("CHAT", "[나]: " + msg);
                chatInput.setText("");
            }
        });

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        rightPanel.add(chatPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerLocation(320);

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
        updateList(searchService.search(searchField.getText()));
    }

    public void updateList(List<Concept> concepts) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (Concept c : concepts) listModel.addElement(c);
        });
    }

    private void initStatusBar() {
        add(new JLabel(" 시스템 가동 중"), BorderLayout.SOUTH);
    }

    public void onDataAdded(Concept c) {
        if (client != null) client.send("ADD", c);
        refreshList();
    }
}