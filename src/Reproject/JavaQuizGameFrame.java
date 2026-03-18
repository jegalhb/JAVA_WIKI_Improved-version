package Reproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Java 학습 게임 UI 뼈대 프레임.
 * - 상단: 도움말 / 접속자 / 사전 / 게임 시작 / 나가기
 * - 중앙: 라운드 상태 및 문제 보드
 * - 하단: 로그 + 답 입력 // 구조 개선중
 */
public class JavaQuizGameFrame extends JFrame {
    private static final Color BG_GAME = new Color(76, 165, 79);
    private static final Color BG_PATTERN = new Color(93, 179, 96);
    private static final Color PANEL_DARK = new Color(44, 34, 24);
    private static final Color PANEL_GOLD = new Color(206, 165, 78);
    private static final Color ACCENT = new Color(82, 128, 237);
    private static final Icon USER_ICON = new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(24, 24, 24));
            g2.fillOval(x + 5, y + 1, 10, 10);
            g2.fillRoundRect(x + 2, y + 10, 16, 9, 8, 8);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    };

    private final ConceptRepository repository;
    private final SearchService searchService;

    private final JTextArea gameLogArea = new JTextArea();
    private final JTextField answerField = new JTextField();
    private final JTextField dictSearchField = new JTextField();
    private final DefaultListModel<Concept> dictListModel = new DefaultListModel<>();
    private final JList<Concept> dictList = new JList<>(dictListModel);
    private final JTextArea dictDetailArea = new JTextArea();
    private final List<String> connectedUsers = new ArrayList<>();

    public JavaQuizGameFrame(ConceptRepository repository, SearchService searchService) {
        this.repository = repository;
        this.searchService = searchService;

        setTitle("Java Quiz Arena");
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connectedUsers.add("로컬 사용자 (오프라인)");

        JPanel root = new PatternPanel();
        root.setLayout(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(10, 12, 12, 12));
        setContentPane(root);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildCenterPane(), BorderLayout.CENTER);
        root.add(buildBottomPane(), BorderLayout.SOUTH);

        loadDictionary(repository.findAll());
    }

    /**
     * 외부 네트워크 레이어에서 실제 접속자 목록을 전달할 때 사용.
     */
    public void setConnectedUsers(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            connectedUsers.clear();
            if (users == null || users.isEmpty()) {
                connectedUsers.add("접속자 없음");
            } else {
                for (String user : users) {
                    if (user != null && !user.trim().isEmpty()) {
                        connectedUsers.add(user.trim());
                    }
                }
                if (connectedUsers.isEmpty()) {
                    connectedUsers.add("접속자 없음");
                }
            }
        });
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        JButton helpButton = createTopButton("?");
        helpButton.addActionListener(e -> showHowToPlayDialog());

        JButton usersButton = createIconTopButton(USER_ICON);
        usersButton.setToolTipText("현재 접속자 보기");
        usersButton.addActionListener(e -> showConnectedUsersDialog());

        JButton dictionaryButton = new JButton("사전");
        styleActionTab(dictionaryButton, new Color(124, 201, 126));
        dictionaryButton.addActionListener(e -> dictSearchField.requestFocusInWindow());

        JButton startButton = new JButton("게임 시작");
        styleActionTab(startButton, new Color(125, 180, 255));
        startButton.addActionListener(e -> startGame());

        JButton exitButton = new JButton("나가기");
        styleActionTab(exitButton, new Color(238, 162, 162));
        exitButton.addActionListener(e -> exitGame());

        top.add(helpButton);
        top.add(usersButton);
        top.add(dictionaryButton);
        top.add(startButton);
        top.add(exitButton);
        return top;
    }

    private JComponent buildCenterPane() {
        JPanel center = new JPanel(new BorderLayout(12, 10));
        center.setOpaque(false);

        center.add(buildGameBoard(), BorderLayout.CENTER);
        center.add(buildDictionaryPanel(), BorderLayout.EAST);
        return center;
    }

    private JComponent buildGameBoard() {
        JPanel boardWrap = new JPanel(new BorderLayout());
        boardWrap.setOpaque(false);
        boardWrap.setBorder(new EmptyBorder(18, 12, 10, 12));

        JPanel board = new JPanel(new BorderLayout(0, 10));
        board.setBackground(PANEL_DARK);
        board.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_GOLD, 8, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel status = new JLabel("라운드 1 / 10", SwingConstants.CENTER);
        status.setForeground(new Color(240, 225, 160));
        status.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        JLabel title = new JLabel("준비 완료! 자바 퀴즈를 시작하세요", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 30));

        JProgressBar timerBar = new JProgressBar(0, 100);
        timerBar.setValue(75);
        timerBar.setStringPainted(true);
        timerBar.setString("남은 시간 75%");
        timerBar.setForeground(ACCENT);
        timerBar.setBackground(new Color(24, 31, 46));
        timerBar.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        board.add(status, BorderLayout.NORTH);
        board.add(title, BorderLayout.CENTER);
        board.add(timerBar, BorderLayout.SOUTH);

        boardWrap.add(board, BorderLayout.CENTER);
        return boardWrap;
    }

    private JComponent buildDictionaryPanel() {
        JPanel dictPanel = new JPanel(new BorderLayout(8, 8));
        dictPanel.setPreferredSize(new Dimension(380, 0));
        dictPanel.setBackground(new Color(248, 250, 255));
        dictPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 202, 227), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("자바 위키 사전");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setOpaque(false);
        dictSearchField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        JButton searchBtn = new JButton("검색");
        searchBtn.addActionListener(e -> runDictionarySearch());
        dictSearchField.addActionListener(e -> runDictionarySearch());
        searchRow.add(dictSearchField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);

        dictList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dictList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        dictList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showConceptDetail(dictList.getSelectedValue());
            }
        });

        dictDetailArea.setEditable(false);
        dictDetailArea.setLineWrap(true);
        dictDetailArea.setWrapStyleWord(true);
        dictDetailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        dictDetailArea.setText("검색어를 입력해 개념을 확인하세요.");

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(dictList),
                new JScrollPane(dictDetailArea)
        );
        split.setDividerLocation(230);
        split.setResizeWeight(0.55);

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setOpaque(false);
        body.add(searchRow, BorderLayout.NORTH);
        body.add(split, BorderLayout.CENTER);

        dictPanel.add(title, BorderLayout.NORTH);
        dictPanel.add(body, BorderLayout.CENTER);
        return dictPanel;
    }

    private JComponent buildBottomPane() {
        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.setOpaque(false);

        gameLogArea.setEditable(false);
        gameLogArea.setLineWrap(true);
        gameLogArea.setWrapStyleWord(true);
        gameLogArea.setRows(6);
        gameLogArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        gameLogArea.setText("[알림] 게임 대기 중입니다.\n");

        JPanel inputRow = new JPanel(new BorderLayout(6, 0));
        answerField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        JButton submitButton = new JButton("제출");
        submitButton.addActionListener(e -> submitAnswer());
        answerField.addActionListener(e -> submitAnswer());
        inputRow.add(answerField, BorderLayout.CENTER);
        inputRow.add(submitButton, BorderLayout.EAST);

        bottom.add(new JScrollPane(gameLogArea), BorderLayout.CENTER);
        bottom.add(inputRow, BorderLayout.SOUTH);
        return bottom;
    }

    private JButton createIconTopButton(Icon icon) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(36, 32));
        button.setMargin(new Insets(4, 6, 4, 6));
        button.setFocusable(false);
        button.setBackground(new Color(236, 236, 236));
        return button;
    }
    private JButton createTopButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setMargin(new Insets(6, 10, 6, 10));
        button.setFocusable(false);
        button.setBackground(new Color(236, 236, 236));
        return button;
    }

    private void styleActionTab(JButton button, Color bg) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setBackground(bg);
        button.setFocusable(false);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
    }

    private void showHowToPlayDialog() {
        String message = """
                [플레이 방법]
                1) 라운드가 시작되면 중앙 보드의 문제를 확인합니다.
                2) 하단 입력창에 정답을 입력하고 제출합니다.
                3) 사전 탭에서 자바 개념을 검색해 힌트/복습에 활용할 수 있습니다.

                * 현재 화면은 뼈대 버전이며 실제 라운드 로직은 다음 단계에서 연결됩니다.
                """;
        JOptionPane.showMessageDialog(this, message, "게임 방법", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showConnectedUsersDialog() {
        JList<String> userList = new JList<>(connectedUsers.toArray(new String[0]));
        userList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        String title = "현재 접속자 (" + connectedUsers.size() + "명)";
        JOptionPane.showMessageDialog(this, new JScrollPane(userList), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitGame() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "게임을 나가시겠습니까?",
                "나가기 확인",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private void startGame() {
        gameLogArea.append("[알림] 게임 시작 버튼이 눌렸습니다. 라운드 로직 연결 예정\n");
        gameLogArea.setCaretPosition(gameLogArea.getDocument().getLength());
        answerField.requestFocusInWindow();
    }

    private void runDictionarySearch() {
        String keyword = dictSearchField.getText() == null ? "" : dictSearchField.getText().trim();
        List<Concept> results = keyword.isEmpty() ? repository.findAll() : searchService.searchByKeyword(keyword);
        loadDictionary(results);
    }

    private void loadDictionary(List<Concept> concepts) {
        dictListModel.clear();
        concepts.stream()
                .sorted(Comparator.comparing(Concept::getTitle, String.CASE_INSENSITIVE_ORDER))
                .forEach(dictListModel::addElement);

        if (dictListModel.isEmpty()) {
            dictDetailArea.setText("검색 결과가 없습니다.");
            return;
        }
        dictList.setSelectedIndex(0);
    }

    private void showConceptDetail(Concept concept) {
        if (concept == null) {
            dictDetailArea.setText("개념을 선택하세요.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(concept.getCategory()).append("] ").append(concept.getTitle()).append("\n\n");
        for (String line : concept.getDescriptionLines()) {
            sb.append(line == null ? "" : line).append("\n");
        }
        dictDetailArea.setText(sb.toString());
        dictDetailArea.setCaretPosition(0);
    }

    private void submitAnswer() {
        String answer = answerField.getText() == null ? "" : answerField.getText().trim();
        if (answer.isEmpty()) {
            return;
        }
        gameLogArea.append("[제출] " + answer + "\n");
        gameLogArea.setCaretPosition(gameLogArea.getDocument().getLength());
        answerField.setText("");
    }

    private static class PatternPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(BG_GAME);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int tile = 42;
            g2.setColor(BG_PATTERN);
            for (int y = 0; y < getHeight(); y += tile) {
                for (int x = (y / tile % 2 == 0 ? 0 : tile / 2); x < getWidth(); x += tile) {
                    g2.fillRect(x, y, tile / 2, tile / 2);
                }
            }
            g2.dispose();
        }
    }
}
