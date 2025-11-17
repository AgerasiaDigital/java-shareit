package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundException_shouldReturnErrorResponse() {
        NotFoundException exception = new NotFoundException("Not found");

        ErrorResponse response = handler.handleNotFoundException(exception);

        assertThat(response.getError(), equalTo("Not found"));
    }

    @Test
    void handleConflictException_shouldReturnErrorResponse() {
        ConflictException exception = new ConflictException("Conflict");

        ErrorResponse response = handler.handleConflictException(exception);

        assertThat(response.getError(), equalTo("Conflict"));
    }

    @Test
    void handleForbiddenException_shouldReturnErrorResponse() {
        ForbiddenException exception = new ForbiddenException("Forbidden");

        ErrorResponse response = handler.handleForbiddenException(exception);

        assertThat(response.getError(), equalTo("Forbidden"));
    }

    @Test
    void handleIllegalArgumentException_shouldReturnErrorResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ErrorResponse response = handler.handleIllegalArgumentException(exception);

        assertThat(response.getError(), equalTo("Invalid argument"));
    }

    @Test
    void handleValidationException_shouldReturnErrorResponse() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("Validation error");

        ErrorResponse response = handler.handleValidationException(exception);

        assertThat(response.getError(), equalTo("Validation failed: Validation error"));
    }

    @Test
    void handleMissingHeaderException_shouldReturnErrorResponse() {
        MissingRequestHeaderException exception = new MissingRequestHeaderException(
                "X-Sharer-User-Id",
                null
        );

        ErrorResponse response = handler.handleMissingHeaderException(exception);

        assertThat(response.getError(), equalTo("Required header is missing: X-Sharer-User-Id"));
    }

    @Test
    void handleException_shouldReturnErrorResponse() {
        Exception exception = new Exception("Internal error");

        ErrorResponse response = handler.handleException(exception);

        assertThat(response.getError(), equalTo("Internal server error: Internal error"));
    }
}