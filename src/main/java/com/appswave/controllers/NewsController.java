package com.appswave.controllers;

import com.appswave.model.dto.UserDTO;
import com.appswave.model.entity.News;
import com.appswave.model.payload.request.NewsRequest;
import com.appswave.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@Slf4j
public class NewsController {
    
    @Autowired
    private NewsService newsService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('ADMIN','CONTENT_WRITER')")
    public ResponseEntity<News> createNews(@ModelAttribute NewsRequest news) {
        UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        News createdNews = newsService.createNews(news,userDTO.getId());
        return ResponseEntity.ok(createdNews);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> approveNews(@PathVariable Long id) {
        UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newsService.approveNews(id,userDTO.getId());
        return ResponseEntity.ok("News approved successfully!");
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> rejectNews(@PathVariable Long id) {
        UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newsService.rejectNews(id,userDTO.getId());
        return ResponseEntity.ok("News rejected!");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ADMIN','CONTENT_WRITER')")
    public ResponseEntity<List<News>> getPendingNews() {
        List<News> pendingNews = newsService.findPendingNews();
        return ResponseEntity.ok(pendingNews);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','CONTENT_WRITER')")
    public ResponseEntity<List<News>> getApprovedNews() {
        List<News> approvedNews = newsService.findApprovedNews();
        return ResponseEntity.ok(approvedNews);
    }

    @GetMapping("/approved-published")
    public ResponseEntity<List<News>> getApprovedPublishedNews() {
        List<News> approvedNews = newsService.findApprovedPublishedNews();
        return ResponseEntity.ok(approvedNews);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CONTENT_WRITER')")
    public ResponseEntity<News> deleteNews(@PathVariable Long id) {
        UserDTO userDTO = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(newsService.deleteNews(id,userDTO.getId()));
    }
}
