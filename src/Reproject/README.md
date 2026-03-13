# JAVA_WIKI Improved Version

Java 학습 개념을 검색/조회/추가/수정/삭제하고, JSON 저장과 실시간 협업 채팅을 지원하는 Swing 프로젝트입니다.

이번 개선의 핵심은 단순 기능 추가가 아니라, **기존 방식의 문제를 발견하고 실제 사용 흐름 기준으로 해결한 과정**입니다.

## 1. 개선 배경 (기존 -> 개선)

### 기존 구조의 문제
- `data.txt` 중심 저장 방식이라 구조화/검증/확장이 어려움
- 카테고리 버튼이 다수로 분산되어 UI 동선이 복잡함
- 검색은 실행 후 결과 확인 방식이라 입력 중 탐색 경험이 느림
- 검색 추천을 선택해도 유사 결과가 너무 많이 나와서 정확 탐색이 어려움
- 한글 조합 입력(IME) 중 자동완성 이벤트와 충돌해 입력이 끊기는 현상 발생

### 개선 방향
- 저장 포맷을 `data.json`으로 전환
- 카테고리 선택을 콤보박스로 단일화
- 검색 자동완성(입력 중 추천) 도입
- 추천 항목 선택 시 **정확히 해당 항목만** 보여주도록 검색 흐름 분리
- IME 입력을 방해하지 않도록 키/포커스 처리 정리


## 1-1. 변경 전/후 한눈에 비교

| 항목 | 기존 방식 | 개선 방식 | 개선 효과 |
|---|---|---|---|
| 데이터 저장 | `data.txt` 텍스트 중심 | `data.json` 구조화 저장 | 파싱/검증/확장 용이 |
| 카테고리 UI | 버튼 다중 배치 | `JComboBox` 단일 선택 | 화면 단순화, 동선 축소 |
| 검색 경험 | 실행 후 결과 확인 | 입력 중 자동완성 + 추천 팝업 | 탐색 속도 향상 |
| 추천 선택 동작 | 선택 후 유사 결과 다수 노출 | 선택 항목 1건 정확 표시 | 의도한 항목 즉시 도달 |
| 한글 입력 안정성 | IME 조합 중 간헐적 끊김 | debounce + 이벤트 분리 | 연속 입력 안정화 |
| 검색 결과 접근 | 카테고리 수동 확장 필요 | `Search Results` 노드 즉시 표시 | 결과 접근 시간 단축 |
| 상세 연동 | 검색 후 추가 클릭 필요 | 추천 선택 즉시 상세 동기화 | 확인 단계 감소 |
## 2. 이번에 추가/수정된 검색 기능

### 2-1. 자동완성 추천 UI
- `DocumentListener` + `Timer`(debounce)로 입력 중 추천 계산
- 추천 목록은 `JPopupMenu + JList`로 표시
- `UP/DOWN/ESC` 키로 추천 목록 탐색 가능

### 2-2. 추천 선택 시 정확 매칭 표시
- 변경 전: 추천을 눌러도 `search()`가 실행되어 관련 결과가 다수 노출
- 변경 후: 선택한 추천 항목은 `Collections.singletonList(selected)`로 렌더링하여 **1건만 표시**

### 2-3. 검색 결과 바로 확인
- 검색 모드에서 트리에 `Search Results` 노드를 추가
- 카테고리(기초/중급/고급/메서드)를 일일이 펼치지 않아도 검색 결과를 즉시 확인 가능

## 3. 실제 이슈와 해결 기록

### 이슈 A. 한글 입력이 중간에 멈춤 (`크`/`클` 이후 입력 불가)
원인
- 자동완성 동작 중 검색 실행/포커스 이벤트가 IME 조합 흐름에 간섭

해결
- 자동완성은 문서 변경 시 debounce로만 갱신
- Enter 동작을 분리: 추천이 열려 있고 선택이 있으면 추천 수락, 아니면 일반 검색
- 프로그램적으로 텍스트를 변경할 때 `suppressAutoCompleteUpdate`로 재진입 방지

### 이슈 B. 추천 선택 시 관련 결과가 모두 노출됨
원인
- 추천 선택 후 일반 `performSearch()`를 호출하여 fuzzy 검색 경로를 다시 탐

해결
- `acceptSuggestionSelection()`에서 일반 검색을 호출하지 않고
  `updateList(Collections.singletonList(selected), true)`로 정확 항목만 표시
