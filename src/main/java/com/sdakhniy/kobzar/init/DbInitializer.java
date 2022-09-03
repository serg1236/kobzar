package com.sdakhniy.kobzar.init;

import com.sdakhniy.kobzar.model.Tables;
import com.sdakhniy.kobzar.model.tables.records.WordsRecord;
import org.jooq.impl.DefaultCloseableDSLContext;
import org.jooq.impl.UpdatableRecordImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class DbInitializer {
    private static final String DICT_FILE_NAME = "raw_dictionary.txt";

    @Autowired
    private DefaultCloseableDSLContext dslContext;

    private static final Map<String, Double> LETTERS_RANK = new HashMap<>();

    static {
        LETTERS_RANK.put("А", 0.064);
        LETTERS_RANK.put("Б", 0.013);
        LETTERS_RANK.put("В", 0.046);
        LETTERS_RANK.put("Г", 0.013);
        LETTERS_RANK.put("Ґ", 0.000);
        LETTERS_RANK.put("Д", 0.027);
        LETTERS_RANK.put("Е", 0.042);
        LETTERS_RANK.put("Є", 0.005);
        LETTERS_RANK.put("Ж", 0.007);
        LETTERS_RANK.put("З", 0.020);
        LETTERS_RANK.put("И", 0.055);
        LETTERS_RANK.put("І", 0.044);
        LETTERS_RANK.put("Ї", 0.010);
        LETTERS_RANK.put("Й", 0.009);
        LETTERS_RANK.put("К", 0.033);
        LETTERS_RANK.put("Л", 0.027);
        LETTERS_RANK.put("М", 0.029);
        LETTERS_RANK.put("Н", 0.068);
        LETTERS_RANK.put("О", 0.086);
        LETTERS_RANK.put("П", 0.025);
        LETTERS_RANK.put("Р", 0.043);
        LETTERS_RANK.put("С", 0.037);
        LETTERS_RANK.put("Т", 0.045);
        LETTERS_RANK.put("У", 0.027);
        LETTERS_RANK.put("Ф", 0.003);
        LETTERS_RANK.put("Х", 0.011);
        LETTERS_RANK.put("Ц", 0.010);
        LETTERS_RANK.put("Ч", 0.011);
        LETTERS_RANK.put("Ш", 0.005);
        LETTERS_RANK.put("Щ", 0.004);
        LETTERS_RANK.put("Ь", 0.016);
        LETTERS_RANK.put("Ю", 0.008);
        LETTERS_RANK.put("Я", 0.019);
    }

    @PostConstruct
    public void initDb() throws IOException, URISyntaxException {
        try (var query = dslContext.selectCount().from(Tables.WORDS)) {
            if (query.fetchOne(0, int.class) > 0) {
                return;
            }
        }
        System.out.println("Before init...");
        List<String> lines = new ArrayList<>();
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(DICT_FILE_NAME)) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null; ) {
                lines.add(line);
            }
        }
        lines.stream().filter(l -> !l.isBlank())
                .map(line -> line.split("\\s")[0])
                .filter(l -> !l.isBlank() && l.length() == 5 && !l.contains("'") && !l.contains("-"))
                .distinct()
                .map(word -> {
                    WordsRecord record = dslContext.newRecord(Tables.WORDS);
                    record.setWord(word);
                    record.setLetters(word.split(""));
                    record.setUniqueNumber((int) Arrays.stream(record.getLetters()).distinct().count());
                    record.setRank(Arrays.stream(record.getLetters())
                            .map(l -> {
                                return LETTERS_RANK.get(l.toUpperCase());
                            })
                            .mapToDouble(rank -> rank)
                            .sum());
                    return record;
                })
                .forEach(UpdatableRecordImpl::store);
        System.out.println("Initialized!");
    }
}
