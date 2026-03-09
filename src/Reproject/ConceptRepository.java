package Reproject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConceptRepository {
    private static final String DATA_FILE = "data.txt";
    private static final String CAT_METHOD = "메소드";
    private static final String CAT_BASIC = "기초";
    private static final String CAT_MID = "중급";
    private static final String CAT_ADV = "고급";

    private final Map<String, Concept> database = new HashMap<>();

    public ConceptRepository() {
        initData();
        initMethod();
        readFile(DATA_FILE, database);

        if (database.isEmpty()) {
            seedDefaultConcepts();
            save();
        }
    }

    private void initData() {
        // kept for compatibility with previous structure
    }

    private void initMethod() {
        // kept for compatibility with previous structure
    }

    public synchronized void save() {
        writeFile(DATA_FILE, database);
    }

    public synchronized List<Concept> findMethodAll() {
        return database.values().stream()
                .filter(c -> CAT_METHOD.equals(c.getCategory()))
                .sorted(Comparator.comparing(Concept::getTitle))
                .toList();
    }

    public synchronized void addConcept(Concept c) {
        database.put(c.getId(), c);
    }

    public synchronized void replaceAll(List<Concept> concepts) {
        database.clear();
        for (Concept c : concepts) {
            database.put(c.getId(), c);
        }
    }

    public synchronized void deleteConcept(String id) {
        database.remove(id);
    }

    public synchronized Concept findById(String id) {
        return database.get(id);
    }

    public synchronized List<Concept> findAll() {
        return new ArrayList<>(database.values());
    }

    private void readFile(String filename, Map<String, Concept> database) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            List<String> block = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("---")) {
                    parseAndPut(block, database);
                    block.clear();
                    continue;
                }
                block.add(line);
            }
            parseAndPut(block, database);
        } catch (IOException e) {
            System.err.println("readFile error: " + e.getMessage());
        }
    }

    private void parseAndPut(List<String> block, Map<String, Concept> database) {
        if (block == null || block.isEmpty()) return;

        List<String> lines = new ArrayList<>();
        for (String raw : block) {
            if (raw != null && !raw.trim().isEmpty()) {
                lines.add(raw.trim());
            }
        }

        if (lines.size() < 3) return;

        String id = lines.get(0);
        String title = lines.get(1);
        String category = normalizeCategory(lines.get(2), id);
        if (id.isEmpty() || title.isEmpty() || category.isEmpty()) return;

        Concept concept = new Concept(id, title, category);
        for (int i = 3; i < lines.size(); i++) {
            concept.addLine(lines.get(i));
        }

        database.put(id, concept);
    }

    private void writeFile(String filename, Map<String, Concept> database) {
        List<Concept> concepts = new ArrayList<>(database.values());
        concepts.sort(Comparator.comparing(Concept::getId));

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            for (Concept concept : concepts) {
                writer.write(concept.getId());
                writer.newLine();
                writer.write(concept.getTitle());
                writer.newLine();
                writer.write(concept.getCategory());
                writer.newLine();

                for (String desc : concept.getDescriptionLines()) {
                    writer.write(desc);
                    writer.newLine();
                }

                writer.write("---");
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("writeFile error: " + e.getMessage());
        }
    }

    private String normalizeCategory(String category, String id) {
        String c = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);

        if (c.contains("method") || c.contains("메소드")) return CAT_METHOD;
        if (c.contains("basic") || c.contains("기초")) return CAT_BASIC;
        if (c.contains("intermediate") || c.contains("중급")) return CAT_MID;
        if (c.contains("advanced") || c.contains("고급")) return CAT_ADV;

        return categoryById(id);
    }

    private String categoryById(String id) {
        if (id == null || id.isEmpty()) return CAT_METHOD;
        char p = Character.toUpperCase(id.charAt(0));
        if (p == 'B') return CAT_BASIC;
        if (p == 'I') return CAT_MID;
        if (p == 'A') return CAT_ADV;
        return CAT_METHOD;
    }

    private void seedDefaultConcepts() {
        addConcept(new Concept("M01", "System.out.println()", CAT_METHOD)
                .addLine("[설명] Console output with newline.")
                .addLine("[코드] System.out.println(\"Hello Java\");"));

        addConcept(new Concept("M02", "Scanner.nextLine()", CAT_METHOD)
                .addLine("[설명] Read full line until Enter.")
                .addLine("[코드] String str = sc.nextLine();"));
    }
}