- 동시에 `displayDetail(selected)` 호출로 상세 패널 즉시 동기화

### 이슈 C. 검색 후 결과 확인을 위해 폴더를 계속 열어야 함
원인
- 검색 모드 전용 결과 노드가 없어 카테고리 트리 의존

해결
- `renderTree(List<Concept> concepts, boolean searchMode)`로 확장
- `searchMode=true`일 때 `Search Results` 노드 생성 및 결과 배치

## 4. 코드 반영 위치
- 메인 검색/자동완성/UI 제어: `src/Reproject/MainWikiFrame.java`
- 검색 점수/추천 계산: `src/Reproject/SearchService.java`
- 데이터 저장/로딩(JSON): `src/Reproject/ConceptRepository.java`

## 5. 핵심 메서드 시퀀스

### 5-1. 입력 중 자동완성
```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant SF as searchField
    participant T as debounce Timer
    participant MW as MainWikiFrame
    participant SS as SearchService

    U->>SF: 키 입력
    SF->>T: restart()
    T-->>MW: timeout
    MW->>SS: suggest(query, limit)
    SS-->>MW: 추천 목록 반환
    MW->>MW: popup list 갱신
```

### 5-2. 추천 선택(정확 매칭)
```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant MW as MainWikiFrame

    U->>MW: 추천 클릭/Enter
    MW->>MW: acceptSuggestionSelection()
    MW->>MW: updateList(singletonList(selected), true)
    MW->>MW: displayDetail(selected)
```

### 5-3. 일반 검색 실행 + 트리 렌더링 (`performSearch` + `renderTree`)
```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant SF as searchField
    participant MW as MainWikiFrame
    participant SS as SearchService
    participant T as JTree

    U->>SF: 검색어 입력 후 Enter/검색 버튼
    SF-->>MW: performSearch()
    MW->>MW: suggestionPopup 숨김
    MW->>SS: search(keyword)
    SS-->>MW: List<Concept>
    MW->>MW: updateList(results, searchMode=true)
    MW->>MW: renderTree(concepts, searchMode)
    MW->>T: setModel(DefaultTreeModel)
    MW->>T: expandRow() 초기 확장
```

### 5-4. 서버 동기화 + 개념 추가 전파 (`applyServerData` + `onDataAdded`)
```mermaid
sequenceDiagram
    autonumber
    participant S as Server
    participant C as WikiClient
    participant MW as MainWikiFrame
    participant R as ConceptRepository
    participant E as ConceptEditFrame

    S-->>C: 최신 Concept 목록 전송
    C-->>MW: applyServerData(concepts)
    MW->>R: replaceAll(concepts)
    MW->>MW: 검색 상태에 따라 updateList 또는 refreshList

    E-->>MW: onDataAdded(concept)
    MW->>R: addConcept(concept)
    MW->>C: (온라인 모드) send("ADD", concept)
    MW->>MW: refreshList()
```

### 5-5. 현재 뷰 재계산 (`applyCurrentView`)
```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant MW as MainWikiFrame
    participant R as ConceptRepository
    participant SS as SearchService

    U->>MW: 카테고리 변경 / 화면 갱신 트리거
    MW->>MW: keyword 확인(searchField)
    alt keyword is empty
        MW->>R: findAll()
    else keyword exists
        MW->>SS: search(keyword)
    end
    MW->>MW: updateList(base, searchMode)
```

### 5-6. 자동완성 갱신 분기 (`updateAutoCompleteSuggestions`)
```mermaid
sequenceDiagram
    autonumber
    participant MW as MainWikiFrame
    participant SS as SearchService
    participant P as SuggestionPopup

    MW->>MW: suppressAutoCompleteUpdate 확인
    alt suppress=true
        MW-->>MW: return
    end
    MW->>MW: keyword trim
    alt keyword empty
        MW->>P: hide
        MW-->>MW: return
    end
    MW->>SS: suggest(keyword, limit)
    alt suggested empty
        MW->>P: hide
        MW-->>MW: return
    else suggested exists
        MW->>MW: model clear/add + selectedIndex=0
        MW->>P: show or revalidate/repaint
    end
```

