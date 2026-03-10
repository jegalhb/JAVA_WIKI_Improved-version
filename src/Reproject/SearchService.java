package Reproject;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {
    // 검색의 원천이 되는 데이터 창고를 참조함
    // Main에서 생성된 저장소 객체를 받아와서 검색 시마다 데이터를 꺼내 쓰는 통로로 사용
    private final ConceptRepository repository;

    public SearchService(ConceptRepository repository) {
        this.repository = repository;
    }

    /**
     * 사용자가 검색창에 입력한 단어를 분석하여 연관된 지식 리스트를 생성함
     * 입력된 텍스트는 MainWikiFrame의 검색 로직을 거쳐 이 메서드로 전달됨
     * 계산된 결과 리스트는 다시 MainWikiFrame의 'listModel'에 전달되어 왼쪽 인덱스 화면을 갱신함
     */
    public List<Concept> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            // 검색어가 없을 경우 창고의 모든 데이터를 꺼내 초기 전체 목록 상태로 되돌림
            return repository.findAll();
        }

        String lowerQuery = query.toLowerCase().trim();

        return repository.findAll().stream()
                // 각 지식 데이터에 유사도 점수를 매겨 결과 객체로 변환함
                .map(concept -> new SearchResult(concept, calculateScore(concept, lowerQuery)))
                // 점수가 없는 무관한 데이터를 필터링하여 리스트의 순도를 높임
                .filter(result -> result.score > 0)
                // 점수가 높은 순으로 정렬하여 사용자가 원하는 정보가 리스트 상단에 오게 함
                .sorted(Comparator.comparingDouble((SearchResult r) -> r.score).reversed())
                .map(result -> result.concept)
                .collect(Collectors.toList());
    }

    /**
     * 검색 결과가 없을 때 사용자의 오타를 감지하여 가장 적절한 '추천 단어'를 찾아냄
     * 여기서 찾아낸 단어는 MainWikiFrame의 알림창(JOptionPane)으로 전달됨
     * "혹시 'XX'를 찾으시나요?"라는 메시지를 통해 사용자가 올바른 검색어로 유도되도록 영향을 줌
     */
    public Concept getBestMatch(String query) {
        if (query == null || query.trim().isEmpty()) return null;

        String lowerQuery = query.toLowerCase().trim();

        return repository.findAll().stream()
                .map(concept -> new SearchResult(concept, calculateScore(concept, lowerQuery)))
                .filter(result -> result.score > 0.5)
                .max(Comparator.comparingDouble(r -> r.score))
                .map(r -> r.concept)
                .orElse(null);
    }

    /**
     * 제목, 태그 등을 분석하여 지식의 '검색 순위'를 결정하는 핵심 점수 산출 로직
     * 여기서 계산된 점수는 search() 메서드 내의 정렬(Sorted) 로직에 직접적인 영향을 미침
     * 제목 일치는 가장 높은 점수를 주어 검색 결과의 정확성을 보장함
     */
    private double calculateScore(Concept concept, String query) {
        double score = 0;
        String title = concept.getTitle().toLowerCase();

        // 제목 포함 여부 체크: 일치할 경우 리스트 최상단으로 이동시키기 위한 높은 점수 부여
        if (title.contains(query)) {
            score += 10.0;
        } else {
            // 단어 단위 유사도 체크: 오타가 있더라도 리스트에 나타나게 하여 검색 누락을 방지함
            String[] titleParts = title.split("\\s+");
            for (String part : titleParts) {
                double partSim = getSimilarityRatio(part, query);
                if (partSim > 0.5) {
                    score += partSim * 8.0;
                }
            }
        }

        // 태그 일치 여부 체크: 제목에 단어가 없더라도 키워드 기반으로 검색 결과에 포함시킴
        for (String tag : concept.getTags()) {
            if (tag.toLowerCase().contains(query)) {
                score += 5.0;
            }
        }

        // 전체 문장 유사도 보정: 전체적인 맥락이 비슷하면 검색 순위를 소폭 상승시킴
        double totalSim = getSimilarityRatio(title, query);
        score += totalSim * 3.0;

        return score;
    }

    /**
     * 두 텍스트의 일치 비율을 0.0~1.0 사이 숫자로 환산함
     * 이 결과값은 calculateScore의 가중치와 곱해져 최종적인 리스트 배치 순서를 결정함
     */
    private double getSimilarityRatio(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.isEmpty() && s2.isEmpty()) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int distance = computeLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        return 1.0 - ((double) distance / Math.max(s1.length(), s2.length()));
    }

    /**
     * 한 단어를 다른 단어로 바꾸기 위한 최소 편집 횟수를 계산함 (알고리즘적 근거)
     * 이 계산 결과는 최종적으로 검색 엔진이 '유사한 단어'라고 판단하는 기준이 됨
     */
    private int computeLevenshteinDistance(String lhs, String rhs) {
        if (lhs==null || rhs == null){
            throw new IllegalArgumentException("메서드 입력값이 잘못됐습니다.");
        }
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++) distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++) distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++) {
            for (int j = 1; j <= rhs.length(); j++) {
                distance[i][j] = Math.min(
                        Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + (lhs.charAt(i - 1) == rhs.charAt(j - 1) ? 0 : 1)
                );
            }
        }
        return distance[lhs.length()][rhs.length()];
    }

    // 데이터 가공을 위해 제목과 점수를 한 쌍으로 묶어주는 임시 저장 가방
    // Stream 연산 안에서만 사용되며 외부 UI에는 Concept 객체만 전달됨
    private static class SearchResult {
        Concept concept;
        double score;
        SearchResult(Concept concept, double score) {
            this.concept = concept;
            this.score = score;
        }
    }
}