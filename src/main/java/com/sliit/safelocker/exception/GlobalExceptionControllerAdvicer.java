package com.sliit.safelocker.exception;

import com.sliit.safelocker.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
@ControllerAdvice
public class GlobalExceptionControllerAdvicer {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<CommonResponse<String>> handleDuplicatetExcepiton(SQLIntegrityConstraintViolationException e) {
        return ResponseEntity.ok(CommonResponse.<String>builder()
                .isSuccess(false).errorMessage("Record already exists").dataBundle("Record already exists")
                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.ok(CommonResponse.<String>builder()
                .isSuccess(false).errorMessage(e.getMessage()).dataBundle("Resource cannot be found")
                .build());
    }


    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<CommonResponse<String>> handleInvalidAttributeValueException(InvalidValueException e) {
        return ResponseEntity.ok(CommonResponse.<String>builder()
                .isSuccess(false).errorMessage(e.getMessage()).dataBundle("Invalid inputs")
                .build());
    }


}
