package com.sdakhniy.kobzar.dao;

import com.sdakhniy.kobzar.model.Tables;
import com.sdakhniy.kobzar.model.tables.records.WordsRecord;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultCloseableDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class WorldsDao {

    @Autowired
    private DefaultCloseableDSLContext dslContext;

    public List<WordsRecord> getWords(List<String> exclude, Map<String, Integer> includeWrongPosition, Map<String, Integer> includeOnPosition) {
        return dslContext.selectFrom(Tables.WORDS)
                .where(
                        Tables.WORDS.LETTERS.contains(includeWrongPosition.keySet().toArray(new String[]{})),
                        DSL.not(DSL.condition("{0} && {1}", Tables.WORDS.LETTERS, DSL.val(exclude.toArray(String[]::new)))),
                        DSL.and(
                                includeOnPosition.entrySet().stream()
                                        .map(entry -> DSL.arrayGet(Tables.WORDS.LETTERS, entry.getValue()).eq(entry.getKey()))
                                        .toArray(Condition[]::new)),
                        DSL.and(
                                includeWrongPosition.entrySet().stream()
                                        .map(entry -> DSL.arrayGet(Tables.WORDS.LETTERS, entry.getValue()).ne(entry.getKey()))
                                        .toArray(Condition[]::new))).orderBy(Tables.WORDS.RANK.desc())
                .fetch().stream().toList();
    }
}