### 5-7. 추천 확정 처리 (`acceptSuggestionSelection`)
```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant MW as MainWikiFrame

    U->>MW: 추천 클릭/Enter
    MW->>MW: selected 항목 확인
    alt selected is null
        MW-->>MW: return
    end
    MW->>MW: suppressAutoCompleteUpdate=true
    MW->>MW: searchField.setText(selected.title)
    MW->>MW: suppressAutoCompleteUpdate=false
    MW->>MW: suggestionPopup hide
    MW->>MW: updateList(singletonList(selected), true)
    MW->>MW: displayDetail(selected)
```

### 5-8. 상세 패널 렌더링 (`displayDetail`)
```mermaid
sequenceDiagram
    autonumber
    participant T as JTree
    participant MW as MainWikiFrame
    participant SP as JScrollPane(detail)

    T-->>MW: TreeSelectionEvent(selected Concept)
    MW->>MW: detailPanel 생성
    MW->>MW: title/category/구분선 배치
    MW->>MW: descriptionLines 순회
    alt heading line
        MW->>MW: heading 스타일 적용
    else code line
        MW->>MW: code 스타일 적용
    else normal line
        MW->>MW: body 스타일 적용
    end
    MW->>SP: setViewportView(detailPanel)
    MW->>SP: scrollTop + revalidate/repaint
```

### 5-9. 로직 프로세스 (분기 상세)

#### 5-9-1. 시작부터 종료까지 전체 분기 상세도
```mermaid
flowchart TD
    A([시작]) --> B([연결 확인])
    B --> C{온라인 모드인가}

    C -- 예 --> D[서버 연결 및 동기화 데이터 수신]
    C -- 아니오 --> E[로컬 JSON 로드]
    D --> F[메인 화면 진입]
    E --> F

    F --> G[/검색어 입력/]
    G --> H[자동완성 갱신<br/>문서 변경 감지 + 디바운스]
    H --> I{추천 항목 존재 여부}

    I -- 예 --> J{추천 선택 여부}
    J -- 예 --> K[선택 항목 1건 표시<br/>acceptSuggestionSelection]
    J -- 아니오 --> L[일반 검색 실행<br/>performSearch]
    I -- 아니오 --> L

    L --> M{검색 결과 존재 여부}
    M -- 예 --> N[검색결과 노드 렌더링<br/>renderTree searchMode true]
    M -- 아니오 --> O[유사 항목 안내<br/>getBestMatch]

    K --> P[상세 패널 표시<br/>displayDetail]
    N --> P
    O --> G

    P --> Q{추가 수정 삭제 작업 여부}
    Q -- 아니오 --> R([종료])
    Q -- 예 --> S[저장소 반영<br/>ConceptRepository]

    S --> T{온라인 모드 여부}
    T -- 예 --> U[서버 전파<br/>ADD UPDATE DELETE]
    T -- 아니오 --> V[로컬 상태 갱신]

    U --> W[목록 새로고침<br/>refreshList]
    V --> W
    W --> G

    classDef startEnd fill:#e9f7ef,stroke:#2e7d32,color:#1b5e20,stroke-width:1.5px;
    classDef process fill:#eef4ff,stroke:#2f5ea8,color:#183b73,stroke-width:1.2px;
    classDef decision fill:#fff4e5,stroke:#b26a00,color:#7a4300,stroke-width:1.2px;
    classDef warn fill:#fdecec,stroke:#b33939,color:#7a1f1f,stroke-width:1.2px;

    class A,R startEnd;
    class B,D,E,F,G,H,K,L,N,P,S,U,V,W process;
    class C,I,J,M,Q,T decision;
    class O warn;
```

#### 5-9-2. 데이터 저장/동기화 상세 시퀀스
```mermaid
sequenceDiagram
    autonumber
    participant 사용자
    participant 편집화면
    participant 메인화면
    participant 저장소
    participant 서버

    사용자->>편집화면: 개념 추가 수정 삭제 요청
    편집화면->>메인화면: 결과 전달 onDataAdded 등
    메인화면->>저장소: 데이터 반영 add update delete

    alt 온라인 모드
        메인화면->>서버: 변경 이벤트 전송
        서버-->>메인화면: 최신 목록 동기화
        메인화면->>저장소: replaceAll 반영
    else 오프라인 모드
        메인화면->>메인화면: 로컬 상태만 갱신
    end

    메인화면->>메인화면: refreshList 실행
    메인화면-->>사용자: 갱신된 목록 상세 화면 표시
```

