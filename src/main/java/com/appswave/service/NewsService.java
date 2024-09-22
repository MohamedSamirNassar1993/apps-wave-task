package com.appswave.service;

import com.appswave.model.entity.News;
import com.appswave.model.entity.User;
import com.appswave.model.enums.NewsStatusEnum;
import com.appswave.model.enums.RoleEnum;
import com.appswave.model.payload.request.NewsRequest;
import com.appswave.repository.NewsRepository;
import com.appswave.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;

@Service
@Transactional
@Slf4j
public class NewsService {

    @Value("${files.path}")
    private String path;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    public News createNews(NewsRequest request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        News news = new News();
        news.setCreatedUser(user);
        news.setCreationTime(LocalDateTime.now());
        news.setTitle(request.getTitle());
        news.setTitleArabic(request.getTitleArabic());
        news.setDescription(request.getDescription());
        news.setDescriptionArabic(request.getDescriptionArabic());
        news.setPublishDate(request.getPublishDate());
        news.setStatus(NewsStatusEnum.PENDING);
        news.setImageUrl(createFileLocally(request.getImage(), path));
        newsRepository.save(news);
        return news;
    }

    public String createFileLocally(MultipartFile file, String path) {
        int index = file.getOriginalFilename().lastIndexOf(".");
        String fileExtension = file.getOriginalFilename().substring(index);
        String fileName = file.getOriginalFilename().substring(0, index);
        String finalFileName = fileName + "_" + DateTimeFormatter.ofPattern("uuuuMMddHHmmss").format(LocalDateTime.now()) + fileExtension;
        createDirAndFile(file, finalFileName, path);
        return finalFileName;
    }

    public void createDirAndFile(MultipartFile file, String fileName, String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File createdFile = new File(directory.getAbsolutePath(), fileName);
        try {
            file.transferTo(createdFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<News> findPendingNews() {
        return newsRepository.findByStatus(NewsStatusEnum.PENDING);
    }

    public List<News> findApprovedNews() {
        return newsRepository.findByStatus(NewsStatusEnum.APPROVED);
    }

    public List<News> findApprovedPublishedNews() {
        return newsRepository.findByStatusAndPublishDateBefore(NewsStatusEnum.APPROVED, LocalDate.now());
    }

    public News approveNews(Long id, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        if (news.getStatus() == NewsStatusEnum.PENDING) {
            news.setStatus(NewsStatusEnum.APPROVED);
            news.setApprovedUser(user);
            news.setApprovalTime(LocalDateTime.now());
            return newsRepository.save(news);
        } else {
            throw new IllegalStateException("News is not in a PENDING state");
        }
    }

    public News rejectNews(Long id, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        if (news.getStatus() == NewsStatusEnum.PENDING) {
            news.setStatus(NewsStatusEnum.REJECTED);
            news.setRejectionUser(user);
            news.setRejectionTime(LocalDateTime.now());
            return newsRepository.save(news);
        } else {
            throw new IllegalStateException("News is not in a PENDING state");
        }
    }

    public News deleteNews(Long id, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        if(user.getRole().getName() == RoleEnum.ADMIN){
            news.setDeleted(true);
            news.setDeletionUser(user);
            return newsRepository.save(news);
        } else if (user.getRole().getName().equals(RoleEnum.CONTENT_WRITER)) {
            if(news.getStatus() == NewsStatusEnum.PENDING) {
                news.setDeleted(true);
                news.setDeletionUser(user);
                return newsRepository.save(news);
            }else {
                throw new IllegalStateException("Cannot delete!!");
            }
        } else {
            throw new IllegalStateException("Cannot delete!!");
        }
    }
}