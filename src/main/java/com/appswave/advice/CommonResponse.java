package com.appswave.advice;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {

  private int statusCode;
  private boolean error;
  private String message;
  private Date  date;
  private Object data;
  private Object otherData;
}