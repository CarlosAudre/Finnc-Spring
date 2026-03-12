package com.project.FinnC.infra_security;

import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;
import com.project.FinnC.exeptions.EmailAlreadyExistsException;
import com.project.FinnC.exeptions.PeriodBalanceInsufficientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> emailAlreadyExistsHandler(EmailAlreadyExistsException exception){
        RestErrorMessage response = new RestErrorMessage(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(PeriodBalanceInsufficientException.class)
    private ResponseEntity<RestErrorMessage> PeriodBalanceInsufficientHandler(PeriodBalanceInsufficientException exception){
        RestErrorMessage response = new RestErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(ContainerPeriodNotFoundException.class)
    private ResponseEntity<RestErrorMessage> containerPeriodNotFoundHandler(ContainerPeriodNotFoundException exception){
        RestErrorMessage response = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
