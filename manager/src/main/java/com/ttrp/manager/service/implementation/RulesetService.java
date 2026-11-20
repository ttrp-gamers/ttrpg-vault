package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.character.CharacterCalculationRequest;
import com.ttrp.manager.dto.character.CharacterCalculationResponse;
import com.ttrp.manager.service.declaration.IRulesetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class RulesetService implements IRulesetService {
    private final Open5eClient open5eClient;

    @Override
    public List<Map<String, Object>> getRulesets() {
        return List.of(dnd5eRuleset("DND_5E_2014", "Dungeons & Dragons 5e (2014)"),
                dnd5eRuleset("DND_5E_2024", "Dungeons & Dragons 5e (2024)"),
                customRuleset());
    }

    @Override
    public Map<String, List<Map<String, Object>>> getCharacterCatalog() {
        return Map.of(
                "classes2014", open5eClient.getClasses("5e-2014"),
                "subclasses2014", open5eClient.getSubclasses("5e-2014"),
                "classes2024", open5eClient.getClasses("5e-2024"),
                "subclasses2024", open5eClient.getSubclasses("5e-2024"),
                "species", open5eClient.getSpecies(),
                "backgrounds", open5eClient.getBackgrounds()
        );
    }

    @Override
    public CharacterCalculationResponse calculate(CharacterCalculationRequest request) {
        Map<String, Object> ruleset = request.rulesetSchema() != null
                ? request.rulesetSchema()
                : ruleset(Optional.ofNullable(request.rulesetKey()).orElse("CUSTOM"));
        Map<String, Object> values = request.values() == null ? Map.of() : request.values();
        Map<String, Object> context = new HashMap<>(values);
        context.put("level", request.level() == null ? 1 : request.level());
        context.put("hitDie", request.hitDie() == null ? 8 : request.hitDie());

        Map<String, Object> derived = new LinkedHashMap<>();
        Object rawStats = ruleset.get("derivedStats");
        if (rawStats instanceof List<?> stats) {
            for (Object rawStat : stats) {
                if (rawStat instanceof Map<?, ?> stat) {
                    String id = String.valueOf(stat.get("id"));
                    Object formula = stat.containsKey("formula") ? stat.get("formula") : "";
                    derived.put(id, evaluate(id, String.valueOf(formula), ruleset, context));
                }
            }
        }
        Integer hp = number(derived.get("hitPoints"));
        return new CharacterCalculationResponse(derived, hp);
    }

    private Map<String, Object> ruleset(String key) {
        if ("DND_5E".equals(key) || "DND_5E_2014".equals(key)) {
            return dnd5eRuleset("DND_5E_2014", "Dungeons & Dragons 5e (2014)");
        }
        if ("DND_5E_2024".equals(key)) {
            return dnd5eRuleset("DND_5E_2024", "Dungeons & Dragons 5e (2024)");
        }
        if ("CUSTOM".equals(key)) {
            return customRuleset();
        }
        throw new ResponseStatusException(BAD_REQUEST, "Unknown ruleset: " + key);
    }

    private Map<String, Object> customRuleset() {
        return new LinkedHashMap<>(Map.of(
                "id", "custom",
                "name", "Homebrew (custom ruleset)",
                "description", "Define the fields your game needs.",
                "terminology", Map.of("archetype", "Archetype", "level", "Level"),
                "archetypes", List.of(),
                "sections", List.of(),
                "derivedStats", List.of(),
                "resources", List.of()
        ));
    }

    private Map<String, Object> dnd5eRuleset(String id, String name) {
        List<Map<String, Object>> fields = List.of(
                field("strength", "Strength"),
                field("dexterity", "Dexterity"),
                field("constitution", "Constitution"),
                field("intelligence", "Intelligence"),
                field("wisdom", "Wisdom"),
                field("charisma", "Charisma")
        );
        return new LinkedHashMap<>(Map.of(
                "id", id,
                "name", name,
                "description", "Standard ability scores and level-based hit points.",
                "terminology", Map.of("archetype", "Class", "level", "Level"),
                "archetypes", List.of("Barbarian", "Bard", "Cleric", "Druid", "Fighter", "Monk",
                        "Paladin", "Ranger", "Rogue", "Sorcerer", "Warlock", "Wizard"),
                "sections", List.of(Map.of("id", "abilities", "title", "Ability Scores", "fields", fields)),
                "derivedStats", List.of(Map.of("id", "hitPoints", "label", "Hit Points",
                        "formula", "level * hitDie + floor((constitution - 10) / 2) * level")),
                "resources", List.of()
        ));
    }

    private Map<String, Object> field(String id, String label) {
        return Map.of("id", id, "label", label, "model", "score", "defaultValue", 10,
                "min", 1, "max", 30, "modifier", "floor((" + id + " - 10) / 2)");
    }

    private Object evaluate(String statId, String formula, Map<String, Object> ruleset, Map<String, Object> values) {
        if (formula.isBlank()) return null;
        try {
            return new FormulaParser(formulaWithIds(formula, ruleset), values).parse();
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Invalid formula for derived stat '" + statId + "'", exception);
        }
    }

    private String formulaWithIds(String formula, Map<String, Object> ruleset) {
        Map<String, Integer> occurrences = new HashMap<>();
        Object rawSections = ruleset.get("sections");
        if (!(rawSections instanceof List<?> sections)) return formula;
        String result = formula;
        List<String[]> replacements = new ArrayList<>();
        for (Object rawSection : sections) {
            if (!(rawSection instanceof Map<?, ?> section) || !(section.get("fields") instanceof List<?> fields)) continue;
            for (Object rawField : fields) {
                if (!(rawField instanceof Map<?, ?> field)) continue;
                Object rawLabel = field.containsKey("label") ? field.get("label") : field.get("id");
                String label = String.valueOf(rawLabel);
                int occurrence = occurrences.getOrDefault(label, 0) + 1;
                occurrences.put(label, occurrence);
                replacements.add(new String[] { occurrence == 1 ? label : label + " (" + occurrence + ")", String.valueOf(field.get("id")) });
            }
        }
        replacements.sort((left, right) -> Integer.compare(right[0].length(), left[0].length()));
        for (String[] replacement : replacements) {
            result = result.replaceAll("(?<![A-Za-z0-9_])" + java.util.regex.Pattern.quote(replacement[0]) + "(?![A-Za-z0-9_])", replacement[1]);
        }
        return result;
    }

    private Integer number(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private static final class FormulaParser {
        private final String source;
        private final Map<String, Object> values;
        private int position;

        FormulaParser(String source, Map<String, Object> values) {
            this.source = source;
            this.values = values;
        }

        double parse() {
            double result = expression();
            skip();
            if (position != source.length()) throw new IllegalArgumentException();
            return Math.round(result * 100.0) / 100.0;
        }

        private double expression() {
            double result = term();
            while (true) {
                skip();
                if (match('+')) result += term();
                else if (match('-')) result -= term();
                else return result;
            }
        }

        private double term() {
            double result = factor();
            while (true) {
                skip();
                if (match('*')) result *= factor();
                else if (match('/')) {
                    double divisor = factor();
                    if (divisor == 0) throw new ArithmeticException();
                    result /= divisor;
                } else return result;
            }
        }

        private double factor() {
            skip();
            if (match('-')) return -factor();
            if (match('(')) {
                double result = expression();
                expect(')');
                return result;
            }
            if (position < source.length() && Character.isDigit(source.charAt(position))) {
                int start = position++;
                while (position < source.length() && (Character.isDigit(source.charAt(position)) || source.charAt(position) == '.')) position++;
                return Double.parseDouble(source.substring(start, position));
            }
            int start = position;
            while (position < source.length() && (Character.isLetterOrDigit(source.charAt(position)) || source.charAt(position) == '_')) position++;
            if (start == position) throw new IllegalArgumentException();
            String name = source.substring(start, position);
            skip();
            if (match('(')) {
                List<Double> args = new ArrayList<>();
                args.add(expression());
                while (match(',')) args.add(expression());
                expect(')');
                return switch (name) {
                    case "floor" -> Math.floor(args.getFirst());
                    case "ceil" -> Math.ceil(args.getFirst());
                    case "round" -> Math.round(args.getFirst());
                    case "min" -> args.stream().mapToDouble(value -> Objects.requireNonNull(value).doubleValue()).min().orElseThrow();
                    case "max" -> args.stream().max(Comparator.naturalOrder()).orElseThrow();
                    case "abs" -> Math.abs(args.getFirst());
                    default -> throw new IllegalArgumentException();
                };
            }
            Object value = values.get(name);
            return value instanceof Number number ? number.doubleValue() : 0;
        }

        private boolean match(char expected) {
            skip();
            if (position < source.length() && source.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        private void expect(char expected) {
            if (!match(expected)) throw new IllegalArgumentException();
        }

        private void skip() {
            while (position < source.length() && Character.isWhitespace(source.charAt(position))) position++;
        }
    }
}
