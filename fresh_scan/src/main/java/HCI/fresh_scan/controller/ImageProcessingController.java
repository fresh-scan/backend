package HCI.fresh_scan.controller;

import HCI.fresh_scan.service.ImageProcessingService;
import HCI.fresh_scan.service.RecognitionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ImageProcessingController {

    @Autowired
    private ImageProcessingService imageProcessingService;

    @Autowired
    private RecognitionResultService recognitionResultService;

    @PostMapping("/process-images")
    public String processImages(@RequestParam("files") List<MultipartFile> files) throws Exception {
        for (MultipartFile file : files) {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            try {
                // Python 스크립트 실행 및 결과 받기
                Map<String, Object> result = imageProcessingService.processImage(tempFile);

                // 결과 저장
                recognitionResultService.saveResult(tempFile.getAbsolutePath(), result);
            } finally {
                tempFile.delete(); // 임시 파일 삭제
            }
        }

        // 저장된 결과를 보여주는 페이지로 리다이렉트
        return "redirect:/api/results";
    }
}
