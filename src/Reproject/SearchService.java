package Reproject;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {
    private final ConceptRepository repository;

    public SearchService(ConceptRepository repository) {
        this.repository = repository;
    }

    /**
     * 메인 검색.
     * - query가 비어 있으면 전체 반환
     * - calculateScore() 기준으로 점수화 후 내림차순 정렬
     */
    public List<Concept> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll();
        }

        String lowerQuery = query.toLowerCase().trim();

        return repository.findAll().stream()
                .map(concept -> new SearchResult(concept, calculateScore(concept, lowerQuery)))
                .filter(result -> result.score > 0)
                .sorted(Comparator.comparingDouble((SearchResult r) -> r.score).reversed())
                .map(result -> result.concept)
                .collect(Collectors.toList());
    }

    /**
     * 자동완성 추천 전용 API.
     *
     * 설계 포인트
     * 1) 입력 도중에는 "정확 검색"보다 "빠른 후보 제시"가 중요해서 startsWith 가중치를 크게 준다.
     * 2) 제목에 query가 포함되면 가중치를 추가한다.
     * 3) 최종 fallback으로 기존 유사도 점수(calculateScore)를 더해 오타/부분일치도 살린다.
     */
    public List<Concept> suggest(String query, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String lowerQuery = query.toLowerCase().trim();

        return repository.findAll().stream()
                .map(concept -> {
                    String title = concept.getTitle() == null ? "" : concept.getTitle().toLowerCase(Locale.ROOT);
                    double score = calculateScore(concept, lowerQuery);

                    // 자동완성에서는 prefix 매칭이 사용자 기대와 가장 잘 맞는다.
                    if (title.startsWith(lowerQuery)) {
                        score += 20.0;
                    }
                    if (title.contains(lowerQuery)) {
                        score += 8.0;
                    }
                    return new SearchResult(concept, score);
                })
                .filter(result -> result.score > 0)
                .sorted(Comparator.comparingDouble((SearchResult r) -> r.score).reversed())
                .limit(limit)
                .map(result -> result.concept)
                .collect(Collectors.toList());
    }

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
     * 제목/태그/문자열 유사도 기반 점수 계산.
     * 점수는 검색 정렬 기준이며, 자동완성 suggest()에서도 기본점수로 재활용한다.
     */
    private double calculateScore(Concept concept, String query) {
        double score = 0;
        String title = concept.getTitle().toLowerCase();

        if (title.contains(query)) {
            score += 10.0;
        } else {
            String[] titleParts = title.split("\\s+");
            for (String part : titleParts) {
                double partSim = getSimilarityRatio(part, query);
                if (partSim > 0.5) {
                    score += partSim * 8.0;
                }
            }
        }

        for (String tag : concept.getTags()) {
            if (tag.toLowerCase().contains(query)) {
                score += 5.0;
            }
        }

        double totalSim = getSimilarityRatio(title, query);
        score += totalSim * 3.0;

        return score;
    }

    private double getSimilarityRatio(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.isEmpty() && s2.isEmpty()) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int distance = computeLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        return 1.0 - ((double) distance / Math.max(s1.length(), s2.length()));
    }

    private int computeLevenshteinDistance(String lhs, String rhs) {
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException("메서드 입력값이 잘못되었습니다.");
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

    private static class SearchResult {
        Concept concept;
        double score;

        SearchResult(Concept concept, double score) {
            this.concept = concept;
            this.score = score;
        }
    }
}