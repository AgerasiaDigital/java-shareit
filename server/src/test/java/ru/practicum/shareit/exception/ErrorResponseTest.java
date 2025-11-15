package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        ErrorResponse response = new ErrorResponse("Error message");

        assertThat(response.getError(), equalTo("Error message"));
    }

    @Test
    void testErrorResponseSetter() {
        ErrorResponse response = new ErrorResponse();
        response.setError("Error message");

        assertThat(response.getError(), equalTo("Error message"));
    }
}