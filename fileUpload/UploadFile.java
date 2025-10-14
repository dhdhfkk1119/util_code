package com.nationwide.nationwide_server.core.util;

import com.nationwide.nationwide_server.core.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadFile {
    private final UploadProperties uploadProperties;

    public List<String> uploadImages(MultipartFile[] multipartFiles, String dir) throws IOException{
        String fullUploadPath = Paths.get(uploadProperties.getRootDir(),dir).toString();

        createUploadDirectory(fullUploadPath);

        List<String> fileNames = new ArrayList<>();

        for(MultipartFile file : multipartFiles) {
            String originFilename = file.getOriginalFilename();
            String extension = getFileExtension(originFilename);
            String uniqueFileName = generateUniqueFileName(extension);
            Path filePath = Paths.get(fullUploadPath, uniqueFileName);
            file.transferTo(filePath);
            fileNames.add(filePath.toString());
        }

        return fileNames;
    }

    public String uploadImage(MultipartFile multipartFiles, String dir) throws IOException{
        String fullUploadPath = Paths.get(uploadProperties.getRootDir(),dir).toString();

        createUploadDirectory(fullUploadPath);
        String originFilename = multipartFiles.getOriginalFilename();
        String extension = getFileExtension(originFilename);
        String uniqueFileName = generateUniqueFileName(extension);
        Path filePath = Paths.get(fullUploadPath, uniqueFileName);
        multipartFiles.transferTo(filePath);

        return filePath.toString();
    }

    private String generateUniqueFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMDD_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return timestamp +  "_" + uuid + extension;
    }

    // 파일 확장자만 추출 해주는 메서드
    private String getFileExtension(String originFilename) {
        if(originFilename == null || originFilename.lastIndexOf(".") == -1){
            return "";
        }
        return originFilename.substring(originFilename.lastIndexOf("."));
    }

    // 폴더를 생성하는 메서드
    private void createUploadDirectory(String fullUploadPath) throws IOException{
        Path uploadPath = Paths.get(fullUploadPath);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
    }

    public void deleteProfileImage(String imagePath,String subDirName){
        if(imagePath != null && imagePath.isEmpty() == false){
            try{

                String fileName = imagePath.substring(imagePath.lastIndexOf("/")+1);

                Path filePath = Paths.get(subDirName,fileName);

                Files.deleteIfExists(filePath);

            }catch (IOException e){
                throw new RuntimeException("프로필 이미지를 삭제하지 못했습니다");
            }
        }
    }
}