프로세스 포인트
- 추천 선택 경로는 일반 검색 경로와 분리되어 과검색을 방지합니다.
- 검색 결과는 `검색결과` 노드로 우선 노출되어 카테고리 확장 없이 확인할 수 있습니다.
- CRUD 이후 온라인 오프라인 분기에 따라 서버 전파 여부가 달라집니다.
## 6. 실행 방법
1. 서버 실행: `Reproject.WikiServer`
2. 클라이언트 실행: `Reproject.WikiClient`
3. 단독 실행(오프라인 테스트): `Reproject.Main`

## 7. 참고 화면
- 메인 UI: `docs/screenshots/main-ui.png`
- 수정/등록 UI: `docs/screenshots/edit-frame.png`
- 코드 라인 렌더링: `docs/screenshots/code-line-rendering.png`

---

## 부록. PPT 캡처용 다이어그램 (임시)

### A. 유저 플로우 (캡처용)
```mermaid
flowchart LR
    A0([사용자 시작]) --> A1[앱 실행]
    A1 --> A2{온라인 모드}

    A2 -- 예 --> A3[서버 연결 및 동기화]
    A2 -- 아니오 --> A4[로컬 JSON 로드]
    A3 --> A5[메인 진입]
    A4 --> A5

    A5 --> B1[/검색어 입력/]
    B1 --> B2[자동완성 확인]
    B2 --> B3{추천 선택}
    B3 -- 예 --> B4[정확 1건 표시]
    B3 -- 아니오 --> B5[일반 검색]
    B4 --> C1[상세 확인]
    B5 --> C1

    C1 --> C2{수정 작업 실행}
    C2 -- 예 --> C3[추가 수정 삭제 반영]
    C2 -- 아니오 --> C4([종료])
    C3 --> C5[목록 갱신]
    C5 --> B1

    classDef startEnd fill:#e9f7ef,stroke:#2e7d32,color:#1b5e20,stroke-width:1.4px;
    classDef process fill:#eef4ff,stroke:#2f5ea8,color:#183b73,stroke-width:1.2px;
    classDef decision fill:#fff4e5,stroke:#b26a00,color:#7a4300,stroke-width:1.2px;

    class A0,C4 startEnd;
    class A1,A3,A4,A5,B1,B2,B4,B5,C1,C3,C5 process;
    class A2,B3,C2 decision;
```

**설명 문구 (슬라이드 우측 Description 용)**
- 사용자는 `입력 → 선택 → 확인 → 수정` 4단계 흐름으로 학습 정보를 탐색합니다.
- 추천 선택 경로와 일반 검색 경로를 분리해 과검색을 방지했습니다.
- 작업 이후 즉시 목록 갱신으로 연속 탐색이 가능합니다.

### B. 로직 프로세스 (시작~종료 전체 분기 상세, 캡처용)
```mermaid
flowchart TD
    A([시작]) --> B([연결 확인])
    B --> C{온라인 모드인가}

    C -- 예 --> D[서버 연결 + 동기화 수신]
    C -- 아니오 --> E[로컬 JSON 로드]
    D --> F[메인 화면 진입]
    E --> F

    F --> G[/검색어 입력/]
    G --> H[자동완성 갱신
DocumentListener + debounce]
    H --> I{추천 항목 존재 여부}

    I -- 예 --> J{추천 선택 여부}
    J -- 예 --> K[선택 항목 1건 표시
acceptSuggestionSelection]
    J -- 아니오 --> L[일반 검색 실행
performSearch]
    I -- 아니오 --> L

    L --> M{검색 결과 존재 여부}
    M -- 예 --> N[검색결과 노드 렌더링
renderTree searchMode true]
    M -- 아니오 --> O[유사 항목 안내
getBestMatch]

    K --> P[상세 패널 표시
displayDetail]
    N --> P
    O --> G

    P --> Q{CRUD 작업 여부}
    Q -- 아니오 --> R([종료])
    Q -- 예 --> S[저장소 반영
ConceptRepository]

    S --> T{온라인 모드 여부}
    T -- 예 --> U[서버 전파
ADD UPDATE DELETE]
    T -- 아니오 --> V[로컬 상태 갱신]

    U --> W[목록 새로고침
refreshList]
    V --> W
    W --> G
```

