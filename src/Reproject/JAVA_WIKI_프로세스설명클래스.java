package Reproject;

/**
 * JAVA_WIKI 전체 로직을 분석/복습하기 위한 설명 전용 클래스.
 *
 * 목적
 * 1) 실행 진입점이 2개(Main, WikiClient)인 이유를 명확히 이해한다.
 * 2) UI 이벤트 -> 저장소 반영 -> 서버 전파 -> 전체 동기화의 흐름을 단계별로 정리한다.
 * 3) 엔터 검색, 실시간 동기화, 채팅 닉네임 처리의 동작 원리를 빠르게 재확인한다.
 */
public class JAVA_WIKI_프로세스설명클래스 {

    private JAVA_WIKI_프로세스설명클래스() {
        // 설명 전용 유틸 클래스이므로 인스턴스 생성을 막는다.
    }

    public static String getProcessGuide() {
        String nl = System.lineSeparator();
        return String.join(nl,
                "================ JAVA_WIKI 로직 프로세스 설명 ================",
                "",
                "[A] 전체 구조 요약",
                "- 데이터: Concept",
                "- 저장소: ConceptRepository(Map 기반)",
                "- 검색엔진: SearchService(점수 기반 정렬)",
                "- UI: MainWikiFrame(목록/상세/채팅)",
                "- 편집 UI: ConceptEditFrame(추가/수정)",
                "- 통신: WikiClient <-> WikiServer",
                "",
                "[B] 프로그램 실행부터 종료까지 전체 프로세스",
                "",
                "1) 프로그램 시작",
                "- 오프라인: Main.main() -> Repository 생성 -> SearchService 생성 -> MainWikiFrame 생성/표시",
                "- 온라인: WikiClient.main() -> 위와 동일하게 UI 생성 -> IP/PORT/닉네임 입력 -> client.start()",
                "",
                "2) UI 초기 렌더링",
                "- MainWikiFrame 생성자에서 initTopPanel(), initCenterPanel(), initStatusBar() 순서로 UI 구성",
                "- updateList(repository.findAll())로 초기 목록 표시",
                "",
                "3) 검색(버튼/엔터)",
                "- searchButton.addActionListener / searchField.addActionListener -> performSearch()",
                "- performSearch(): searchField.getText().trim() -> searchService.search(keyword)",
                "- updateList()가 JList 모델 반영",
                "",
                "4) 저장(추가/수정)",
                "- MainWikiFrame의 [지식 추가/수정] 버튼 -> ConceptEditFrame 오픈",
                "- ConceptEditFrame.saveAction()에서 Concept 조립",
                "- mainFrame.onDataAdded(newConcept) 호출(단일 저장 경로)",
                "- onDataAdded():",
                "  a. repository.addConcept(c) 로컬 즉시 반영",
                "  b. client != null 이면 client.send('ADD', c) 서버 전파",
                "  c. refreshList() 로 현재 필터/화면 갱신",
                "",
                "5) 삭제",
                "- 메인 리스트 선택 후 [지식 삭제] 버튼",
                "- repository.deleteConcept(id) 로컬 삭제",
                "- client != null 이면 client.send('DELETE', id) 서버 전파",
                "- refreshList() 호출",
                "",
                "6) 협업 동기화(서버 반영)",
                "- 서버: ADD/DELETE 수신 -> 서버 Repository 수정 -> broadcast('REFRESH')",
                "- 클라이언트: REFRESH 수신 -> send('LIST')",
                "- 서버: LIST 수신 -> LIST_DATA + findAll() 반환",
                "- 클라이언트: LIST_DATA 수신 -> mainFrame.applyServerData(list)",
                "- applyServerData(): repository.replaceAll(list) 후",
                "  a. 검색어가 있으면 updateList(searchService.search(keyword))",
                "  b. 검색어가 없으면 refreshList()",
                "",
                "7) 채팅",
                "- chatInput Enter -> client.send('CHAT', msg 본문)",
                "- WikiClient.send()에서만 '[닉네임]: ' 접두사 부착(중복 방지)",
                "- 서버 broadcast('CHAT_MSG', msg) -> 모든 클라 appendChat(msg)",
                "",
                "8) 종료",
                "- JFrame 닫기(EXIT_ON_CLOSE) 시 프로세스 종료",
                "- 서버는 해당 클라이언트 소켓 종료 감지 후 clients 목록에서 제거",
                "",
                "[C] 메서드별 상호작용 맵(호출 체인)",
                "",
                "1. 시작 체인",
                "Main.main / WikiClient.main",
                "-> new ConceptRepository()",
                "-> new SearchService(repository)",
                "-> new MainWikiFrame(searchService, repository)",
                "-> (온라인) frame.setClient(client), client.start(ip, port)",
                "",
                "2. 검색 체인",
                "JButton/JTextField Action",
                "-> MainWikiFrame.performSearch()",
                "-> SearchService.search(query)",
                "-> MainWikiFrame.updateList(results)",
                "",
                "3. 저장 체인",
                "ConceptEditFrame.saveAction()",
                "-> MainWikiFrame.onDataAdded(concept)",
                "-> ConceptRepository.addConcept(concept)",
                "-> WikiClient.send('ADD', concept) [온라인]",
                "-> MainWikiFrame.refreshList()",
                "",
                "4. 삭제 체인",
                "MainWikiFrame delete button",
                "-> ConceptRepository.deleteConcept(id)",
                "-> WikiClient.send('DELETE', id) [온라인]",
                "-> MainWikiFrame.refreshList()",
                "",
                "5. 서버 수신 체인",
                "WikiServer.ClientHandler.run()",
                "-> readUTF(command)",
                "-> (ADD/DELETE/LIST/CHAT 분기 처리)",
                "-> 필요 시 broadcast(type, data)",
                "",
                "6. 클라이언트 수신 체인",
                "WikiClient.start() 수신 루프",
                "-> REFRESH: send('LIST')",
                "-> LIST_DATA: MainWikiFrame.applyServerData(list)",
                "-> CHAT_MSG: MainWikiFrame.appendChat(msg)",
                "",
                "[D] 검색 점수 계산 세부(핵심 로직)",
                "- SearchService.calculateScore()에서 누적 점수",
                "  1) 제목 직접 포함: 높은 가중치",
                "  2) 제목 단어별 유사도: 중간 가중치",
                "  3) 태그 포함: 보조 가중치",
                "  4) 전체 문자열 유사도: 미세 보정",
                "- 유사도는 Levenshtein 거리 기반(getSimilarityRatio)",
                "",
                "[E] 왜 이 구조가 안정적인가",
                "- 저장 경로를 onDataAdded 하나로 통일해 로컬반영/서버전파 누락을 방지",
                "- LIST_DATA 수신 시 저장소 replaceAll로 검색/필터 기준 데이터 불일치 제거",
                "- 닉네임 접두사 부착 지점을 WikiClient.send('CHAT') 한 곳으로 고정",
                "",
                "[F] 디버깅 기준점(문제 발생 시 순서)",
                "1) 이벤트가 메서드에 들어오는지 확인(ActionListener 로그)",
                "2) 로컬 저장소 반영 여부 확인(add/delete 후 findAll 크기)",
                "3) send() 호출 여부/명령 타입 확인",
                "4) 서버 readUTF 분기 진입 확인",
                "5) REFRESH->LIST->LIST_DATA 왕복 여부 확인",
                "6) applyServerData 이후 UI 갱신 분기(검색어 유무) 확인",
                "",
                "==============================================================="
        );
    }

    public static void printGuide() {
        System.out.println(getProcessGuide());
    }

    public static void main(String[] args) {
        printGuide();
    }
}