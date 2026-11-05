package com.venerableknowledge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VenerableKnowledgeTest {

    private static Path findProjectRoot() {
        Path dir = Path.of("").toAbsolutePath();
        while (dir != null) {
            if (Files.exists(dir.resolve("settings.gradle")) || Files.exists(dir.resolve("gradle.properties"))) {
                return dir;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException("Could not locate project root from " + Path.of("").toAbsolutePath());
    }

    @ParameterizedTest(name = "Amplifier {0} gives multiplier {1}x")
    @CsvSource({
        "0, 2",
        "1, 4",
        "2, 8",
        "3, 16"
    })
    @DisplayName("Multiplier formula: Level I (amp 0)->2x, II (1)->4x, III (2)->8x, IV (3)->16x")
    void testCalculateMultiplier(int amplifier, int expectedMultiplier) {
        assertEquals(expectedMultiplier, VenerableKnowledgeCommon.calculateMultiplier(amplifier));
    }

    @Test
    @DisplayName("Amplifier negative edge cases default to 1x")
    void testCalculateMultiplierNegative() {
        assertEquals(1, VenerableKnowledgeCommon.calculateMultiplier(-1));
        assertEquals(1, VenerableKnowledgeCommon.calculateMultiplier(-5));
    }

    @Test
    @DisplayName("Durations: STANDARD_DURATION == 2400 (2m), LONG_DURATION == 6000 (5m)")
    void testDurations() {
        assertEquals(2400, VenerableKnowledgeCommon.STANDARD_DURATION, "STANDARD_DURATION must be 2400 ticks (2 minutes)");
        assertEquals(6000, VenerableKnowledgeCommon.LONG_DURATION, "LONG_DURATION must be 6000 ticks (5 minutes)");
    }

    @Test
    @DisplayName("Foreign effect IDs are correctly configured for Apotheosis, Apothic Attributes, and Zenith")
    void testForeignEffectIds() {
        assertEquals(4, VenerableKnowledgeCommon.FOREIGN_EFFECT_IDS.size());
        assertTrue(VenerableKnowledgeCommon.FOREIGN_EFFECT_IDS.stream()
                .anyMatch(id -> id.getNamespace().equals("apotheosis") && id.getPath().equals("ancient_knowledge")));
        assertTrue(VenerableKnowledgeCommon.FOREIGN_EFFECT_IDS.stream()
                .anyMatch(id -> id.getNamespace().equals("apothic_attributes") && id.getPath().equals("ancient_knowledge")));
        assertTrue(VenerableKnowledgeCommon.FOREIGN_EFFECT_IDS.stream()
                .anyMatch(id -> id.getNamespace().equals("apothic_attributes") && id.getPath().equals("knowledge")));
        assertTrue(VenerableKnowledgeCommon.FOREIGN_EFFECT_IDS.stream()
                .anyMatch(id -> id.getNamespace().equals("zenith") && id.getPath().equals("ancient_knowledge")));
    }

    @Test
    @DisplayName("Language files en_us.json and tr_tr.json parse cleanly and include required translation keys")
    void testLanguageFiles() throws IOException {
        Path root = findProjectRoot();
        Path langDir = root.resolve("common/src/main/resources/assets/venerable_knowledge/lang");

        String[] langFiles = {"en_us.json", "tr_tr.json"};
        for (String langFile : langFiles) {
            Path path = langDir.resolve(langFile);
            assertTrue(Files.exists(path), "Lang file must exist: " + path);

            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                assertTrue(json.has("effect.venerable_knowledge.venerable_knowledge"),
                        langFile + " must contain 'effect.venerable_knowledge.venerable_knowledge'");

                assertTrue(json.has("potion.potency.2"),
                        langFile + " must contain 'potion.potency.2'");
                assertEquals("III", json.get("potion.potency.2").getAsString(),
                        langFile + " 'potion.potency.2' must equal 'III'");

                assertTrue(json.has("potion.potency.3"),
                        langFile + " must contain 'potion.potency.3'");
                assertEquals("IV", json.get("potion.potency.3").getAsString(),
                        langFile + " 'potion.potency.3' must equal 'IV'");

                assertTrue(json.has("item.potionring.ring_of_venerable_knowledge"),
                        langFile + " must contain 'item.potionring.ring_of_venerable_knowledge'");
            }
        }
    }

    @Test
    @DisplayName("config/potionring.txt contains venerable_knowledge:venerable_knowledge#9699539")
    void testPotionRingConfig() throws IOException {
        Path root = findProjectRoot();
        Path configFile = root.resolve("config/potionring.txt");
        assertTrue(Files.exists(configFile), "config/potionring.txt must exist at " + configFile);

        List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);
        boolean containsExpectedEntry = lines.stream()
                .map(String::trim)
                .anyMatch(line -> line.equals("venerable_knowledge:venerable_knowledge#9699539"));

        assertTrue(containsExpectedEntry, "config/potionring.txt must contain 'venerable_knowledge:venerable_knowledge#9699539'");
    }
}
