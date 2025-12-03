package com.gler.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for text replacement API.
 * Handles /replace endpoint with validation and transformation rules.
 */
@RestController
@RequestMapping("/api/v1")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Text Replace", description = "Text replacement operations")
public class TextReplaceController {

    /**
     * Replace the first and last characters of the input text according to rules:
     * - length < 2 => 400 Bad Request
     * - length == 2 => 200 OK with empty body
     * - length > 2 => first character replaced with '*' and last with '$'
     *
     * @param text the input text supplied as query parameter
     * @return ResponseEntity with transformed string or appropriate status
     */
    @Operation(summary = "Replace first and last character", description = "Transforms the provided text according to assignment rules")
    @GetMapping("/replace")
    public ResponseEntity<String> replace(@RequestParam String text) {

        // decode because Cucumber sends URL-encoded values
        text = URLDecoder.decode(text, StandardCharsets.UTF_8);

        if (text.length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        if (text.length() == 2) {
            return ResponseEntity.ok("");
        }

        String output = "*" + text.substring(1, text.length() - 1) + "$";
        return ResponseEntity.ok(output);
    }
}
