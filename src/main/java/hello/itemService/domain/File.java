package hello.itemService.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class File {
    private String filePath;
    private String fileName;
    private long fileSize;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fileDate;
    private int boardId;
}