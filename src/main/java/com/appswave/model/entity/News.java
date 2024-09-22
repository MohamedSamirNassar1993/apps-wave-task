package com.appswave.model.entity;

import com.appswave.model.enums.NewsStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
@Where(clause = "isDeleted <> true")
@SQLDelete(sql = "UPDATE News SET isDeleted = true WHERE id = ?" , check = ResultCheckStyle.COUNT)
public class News {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User createdUser;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approval_user_id", referencedColumnName = "id")
  private User approvedUser;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rejection_user_id", referencedColumnName = "id")
  private User rejectionUser;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deletion_user_id", referencedColumnName = "id")
  private User deletionUser;
  private LocalDateTime approvalTime;
  private LocalDateTime rejectionTime;
  private LocalDateTime creationTime;
  private LocalDateTime modificationTime;
  private boolean isDeleted = false;
  private String title;
  private String titleArabic;
  private String description;
  private String descriptionArabic;
  private LocalDate publishDate;
  private String imageUrl;
  @Enumerated(EnumType.STRING)
  private NewsStatusEnum status = NewsStatusEnum.PENDING;  // Default status is PENDING
}