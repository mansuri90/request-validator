package com.theadex.requestvalidator.exceptionhandler;

import com.theadex.requestvalidator.dto.output.ErrorDto;
import com.theadex.requestvalidator.exception.BadRequestException;
import com.theadex.requestvalidator.exception.MissingRequiredFieldsException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

@ControllerAdvice
@ResponseBody
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final ErrorDto errorDto = new ErrorDto(Instant.now(), "MALFORMED_OR_EMPTY_REQUEST_BODY", request.getDescription(false));
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errorMap = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errorMap.put(fieldName, errorMessage);
        });

        final ErrorDto errorDto = new ErrorDto(Instant.now(), errorMap, request.getDescription(false));

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Object responseBody = body;
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            logger.error("Unhandled Exception:", ex);
            responseBody = "INTERNAL_SERVER_ERROR";
        }
        final ErrorDto errorDto = new ErrorDto(Instant.now(), responseBody, request.getDescription(false));
        return new ResponseEntity<>(errorDto, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getParameterName() + " request parameter is required";
        final ErrorDto errorDto = new ErrorDto(Instant.now(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = ex.getName() + " parameter must be convertible to type " + ex.getRequiredType();
        return new ErrorDto(Instant.now(), message, request.getDescription(false));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequiredFieldsException.class)
    public ErrorDto handleMissingRequiredFieldsException(MissingRequiredFieldsException ex, WebRequest request) {
        return new ErrorDto(Instant.now(), ex.getFieldNameToErrorMessageMap(), request.getDescription(false));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException ex, WebRequest request) {
        final HttpStatus httpStatus = ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class))
                .map(ResponseStatus::code).orElse(HttpStatus.BAD_REQUEST);
        final ErrorDto errorDto = new ErrorDto(Instant.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDto, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleTheRest(Exception ex, WebRequest request) {
        logger.error("Unhandled Exception:", ex);
        final HttpStatus httpStatus = ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class))
                .map(ResponseStatus::code).orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        final ErrorDto errorDto = new ErrorDto(Instant.now(), "INTERNAL_SERVER_ERROR", request.getDescription(false));
                return new ResponseEntity<>(errorDto, httpStatus);
    }
}
