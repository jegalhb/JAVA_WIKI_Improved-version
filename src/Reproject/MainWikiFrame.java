package Reproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * 프로그램의 메인 화면을 구성하고 사용자의 입력을 제어하는 클래스입니다.
 * 외부에서 생성된 SearchService(기능)와 ConceptRepository(데이터)를 주입받아 화면에 연결합니다.
 */
public class MainWikiFrame extends JFrame {
    private SearchService searchService;
    private ConceptRepository repository;

    // 프로그램이 실행되는 동안 화면의 상태를 유지해야 하는 컴포넌트들입니다.
    private JScrollPane scrollPane;          // 상세 내용이 길어질 때 스크롤을 제공하기 위함
    private JList<Concept> resultList;       // 왼쪽 화면에 지식 목록을 나열하기 위함
    private DefaultListModel<Concept> listModel; // JList에 데이터를 넣고 빼는 가방 역할을 수행함
    private JTextField searchField;          // 사용자가 검색어를 입력하는 창
    private String currentCategory;          // 현재 어떤 버튼(기초, 중급 등)이 눌려 있는지 기억함

    public MainWikiFrame(SearchService searchService, ConceptRepository repository) {
        this.searchService = searchService;
        this.repository = repository;

        // 프로그램의 전체적인 창 크기와 제목을 설정합니다.
        // 초기 가로 크기를 1100으로 설정하여 버튼 5개가 잘리지 않고 비율에 맞게 배치되도록 유도합니다.
        setTitle("Java Wiki - Responsive UI");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // 동서남북 배치를 위해 자바의 기본 레이아웃을 사용함

        // 화면의 각 구역을 담당하는 메서드들을 순차적으로 실행하여 조립합니다.
        initTopPanel();    // 검색바와 버튼 영역
        initCenterPanel(); // 리스트와 상세 페이지 영역
        initStatusBar();   // 하단 알림바 영역

        // 프로그램 시작 시 초기 전체 목록을 제공하기 위함!
        // repository에서 가져온 전체 데이터를 updateList 메서드로 전달하여 화면을 채웁니다.
        updateList(repository.findAll());

        // 실행 시 창이 모니터의 정중앙에 나타나도록 설정하여 사용자 편의성을 높임
        setLocationRelativeTo(null);
    }

