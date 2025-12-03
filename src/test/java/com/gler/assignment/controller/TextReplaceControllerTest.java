package com.gler.assignment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class TextReplaceControllerTest {

    TextReplaceController controller = new TextReplaceController();

    @Test
    void lessThanTwoReturnsBadRequest() {
        ResponseEntity<String> r = controller.replace("a");
        assertEquals(400, r.getStatusCode().value());
    }

    @Test
    void equalTwoReturnsEmptyBody() {
        ResponseEntity<String> r = controller.replace("ab");
        assertEquals(200, r.getStatusCode().value());
        assertEquals("", r.getBody());
    }

    @Test
    void longerReplaceFirstAndLast() {
        assertEquals("*lephan$", controller.replace("elephant").getBody());
        assertEquals("*om$", controller.replace("home").getBody());
        assertEquals("*b$", controller.replace("abc").getBody());
        assertEquals("*bc#20xy$", controller.replace("abc#20xyz").getBody());
    }
}
