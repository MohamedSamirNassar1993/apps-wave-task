package com.appswave.model.enums;

public enum NewsStatusEnum {
  PENDING,    // Waiting for admin approval
  APPROVED,   // Approved by admin, visible to normal users
  REJECTED    // Rejected by admin, can be deleted or modified
}