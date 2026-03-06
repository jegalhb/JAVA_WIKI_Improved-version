# Reproject Java Wiki

`Reproject`는 Java 학습 개념을 검색/조회/추가/삭제하고, 소켓 기반으로 여러 클라이언트가 실시간 동기화와 채팅을 수행하는 Swing 프로젝트입니다.

## 현재 상태 (2026-03-06)
- 저장소는 `ConceptRepository` 1개로 단순화됨
- 지식 데이터는 코드 하드코딩이 아니라 `data.txt` 파일 기반으로 로드/저장
- `data.txt` 자동 생성 완료 (총 230개 항목)

## 핵심 구조
- `MainWikiFrame`: 검색, 카테고리 필터, 상세 보기, 채팅 UI
- `SearchService`: 유사도(Levenshtein) 기반 검색 점수 계산
- `ConceptRepository`: 메모리(Map) + 파일 IO(read/write)
- `ConceptEditFrame`: 지식 추가/수정 입력 UI
- `WikiServer` / `WikiClient`: 소켓 통신, 목록 동기화, 채팅 브로드캐스트

## 데이터 파일 포맷 (`data.txt`)
```txt
ID
제목
카테고리
설명/코드 여러 줄
---
```

예시:
```txt
M01
System.out.println()
메소드
[설명] 콘솔창에 데이터를 출력하고 줄을 바꾼다.
[코드] System.out.println("Hello Java");
---
```

## 로드/저장 흐름
1. 프로그램 시작
- `ConceptRepository` 생성 시 `readFile("data.txt")` 실행
- 파일이 비어 있으면 최소 기본 데이터 시드 후 `save()`

2. 지식 추가/수정/삭제
- UI에서 먼저 메모리(Map)에 반영
- 온라인 모드에서는 서버와 동기화

3. 종료 시 저장
- `MainWikiFrame` 종료 이벤트에서 `repository.save()` 호출
- 현재 메모리 상태를 `data.txt`로 저장

## 실행 가이드
1. 서버 실행: `Reproject.WikiServer`
2. 클라이언트 실행: `Reproject.WikiClient`
3. 단독 UI 실행: `Reproject.Main`

## 참고
- `data.txt` 경로는 실행 워킹디렉토리 기준입니다.
- 서버/클라이언트 동시 실행 환경에서는 저장 책임을 서버 중심으로 통일하는 것을 권장합니다.
