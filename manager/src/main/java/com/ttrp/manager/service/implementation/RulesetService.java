package com.ttrp.manager.service.implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttrp.manager.dto.character.CharacterCalculationRequest;
import com.ttrp.manager.dto.character.CharacterCalculationResponse;
import com.ttrp.manager.service.declaration.IRulesetService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class RulesetService implements IRulesetService {
    private static final Map<String, String> RULESET_FILES = Map.of(
            "DND_5E", "dnd5e.json",
            "CALL_OF_CTHULHU_7E", "callOfCthulhu7e.json",
            "PATHFINDER_2E", "pathfinder2e.json",
            "CUSTOM", "custom.json"
    );

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Override
    public List<Map<String, Object>> getRulesets() {
        return RULESET_FILES.keySet().stream().map(this::loadRuleset).toList();
    }

    @Override
    public CharacterCalculationResponse calculate(CharacterCalculationRequest request) {
        Map<String, Object> ruleset = request.rulesetSchema() != null
                ? request.rulesetSchema()
                : loadRuleset(Optional.ofNullable(request.rulesetKey()).orElse("CUSTOM"));
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
                    derived.put(id, evaluate(String.valueOf(formula), ruleset, context));
                }
            }
        }
        Integer hp = number(derived.get("hitPoints"));
        return new CharacterCalculationResponse(derived, hp);
    }

    private Map<String, Object> loadRuleset(String key) {
        String filename = RULESET_FILES.get(key);
        if (filename == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Unknown ruleset: " + key);
        }
        try {
            return objectMapper.readValue(
                    resourceLoader.getResource("classpath:character/rulesets/" + filename).getInputStream(),
                    new TypeReference<>() {});
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load ruleset " + key, exception);
        }
    }

    private Object evaluate(String formula, Map<String, Object> ruleset, Map<String, Object> values) {
        try {
            return new FormulaParser(formulaWithIds(formula, ruleset), values).parse();
        } catch (RuntimeException exception) {
            return null;
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
