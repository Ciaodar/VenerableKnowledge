package com.venerableknowledge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    @DisplayName("All 49 JSON brewing recipes parse cleanly and have valid minecraft:brewing structure")
    void testBrewingRecipesStructure() throws IOException {
        Path root = findProjectRoot();
        Path brewingDir = root.resolve("common/src/main/resources/data/venerable_knowledge/recipe/brewing");
        assertTrue(Files.isDirectory(brewingDir), "Brewing directory must exist at " + brewingDir);

        List<Path> recipeFiles;
        try (Stream<Path> stream = Files.list(brewingDir)) {
            recipeFiles = stream.filter(p -> p.toString().endsWith(".json")).toList();
        }

        assertEquals(49, recipeFiles.size(), "There must be exactly 49 brewing recipe JSON files");

        for (Path recipeFile : recipeFiles) {
            try (Reader reader = Files.newBufferedReader(recipeFile, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                assertTrue(json.has("type"), "Recipe " + recipeFile.getFileName() + " must have 'type'");
                assertEquals("minecraft:brewing", json.get("type").getAsString(),
                        "Recipe " + recipeFile.getFileName() + " type must be 'minecraft:brewing'");

                assertTrue(json.has("input") && json.get("input").isJsonObject(),
                        "Recipe " + recipeFile.getFileName() + " must have valid 'input' object");

                assertTrue(json.has("output") && json.get("output").isJsonObject(),
                        "Recipe " + recipeFile.getFileName() + " must have valid 'output' object");

                assertTrue(json.has("reagent") && json.get("reagent").isJsonObject(),
                        "Recipe " + recipeFile.getFileName() + " must have valid 'reagent' object");
            }
        }
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

                assertTrue(json.has("tooltip.venerable_knowledge.ring_description"),
                        langFile + " must contain 'tooltip.venerable_knowledge.ring_description'");
                assertTrue(json.has("tooltip.venerable_knowledge.ring_level_1"),
                        langFile + " must contain 'tooltip.venerable_knowledge.ring_level_1'");
                assertTrue(json.has("tooltip.venerable_knowledge.ring_level_2"),
                        langFile + " must contain 'tooltip.venerable_knowledge.ring_level_2'");

                if ("en_us.json".equals(langFile)) {
                    assertEquals("• Level I (1 Ring): 2x Experience Multiplier",
                            json.get("tooltip.venerable_knowledge.ring_level_1").getAsString());
                    assertEquals("• Level II (2 Rings): 4x Experience Multiplier",
                            json.get("tooltip.venerable_knowledge.ring_level_2").getAsString());
                }
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

    @Test
    @DisplayName("appendRingTooltip adds description, level 1, level 2, and mod attribution")
    void testAppendRingTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("Ring of Venerable Knowledge"));
        VenerableKnowledgeCommon.appendRingTooltip(tooltip);

        assertEquals(5, tooltip.size());
        assertEquals("Ring of Venerable Knowledge", tooltip.get(0).getString());
        assertEquals("tooltip.venerable_knowledge.ring_description", tooltip.get(1).getString());
        assertEquals("tooltip.venerable_knowledge.ring_level_1", tooltip.get(2).getString());
        assertEquals("tooltip.venerable_knowledge.ring_level_2", tooltip.get(3).getString());
        assertEquals("§dEffect of Venerable Knowledge", tooltip.get(4).getString());
    }

    @Test
    @DisplayName("appendRingTooltip deduplicates duplicate trailing lines")
    void testAppendRingTooltipDeduplication() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("Ring of Venerable Knowledge"));
        tooltip.add(Component.literal("Potion Rings - Reforged"));
        tooltip.add(Component.literal("Potion Rings - Reforged"));

        VenerableKnowledgeCommon.appendRingTooltip(tooltip);

        assertEquals(6, tooltip.size());
        assertEquals("Ring of Venerable Knowledge", tooltip.get(0).getString());
        assertEquals("Potion Rings - Reforged", tooltip.get(1).getString());
        assertEquals("tooltip.venerable_knowledge.ring_description", tooltip.get(2).getString());
        assertEquals("tooltip.venerable_knowledge.ring_level_1", tooltip.get(3).getString());
        assertEquals("tooltip.venerable_knowledge.ring_level_2", tooltip.get(4).getString());
        assertEquals("§dEffect of Venerable Knowledge", tooltip.get(5).getString());
    }

    @Test
    @DisplayName("Potion Rings Reforged model and item assets exist and custom texture is removed")
    void testPotionRingAssets() throws IOException {
        Path root = findProjectRoot();
        Path assetsDir = root.resolve("common/src/main/resources/assets/potionring");

        Path oldPng = assetsDir.resolve("textures/item/ring_of_venerable_knowledge.png");
        assertFalse(Files.exists(oldPng), "Custom texture png must be removed");

        Path modelFile = assetsDir.resolve("models/item/ring_of_venerable_knowledge.json");
        assertTrue(Files.exists(modelFile), "Ring model json must exist");
        try (Reader reader = Files.newBufferedReader(modelFile, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject textures = json.getAsJsonObject("textures");
            assertNotNull(textures);
            assertEquals("potionring:item/gold_ring", textures.get("layer0").getAsString());
            assertEquals("potionring:item/gem_color", textures.get("layer1").getAsString());
            assertEquals("potionring:item/gem_light", textures.get("layer2").getAsString());
        }

        Path itemFile = assetsDir.resolve("items/ring_of_venerable_knowledge.json");
        assertTrue(Files.exists(itemFile), "Ring item definition json must exist");
        try (Reader reader = Files.newBufferedReader(itemFile, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject model = json.getAsJsonObject("model");
            assertNotNull(model);
            assertEquals("potionring:item/ring_of_venerable_knowledge", model.get("model").getAsString());
            var tints = model.getAsJsonArray("tints");
            assertNotNull(tints);
            assertEquals(3, tints.size());
            assertEquals(-1, tints.get(0).getAsJsonObject().get("value").getAsInt());
            assertEquals(-7077677, tints.get(1).getAsJsonObject().get("value").getAsInt());
            assertEquals(-1, tints.get(2).getAsJsonObject().get("value").getAsInt());
        }
    }
}
