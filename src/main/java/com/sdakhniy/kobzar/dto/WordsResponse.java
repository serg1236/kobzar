package com.sdakhniy.kobzar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WordsResponse {
    private List<String> words;
}
