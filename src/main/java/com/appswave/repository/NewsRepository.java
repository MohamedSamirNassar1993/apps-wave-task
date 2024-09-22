package com.appswave.repository;

import com.appswave.model.entity.News;
import com.appswave.model.enums.NewsStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

  List<News> findByStatus(NewsStatusEnum status);
  List<News> findByStatusAndPublishDateBefore(NewsStatusEnum status, LocalDate date);
}
