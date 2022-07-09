package com.sdakhniy.kobzar.controller;

import com.sdakhniy.kobzar.dao.WorldsDao;
import com.sdakhniy.kobzar.dto.WordsResponse;
import com.sdakhniy.kobzar.model.tables.records.WordsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("words")
public class WordsController {

    @Autowired
    private WorldsDao dao;

    @GetMapping
    public WordsResponse getWords(
            @RequestParam List<String> exclude,
            @RequestParam List<String> includeOnPosition,
            @RequestParam List<String> includeWrongPosition
    ) {
        return new WordsResponse(

                dao.getWords(toLowerCase(exclude), parseRequestParam(toLowerCase(includeWrongPosition)),
                                parseRequestParam(toLowerCase(includeOnPosition))).stream()
                        .map(WordsRecord::getWord).collect(Collectors.toList())
        );
    }

    private Map<String, Integer> parseRequestParam(List<String> requestParam) {
        return requestParam.stream().map(p -> p.split(":"))
                .collect(Collectors.toMap(p -> p[0], p -> Integer.parseInt(p[1])));
    }

    private List<String> toLowerCase(List<String> letters) {
        return letters.stream()
                .map(String::toLowerCase)
                .toList();
    }
}
