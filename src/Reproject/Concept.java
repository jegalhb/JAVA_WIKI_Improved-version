package Reproject;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * 자바 학습 지식 한 개를 나타내는 핵심 데이터 모델 클래스입니다.
 * 모든 지식은 ID, 제목, 카테고리, 설명 리스트를 가지며 프로그램 전체로 유통됩니다.
 */
public class Concept implements Serializable {
    //  각 지식을 고유하게 식별하고 상세 내용을 찾기 위한 키(Key)로 사용함
    // ConceptRepository의 Map에서 데이터를 넣고 뺄 때 기준점이 됨
    private String id;

    private static final long serialVersionUID = 1l; // 새로운 서버 구축

    // 사용자가 왼쪽 리스트에서 보고 클릭할 지식의 명칭임
    // SearchService에서 검색 대상이 되며, JList 화면에 직접적으로 노출됨
    private String title;

    //기초, 중급, 고급 등 지식의 난이도나 종류를 구분하기 위함
    // 상단 필터 버튼을 눌렀을 때 특정 그룹만 골라내는 기준이 됨
    private String category;

    //  제목 외에 검색에 도움을 줄 부가적인 키워드들을 저장함
    //  SearchService에서 검색 정확도를 높이는 보조 수단으로 활용됨
    private List<String> tags = new ArrayList<>();

    //  지식의 본문 내용(설명, 코드 등)을 줄 단위로 보관하기 위함
    //  MainWikiFrame의 displayDetail() 메서드로 전달되어 줄 단위로 화면에 그려짐
    private List<String> descriptionLines = new ArrayList<>();

    //  지식 객체를 생성할 때 필수 정보(ID, 제목, 분류)를 강제로 입력받기 위함
    //  ConceptRepository의 초기화 로직에서 new Concept(...) 형식으로 호출됨
    public Concept(String id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }
    /**
     * 지식의 본문 내용을 한 줄씩 추가하기 위해 작성함
     *  메서드 체이닝(return this)을 지원하여 데이터를 연속적으로 넣기 편하게 구성함
     *  호출할 때마다 descriptionLines 리스트에 데이터가 쌓여 상세 페이지가 풍성해짐
     */
    public Concept addLine(String line) {
        this.descriptionLines.add(line);
        return this; // 자기 자신을 반환하여 .addLine().addLine() 처럼 연속 호출 가능케 함
    }

    // 아래 Getter 메서드들은 UI와 검색 엔진이 데이터를 읽어갈 수 있도록 통로를 열어줌
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public List<String> getDescriptionLines() { return descriptionLines; }
    public List<String> getTags() { return tags; }

    /**
     *  JList 등 UI 컴포넌트에서 객체를 출력할 때 제목이 바로 보이게 하기 위함
     */
    @Override
    public String toString() { return title; }
}