    /**
     * 상단 검색바 패널을 초기화하는 로직입니다.
     * 사용자가 검색어를 입력하고 엔터를 치거나 버튼을 누르는 '액션'이 발생하는 장소입니다.
     */
    private void initTopPanel() {
        // 컴포넌트 간의 간격을 10픽셀씩 주어 시각적으로 답답하지 않게 구성함
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // 패널 안쪽에 여백을 줌
        topPanel.setBackground(new Color(236, 240, 241)); // 소프트한 회색 배경색 적용

        searchField = new JTextField();
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        // 사용자가 검색 버튼을 누르거나 텍스트창에서 엔터를 치면 performSearch() 메서드로 이동함
        // SearchService의 검색 알고리즘을 가동하게 만드는 트리거
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        // 각 컴포넌트를 레이아웃의 정해진 위치에 배치하여 상단바 UI를 완성함
        topPanel.add(new JLabel("지식 검색: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        // 완성된 패널을 프레임의 북쪽(NORTH) 영역에 고정하여 상단에 위치시킴
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * 화면의 중앙부 중 왼쪽 영역(필터 버튼과 지식 목록)을 초기화하는 로직입니다.
     * 사용자의 클릭 한 번이 어떤 데이터 필터링을 거쳐 리스트에 나타나는지 결정합니다.
     */
    private void initCenterPanel() {
        // 왼쪽 전체를 감싸는 패널로, 상단에는 버튼을 아래에는 목록을 배치하기 위해 BorderLayout을 사용함
        JPanel leftPanel = new JPanel(new BorderLayout());

        // 5개의 카테고리 버튼이 창 너비에 맞춰 동일한 비율로 가로 배치되도록 설정함
        // 1행 5열 구조로 고정하여 창을 전체화면으로 키워도 버튼들이 균형 있게 늘어나도록 유도함
        JPanel filterPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        filterPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 반복문을 사용하여 버튼 생성 로직을 간결하게 만들고 공통 스타일을 일괄 적용함
        String[] categories = {"전체", "기초", "중급", "고급", "메소드 집합"};
        for (String cat : categories) {
            JButton btn = new JButton(cat);

            // 좁은 창에서도 글자가 잘려 '...'으로 표시되는 현상을 방지하기 위해 폰트와 여백을 최적화함
            btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            btn.setMargin(new Insets(2, 2, 2, 2));

            // '메소드 집합' 버튼을 시각적으로 강조하여 일반 카테고리와 차별화된 기능을 제공함을 알림
            if (cat.equals("메소드 집합")) {
                btn.setBackground(new Color(100, 149, 237));
                btn.setForeground(Color.WHITE);
            }

            // 버튼 클릭 시 발생하는 데이터 흐름을 정의함
            // 사용자가 누른 버튼의 텍스트(cat)에 따라 repository에서 서로 다른 리스트를 가져와 화면을 갱신함
            btn.addActionListener(e -> {
                currentCategory = cat; // 현재 어떤 필터가 활성 상태인지 기억함
                if (cat.equals("메소드 집합")) {
                    // 메소드 전용 조회 메서드를 호출하여 결과값을 왼쪽 리스트 모델로 전달함
                    updateList(repository.findMethodAll());
                } else {
                    // 일반 카테고리 필터 로직으로 이동하여 해당 조건의 데이터만 추출함
                    filterList(cat);
                }
            });
            filterPanel.add(btn);
        }
        // 완성된 버튼 패널을 왼쪽 구역의 최상단에 고정함
        leftPanel.add(filterPanel, BorderLayout.NORTH);

        // 실제 지식 제목들이 담기는 데이터 가방(Model)과 화면 컴포넌트(JList)를 생성함
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        // 목록에서 특정 항목을 마우스로 클릭했을 때 발생하는 이벤트 로직임
        // 사용자가 선택한 Concept 객체를 추출하여 우측 상세 보기 메서드(displayDetail)로 전달함
        resultList.addListSelectionListener(e -> {
            // 마우스 클릭이 완료된 최종 상태일 때만 상세 내용을 갱신하도록 제어함
            if (!e.getValueIsAdjusting()) displayDetail(resultList.getSelectedValue());
        });

        // 목록이 길어질 경우를 대비해 스크롤 기능을 추가하고 영역의 제목을 표시함
        JScrollPane leftScroll = new JScrollPane(resultList);
        leftScroll.setBorder(BorderFactory.createTitledBorder("지식 인덱스"));

        // 최종적으로 버튼 아래 공간을 리스트가 가득 채우도록 중앙(CENTER)에 배치함
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        // 사용자가 지식을 선택하기 전, 우측 화면에 표시될 초기 안내 패널을 생성함
        // GridBagLayout을 사용하여 "카테고리를 선택..." 문구를 화면 중앙에 배치함
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.add(new JLabel("카테고리를 선택하거나 검색해 주세요."));

        // 상세 내용이 길어질 때를 대비하여 스크롤 기능을 장착하고 영역 제목을 부여함
        scrollPane = new JScrollPane(welcomePanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("상세 지식"));

        // 왼쪽의 리스트 영역(leftPanel)과 오른쪽의 상세창(scrollPane)을 좌우로 배치함
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, scrollPane);

        // 사용자가 창 크기를 전체 화면으로 키울 때, 각 영역이 늘어나는 비율을 결정함
        // 0.3으로 설정하여 왼쪽 리스트 영역도 30%만큼 함께 커지게 유도함
        // 이 수치는 상단 버튼들이 좁아져서 '...'으로 생략되는 현상을 방지하는 핵심 설정임
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerLocation(320); // 초기 실행 시 구분선의 위치를 지정함

        // 완성된 레이아웃을 프레임의 중앙(CENTER)에 배치하여 화면 전체를 채우게 함
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * 프로그램 하단에 현재 상태를 간략히 보여주는 상태바를 생성함
     * 사용자가 프로그램을 실행했을 때 준비 완료 메시지를 띄워 안정감을 줌
     */
    private void initStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusPanel.add(new JLabel("자바 학습 도우미가 준비되었습니다."));
        add(statusPanel, BorderLayout.SOUTH); // 프레임의 최하단에 배치함
    }

    /**
     * 선택된 지식(Concept) 객체를 받아와서 우측 상세 패널에 실시간으로 그려내는 핵심 로직임
     * 문자열 데이터를 폰트, 색상, 정렬 등을 통해 시각적인 문서 형태로 변환함
     */
    private void displayDetail(Concept selected) {
        if (selected == null) return;

        // 내용을 위에서 아래로 차례대로 쌓기 위해 BoxLayout을 사용함
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // 지식의 제목을 가장 크고 굵게 출력하여 사용자의 시선을 집중시킴
        JLabel titleLabel = new JLabel(selected.getTitle());
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(titleLabel);

        detailPanel.add(Box.createVerticalStrut(2)); // 제목과 분류 사이의 미세한 간격 조절

        // 현재 지식이 어떤 그룹(기초/중급 등)에 속하는지 색상 포인트를 주어 표시함
        JLabel catLabel = new JLabel("분류: " + selected.getCategory());
        catLabel.setForeground(new Color(52, 152, 219)); // 전문적인 느낌의 파란색 적용
        catLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(catLabel);

        detailPanel.add(Box.createVerticalStrut(8));

        // 제목 영역과 본문 영역을 시각적으로 분리하기 위해 가로 구분선을 추가함
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailPanel.add(sep);

        detailPanel.add(Box.createVerticalStrut(10));

        // Concept 객체 내부에 저장된 여러 줄의 설명 데이터를 순회하며 화면에 배치함
        for (String line : selected.getDescriptionLines()) {
            JLabel label = new JLabel();
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            // [H2]로 시작하는 줄은 소제목으로 인식하여 폰트를 키우고 진하게 처리함
            if (line.startsWith("[H2]")) {
                label.setText(line.replace("[H2] ", ""));
                label.setFont(new Font("맑은 고딕", Font.BOLD, 19));
                label.setForeground(new Color(44, 62, 80));
                detailPanel.add(Box.createVerticalStrut(10));
            }
            // [코드]로 시작하는 줄은 파란색 강조와 프로그래밍 전용 폰트(Consolas)를 적용함
            else if (line.startsWith("[코드]")) {
                label.setText(line);
                label.setForeground(Color.BLUE);
                label.setFont(new Font("Consolas", Font.BOLD, 15));
            }
            // 일반 설명 문구 처리
            else {
                label.setText(line);
                label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            }

            detailPanel.add(label);
            detailPanel.add(Box.createVerticalStrut(7)); // 줄 사이의 가독성을 위한 간격
        }

        // 내용이 적을 경우 본문을 위로 밀어올려 배치를 깔끔하게 유지함
        detailPanel.add(Box.createVerticalGlue());

        // 완성된 상세 패널을 화면 우측 스크롤 영역에 장착함
        scrollPane.setViewportView(detailPanel);

        // 새로운 지식을 클릭할 때마다 스크롤바가 항상 맨 위를 가리키도록 하여 사용자 경험을 개선함
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));

        // 컴포넌트 재배치를 즉시 반영하여 화면에 나타냄
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    /**
     * 카테고리 버튼 클릭 시 호출되며, 전체 데이터 중 해당 카테고리만 골라 리스트로 전달함
     * 이 작업 결과는 즉시 왼쪽 인덱스 목록의 변화로 이어짐
     */
    private void filterList(String category) {
        List<Concept> all = repository.findAll();
        listModel.clear(); // 기존 목록을 비우고 새로 채움
        for (Concept c : all) {
            if (category.equals("전체") || c.getCategory().equals(category)) {
                listModel.addElement(c);
            }
        }
    }

    /**
     * 검색창 입력을 처리하며, SearchService로 데이터를 보낸 후 결과 리스트를 화면에 반영함
     */
    private void performSearch() {
        String query = searchField.getText();
        updateList(searchService.search(query));
    }

    /**
     * 외부에서 받은 데이터 리스트를 화면 왼쪽의 JList 모델에 실시간으로 동기화함
     * 이 메서드는 모든 목록 변경(검색, 필터링 등)의 최종 실행 지점임
     */
    private void updateList(List<Concept> concepts) {
        listModel.clear();
        for (Concept c : concepts) {
            listModel.addElement(c);
        }
    }
}