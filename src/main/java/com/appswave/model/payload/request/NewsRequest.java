package com.appswave.model.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewsRequest {
	@NotNull
	private String title;
	@NotNull
	private String titleArabic;
	@NotNull
	private String description;
	@NotNull
	private String descriptionArabic;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate publishDate;
	@NotNull
	private MultipartFile image;
}
