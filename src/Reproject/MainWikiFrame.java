package Reproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainWikiFrame extends JFrame {
    private static final String[] CATEGORY_ORDER = {"기초", "중급", "고급", "메소드"};

    private static final Color BG_APP = new Color(242, 246, 251);
    private static final Color BG_TOP = new Color(226, 235, 247);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color ACCENT = new Color(29, 111, 186);
    private static final Color ACCENT_DARK = new Color(20, 76, 132);
    private static final Color DANGER = new Color(198, 62, 62);
    private static final Color TEXT_MAIN = new Color(28, 36, 48);
    private static final Color TEXT_MUTED = new Color(95, 108, 125);

    private final SearchService searchService;
    private final ConceptRepository repository;
    private WikiClient client;

    private JScrollPane scrollPane;
    private JTree conceptTree;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private String currentCategory = "전체";

    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel statusLabel;

    public MainWikiFrame(SearchService searchService, ConceptRepository repository) {
        this.searchService = searchService;
        this.repository = repository;

        setTitle("Java Wiki - 실시간 협업 자바 학습 시스템");
        setSize(1140, 860);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_APP);

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
            setStatusText("온라인 모드");
        } else {
            setStatusText("오프라인 모드");
        }
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(12, 10));
        topPanel.setBorder(new EmptyBorder(14, 16, 12, 16));
        topPanel.setBackground(BG_TOP);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlPanel.setOpaque(false);

        JLabel categoryLabel = new JLabel("분류");
        categoryLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        categoryLabel.setForeground(TEXT_MUTED);

        categoryCombo = new JComboBox<>(new String[]{"전체", "기초", "중급", "고급", "메소드"});
        categoryCombo.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        categoryCombo.setPreferredSize(new Dimension(112, 34));
        categoryCombo.setFocusable(false);
        categoryCombo.addActionListener(e -> {
            currentCategory = String.valueOf(categoryCombo.getSelectedItem());
            applyCurrentView();
        });

        JButton addBtn = new JButton("지식 추가/수정");
        stylePrimaryButton(addBtn);
        addBtn.addActionListener(e -> new ConceptEditFrame(this, repository, getSelectedConcept()));

        JButton deleteBtn = new JButton("지식 삭제");
        styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> {
            Concept selected = getSelectedConcept();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "삭제할 항목을 먼저 선택해주세요.");
                return;
            }

            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "'" + selected.getTitle() + "' 삭제할까요?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION
            );
            if (answer == JOptionPane.YES_OPTION) {
                repository.deleteConcept(selected.getId());
                if (client != null) {
                    client.send("DELETE", selected.getId());
                }
                refreshList();
            }
        });

        controlPanel.add(categoryLabel);
        controlPanel.add(categoryCombo);
        controlPanel.add(addBtn);
        controlPanel.add(deleteBtn);

        searchField = new JTextField();
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        searchField.setForeground(TEXT_MAIN);
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(187, 203, 221), 1, true),
                new EmptyBorder(7, 12, 7, 12)
        ));

        JButton searchButton = new JButton("검색");
        styleAccentOutlineButton(searchButton);
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        getRootPane().setDefaultButton(searchButton);

        topPanel.add(controlPanel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private void styleAccentOutlineButton(JButton button) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(ACCENT_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(146, 176, 206), 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        button.setFocusPainted(false);
        button.setOpaque(true);
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
        applyCurrentView();
    }

    private void initCenterPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_APP);
        leftPanel.setBorder(new EmptyBorder(10, 12, 12, 6));
        leftPanel.setMinimumSize(new Dimension(430, 0));

        conceptTree = new JTree(new DefaultMutableTreeNode("지식"));
        conceptTree.setRootVisible(false);
        conceptTree.setShowsRootHandles(true);
        conceptTree.setRowHeight(28);
        conceptTree.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        conceptTree.setBackground(BG_CARD);
        conceptTree.setBorder(new EmptyBorder(8, 4, 8, 4));
        conceptTree.setCellRenderer(new ConceptTreeRenderer());
        conceptTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        conceptTree.addTreeSelectionListener(e -> displayDetail(getSelectedConcept()));

        JScrollPane treeScroll = new JScrollPane(conceptTree);
        treeScroll.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(204, 217, 233), 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));
        treeScroll.getViewport().setBackground(BG_CARD);
        leftPanel.add(treeScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(BG_APP);
        rightPanel.setBorder(new EmptyBorder(10, 6, 12, 12));

        scrollPane = new JScrollPane(new JLabel("카테고리를 선택하거나 검색해 주세요", SwingConstants.CENTER));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(204, 217, 233), 1),
                "상세 지식"
        ));
        scrollPane.getViewport().setBackground(BG_CARD);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel chatPanel = new JPanel(new BorderLayout(0, 6));
        chatPanel.setBackground(BG_CARD);
        chatPanel.setPreferredSize(new Dimension(0, 220));
        chatPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(204, 217, 233), 1),
                "실시간 협업 채팅"
        ));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatArea.setBackground(new Color(250, 252, 255));
        chatArea.setText(">> 서버 연결 정보를 입력받는 중입니다...\n");

        chatInput = new JTextField();
        chatInput.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatInput.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(187, 203, 221), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        chatInput.addActionListener(e -> {
            String msg = chatInput.getText().trim();
            if (!msg.isEmpty() && client != null) {
                client.send("CHAT", msg);
                chatInput.setText("");
            }
        });

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        rightPanel.add(chatPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.38);
        splitPane.setDividerLocation(430);
        splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(splitPane, BorderLayout.CENTER);
    }

    private class ConceptTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object user = node.getUserObject();

            label.setBorder(new EmptyBorder(4, 6, 4, 6));
            label.setOpaque(true);

            if (user instanceof Concept concept) {
                label.setText(concept.getTitle());
                label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                label.setIcon(UIManager.getIcon("FileView.fileIcon"));
                if (selected) {
                    label.setBackground(new Color(214, 231, 250));
                    label.setForeground(TEXT_MAIN);
                } else {
                    label.setBackground(BG_CARD);
                    label.setForeground(TEXT_MAIN);
                }
            } else {
                label.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                label.setForeground(ACCENT_DARK);
                label.setIcon(UIManager.getIcon("FileView.directoryIcon"));
                if (selected) {
                    label.setBackground(new Color(226, 236, 249));
                } else {
                    label.setBackground(BG_CARD);
                }
            }
            return label;
        }
    }

    private Concept getSelectedConcept() {
        if (conceptTree == null) {
            return null;
        }

        TreePath path = conceptTree.getSelectionPath();
        if (path == null) {
            return null;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object value = node.getUserObject();
        return value instanceof Concept ? (Concept) value : null;
    }

    private void displayDetail(Concept selected) {
        if (selected == null) {
            return;
        }

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(BG_CARD);
        detailPanel.setBorder(new EmptyBorder(20, 32, 28, 32));

        JLabel titleLabel = new JLabel(selected.getTitle());
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(titleLabel);
        detailPanel.add(Box.createVerticalStrut(4));

        JLabel catLabel = new JLabel("분류: " + selected.getCategory());
        catLabel.setForeground(ACCENT_DARK);
        catLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        detailPanel.add(catLabel);
        detailPanel.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        detailPanel.add(sep);
        detailPanel.add(Box.createVerticalStrut(12));

        for (String rawLine : selected.getDescriptionLines()) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            JLabel label = new JLabel(stripTag(line));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (isHeadingLine(line)) {
                label.setFont(new Font("맑은 고딕", Font.BOLD, 19));
                label.setForeground(TEXT_MAIN);
                detailPanel.add(Box.createVerticalStrut(10));
            } else if (isCodeLine(line)) {
                label.setForeground(ACCENT_DARK);
                label.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            } else {
                label.setForeground(new Color(56, 67, 82));
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

    private boolean isHeadingLine(String line) {
        return line.toUpperCase(Locale.ROOT).startsWith("[H2]");
    }

    private boolean isCodeLine(String line) {
        String upper = line.toUpperCase(Locale.ROOT);
        return upper.startsWith("[CODE]") || line.startsWith("[코드]");
    }

    private String stripTag(String line) {
        int right = line.indexOf(']');
        if (line.startsWith("[") && right > 0 && right + 1 < line.length()) {
            return line.substring(right + 1).trim();
        }
        if (line.startsWith("[") && right > 0) {
            return "";
        }
        return line;
    }

    private void applyCurrentView() {
        String keyword = searchField == null ? "" : searchField.getText().trim();
        List<Concept> base = keyword.isEmpty() ? repository.findAll() : searchService.search(keyword);
        updateList(base);
    }

    private void renderTree(List<Concept> concepts) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("지식");
        Map<String, DefaultMutableTreeNode> categoryNodes = new LinkedHashMap<>();

        for (String category : CATEGORY_ORDER) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(category);
            categoryNodes.put(category, node);
            root.add(node);
        }

        List<Concept> sorted = new ArrayList<>(concepts);
        sorted.sort(Comparator.comparing(Concept::getTitle, String.CASE_INSENSITIVE_ORDER));

        for (Concept concept : sorted) {
            String category = normalizeCategory(concept);
            if (!"전체".equals(currentCategory) && !currentCategory.equals(category)) {
                continue;
            }

            DefaultMutableTreeNode categoryNode = categoryNodes.get(category);
            if (categoryNode == null) {
                categoryNode = categoryNodes.computeIfAbsent("기타", DefaultMutableTreeNode::new);
                if (categoryNode.getParent() == null) {
                    root.add(categoryNode);
                }
            }
            categoryNode.add(new DefaultMutableTreeNode(concept));
        }

        conceptTree.setModel(new DefaultTreeModel(root));
        for (int i = 0; i < conceptTree.getRowCount(); i++) {
            conceptTree.expandRow(i);
        }

        if (!hasConceptNode(root)) {
            JLabel empty = new JLabel("표시할 항목이 없습니다.", SwingConstants.CENTER);
            empty.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            empty.setForeground(TEXT_MUTED);
            scrollPane.setViewportView(empty);
            scrollPane.revalidate();
            scrollPane.repaint();
        }
    }

    private boolean hasConceptNode(DefaultMutableTreeNode root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode categoryNode = (DefaultMutableTreeNode) root.getChildAt(i);
            if (categoryNode.getChildCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private String normalizeCategory(Concept c) {
        String id = c.getId() == null ? "" : c.getId().trim().toUpperCase(Locale.ROOT);
        if (id.startsWith("M")) {
            return "메소드";
        }
        if (id.startsWith("B")) {
            return "기초";
        }
        if (id.startsWith("I")) {
            return "중급";
        }
        if (id.startsWith("A")) {
            return "고급";
        }

        String cat = c.getCategory() == null ? "" : c.getCategory().trim().toLowerCase(Locale.ROOT);
        if (cat.contains("method") || cat.contains("메소드")) {
            return "메소드";
        }
        if (cat.contains("basic") || cat.contains("기초")) {
            return "기초";
        }
        if (cat.contains("intermediate") || cat.contains("중급")) {
            return "중급";
        }
        if (cat.contains("advanced") || cat.contains("고급")) {
            return "고급";
        }
        return c.getCategory();
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        List<Concept> results = keyword.isEmpty() ? repository.findAll() : searchService.search(keyword);
        updateList(results);

        if (!keyword.isEmpty() && results.isEmpty()) {
            Concept best = searchService.getBestMatch(keyword);
            if (best != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "검색 결과가 없습니다. 추천: " + best.getTitle(),
                        "검색 안내",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    public void updateList(List<Concept> concepts) {
        SwingUtilities.invokeLater(() -> renderTree(concepts));
    }

    public void applyServerData(List<Concept> concepts) {
        repository.replaceAll(concepts);
        SwingUtilities.invokeLater(() -> {
            String keyword = searchField != null ? searchField.getText().trim() : "";
            if (!keyword.isEmpty()) {
                updateList(searchService.search(keyword));
            } else {
                refreshList();
            }
        });
    }

    private void initStatusBar() {
        statusLabel = new JLabel("오프라인 모드");
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(4, 12, 8, 12));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setStatusText(String text) {
        if (statusLabel != null) {
            statusLabel.setText(" " + text);
        }
    }

    public void onDataAdded(Concept c) {
        repository.addConcept(c);
        if (client != null) {
            client.send("ADD", c);
        }
        refreshList();
    }
}