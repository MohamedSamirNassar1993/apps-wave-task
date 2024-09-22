package com.appswave.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserException  extends RuntimeException{

  private static final long serialVersionUID = 1L;
  private boolean error;
  private String message;
  private Object data;
}