package com.appswave.advice;

import java.util.Date;

import com.appswave.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public final CommonResponse handleUserException(UserException ex) {
    return new CommonResponse(500, true, ex.getMessage(), new Date(), ex.getData(), ex.getCause());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public final CommonResponse handleIllegalArgumentException(IllegalArgumentException ex) {
    return new CommonResponse(500, true, ex.getMessage(), new Date(), ex.getMessage(), ex.getCause());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public final CommonResponse handleEmailNotFoundException(UsernameNotFoundException ex) {
    return new CommonResponse(404, true,ex.getMessage(), new Date(), ex.getMessage(), ex.getCause());
  }

  @ExceptionHandler(ExpiredJwtException.class)
  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  public final CommonResponse handleExpiredJwtException(ExpiredJwtException ex) {
    log.error("Error from people-sensing application : {} ", ex.getMessage());
    return new CommonResponse(401, true,ex.getMessage(), new Date(), ex.getMessage(), ex.getCause());
  }
}