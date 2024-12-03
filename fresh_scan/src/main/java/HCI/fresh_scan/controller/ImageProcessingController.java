package HCI.fresh_scan.controller;

import HCI.fresh_scan.entity.RecognitionResult;
import HCI.fresh_scan.service.ImageProcessingService;
import HCI.fresh_scan.service.RecognitionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    public Map<String, Object> processImages(@RequestParam("files") List<MultipartFile> files) throws Exception {
        // 리스트 초기화
        List<Long> ids = new ArrayList<>();
        List<String> registeredDates = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> expiryDates = new ArrayList<>();

        Map<String, String> nameMapping = new HashMap<>();
        nameMapping.put("tomato", "토마토");
        nameMapping.put("tofu", "두부");
        nameMapping.put("sauce", "토마토 케첩");
        nameMapping.put("pimang", "피망");
        nameMapping.put("carrot", "당근");
        nameMapping.put("gaji", "가지");
        nameMapping.put("cabbage", "양배추");
        nameMapping.put("beef", "소고기");
        nameMapping.put("milk", "우유");
        nameMapping.put("fish", "생선");

        for (MultipartFile file : files) {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            try {
                // Python 스크립트 실행 및 결과 받기
                Map<String, Object> result = imageProcessingService.processImage(tempFile);

                // 저장된 RecognitionResult 객체 반환
                RecognitionResult savedResult = recognitionResultService.saveResult(tempFile.getAbsolutePath(), result);

                // 데이터 분리 및 리스트에 추가
                ids.add(savedResult.getId());
                registeredDates.add(savedResult.getRegisteredDate());

                // detected_labels와 expiration_dates 처리
                List<String> detectedLabels = (List<String>) savedResult.getDetectedData().get("detected_labels");
                if (!detectedLabels.isEmpty()) {
                    String originalName = detectedLabels.get(0);
                    // 매핑 테이블에서 한글 이름으로 변환
                    String mappedName = nameMapping.getOrDefault(originalName, originalName);
                    names.add(mappedName);
                } else {
                    names.add(null);
                }

                List<String> expirationDates = (List<String>) savedResult.getDetectedData().get("expiration_dates");
                expiryDates.add(expirationDates.isEmpty() ? null : expirationDates.get(0));
            } finally {
                tempFile.delete(); // 임시 파일 삭제
            }
        }

        // Map에 리스트 추가
        Map<String, Object> response = new HashMap<>();
        response.put("id", ids);
        response.put("registeredDate", registeredDates);
        response.put("name", names);
        response.put("expiryDate", expiryDates);

        return response;
    }

}
