package hello.itemService.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${spring.servlet.multipart.location}")
    private String path;

    @GetMapping("/download")
    public void fileDownload(@RequestParam String filePath,
                             @RequestParam String fileName,
                             HttpServletResponse response) {
        File file = new File(path + filePath);
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream = null;
        ServletOutputStream servletOutputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            servletOutputStream = response.getOutputStream();
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            response.setContentLength((int) file.length());
            int read = 0;
            while ((read = bufferedInputStream.read()) != -1) {
                servletOutputStream.write(read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception e) {
                throw new IllegalStateException("서버 오류로 다운로드 로직을 종료하는데 실패하였습니다.");
            }
        }
    }
    // 사진(파일) 불러오기
    @GetMapping("/img")
    public ResponseEntity<Resource> getMemberImage(@RequestParam String filePath) {

        Resource resource = new FileSystemResource(path + filePath);
        HttpHeaders headers = new HttpHeaders();
        Path file = null;
        try {
            file = Paths.get(path + filePath);
            headers.add("Content-Type", Files.probeContentType(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
