package com.sdakhniy.kobzar.dao;

import com.sdakhniy.kobzar.dto.IndexedLetter;
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

    public List<WordsRecord> getWords(List<String> exclude, List<IndexedLetter> includeWrongPosition, List<IndexedLetter> includeOnPosition) {
        return dslContext.selectFrom(Tables.WORDS)
                .where(
                        Tables.WORDS.LETTERS.contains(includeWrongPosition.stream().map(IndexedLetter::letter).toArray(String[]::new)),
                        DSL.not(DSL.condition("{0} && {1}", Tables.WORDS.LETTERS, DSL.val(exclude.toArray(String[]::new)))),
                        DSL.and(
                                includeOnPosition.stream()
                                        .map(entry -> DSL.arrayGet(Tables.WORDS.LETTERS, entry.index()).eq(entry.letter()))
                                        .toArray(Condition[]::new)),
                        DSL.and(
                                includeWrongPosition.stream()
                                        .map(entry -> DSL.arrayGet(Tables.WORDS.LETTERS, entry.index()).ne(entry.letter()))
                                        .toArray(Condition[]::new))).orderBy(Tables.WORDS.UNIQUE_NUMBER.desc(), Tables.WORDS.RANK.desc())
                .fetch().stream().toList();
    }
}