**설명 문구 (슬라이드 우측 Description 용)**
- 검색 단계와 저장 단계를 분리해 로직 복잡도를 낮췄습니다.
- 온라인/오프라인 분기로 동기화 정책을 명확히 구분했습니다.
- 핵심 루프는 `검색 → 상세 → 작업 → 갱신`입니다.

### C. 마지막 슬라이드 (캡처용 구성)

#### C-1. Before vs After 구조 그림
```mermaid
flowchart LR
    subgraph B1[기존 방식 Before]
        B1A[data.txt 중심 저장]
        B1B[카테고리 버튼 분산]
        B1C[검색 실행 후 결과 확인]
        B1D[추천 선택 시 과검색]
    end

    B1 --> M[개선 작업]

    subgraph A1[개선 방식 After]
        A1A[data.json 구조화 저장]
        A1B[카테고리 콤보박스 단일화]
        A1C[입력 중 자동완성 + 검색결과 노드]
        A1D[추천 선택 시 정확 1건 표시]
    end

    M --> A1
```

#### C-2. 마지막 장 Description 문구
- 본 프로젝트는 저장 구조, 검색 UX, 입력 안정성을 중심으로 개선했습니다.
- 사용자 관점에서는 탐색 단계를 줄이고, 개발 관점에서는 유지보수 가능한 구조로 전환했습니다.
- 향후 과제로 즐겨찾기/최근 본 항목 영속화와 검색 랭킹 고도화를 계획하고 있습니다.

#### C-3. 한 줄 결론
`JAVA_WIKI는 학습 정보 탐색 경험을 빠르고 정확하게 만드는 방향으로 개선되었습니다.`
---

## 부록. PPT 캡처용 다이어그램 (최종)

### A. 유저 플로우 (좌→우, PPT용)
```mermaid
flowchart LR
    A([시작]) --> B[앱 실행]
    B --> C{온라인 모드}
    C -- 예 --> D[서버 연결 및 동기화]
    C -- 아니오 --> E[로컬 JSON 로드]
    D --> F[메인 진입]
    E --> F

    F --> G[/검색어 입력/]
    G --> H[추천 목록 표시]
    H --> I{추천 항목 선택}
    I -- 예 --> J[선택 항목 단일 결과 표시]
    I -- 아니오 --> K[일반 검색 결과 표시]

    J --> L[상세 보기]
    K --> L
    L --> M{추가/수정/삭제}
    M -- 예 --> N[저장 및 목록 갱신]
    M -- 아니오 --> O([종료])
    N --> G

    classDef startEnd fill:#e9f7ef,stroke:#2e7d32,color:#1b5e20,stroke-width:1.4px;
    classDef process fill:#eef4ff,stroke:#2f5ea8,color:#183b73,stroke-width:1.2px;
    classDef decision fill:#fff4e5,stroke:#b26a00,color:#7a4300,stroke-width:1.2px;
    class A,O startEnd;
    class B,D,E,F,G,H,J,K,L,N process;
    class C,I,M decision;
```

캡처 권장 설정 (PPT 선명도):
- 브라우저 확대율 `125%~150%`에서 다이어그램 캡처
- 캡처 이미지는 슬라이드 표시 크기의 `최소 2배` 해상도로 저장
- PPT 삽입 후 `자르기`만 사용하고, 과도한 확대는 피하기
- 가능하면 PNG 대신 SVG 내보내기(사용 환경 지원 시) 후 삽입

### B. 로직 프로세스 (처음~끝 전체 분기, 세로형)
```mermaid
flowchart TD
    A([시작]) --> B{온라인 모드}
    B -- 예 --> C[서버 동기화]
    B -- 아니오 --> D[로컬 JSON 로드]
    C --> E[메인 화면]
    D --> E

    E --> F[/검색어 입력/]
    F --> G[추천 목록 갱신]
    G --> H{추천 선택 여부}
    H -- 예 --> I[선택 항목만 결과 표시]
    H -- 아니오 --> J[일반 검색 실행]

    I --> K[상세 보기]
    J --> K
    K --> L{CRUD 실행 여부}
    L -- 아니오 --> M([종료])
    L -- 예 --> N[저장 처리]
    N --> O{온라인 모드}
    O -- 예 --> P[서버 전파]
    O -- 아니오 --> Q[로컬 상태 갱신]
    P --> R[트리 갱신]
    Q --> R
    R --> F
```