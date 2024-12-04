package HCI.fresh_scan.controller;

import HCI.fresh_scan.entity.RecognitionResult;
import HCI.fresh_scan.service.ImageProcessingService;
import HCI.fresh_scan.service.RecognitionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
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

        // 이름 매핑
        Map<String, String> nameMapping = Map.of(
                "tomato", "토마토",
                "tofu", "두부",
                "sauce", "토마토 케첩",
                "pimang", "피망",
                "carrot", "당근",
                "gaji", "가지",
                "cabbage", "양배추",
                "beef", "소고기",
                "milk", "우유",
                "fish", "생선"
        );

        // 라벨 없는 재료의 유통기한 설정 (일 단위)
        Map<String, Integer> labelFreeExpirationDays = Map.of(
                "tomato", 14,  // 2주
                "pimang", 14,  // 2주
                "carrot", 21,  // 3주
                "gaji", 10,    // 10일
                "cabbage", 30, // 1달
                "beef", 7,     // 1주
                "fish", 5      // 5일
        );

        List<File> tempFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);
            tempFiles.add(tempFile);
        }

        try {
            List<Map<String, Object>> results = imageProcessingService.processImages(tempFiles);
            for (int i = 0; i < results.size(); i++) {
                Map<String, Object> result = results.get(i);
                RecognitionResult savedResult = recognitionResultService.saveResult(tempFiles.get(i).getAbsolutePath(), result);

                ids.add(savedResult.getId());
                registeredDates.add(savedResult.getRegisteredDate());

                List<String> detectedLabels = (List<String>) result.get("detected_labels");
                String ingredient = detectedLabels.isEmpty() ? null : detectedLabels.get(0);
                String mappedName = ingredient == null ? null : nameMapping.getOrDefault(ingredient, ingredient);

                // 유통기한 계산
                if (ingredient != null && labelFreeExpirationDays.containsKey(ingredient)) {
                    LocalDate expiryDate = LocalDate.now().plusDays(labelFreeExpirationDays.get(ingredient));
                    expiryDates.add(expiryDate.toString());
                } else {
                    List<String> expirationDates = (List<String>) result.get("expiration_dates");
                    expiryDates.add(expirationDates.isEmpty() ? null : expirationDates.get(0));
                }

                names.add(mappedName);
            }
        } finally {
            for (File tempFile : tempFiles) {
                tempFile.delete();
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
