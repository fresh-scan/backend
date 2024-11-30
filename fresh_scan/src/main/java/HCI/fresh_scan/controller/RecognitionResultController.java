package HCI.fresh_scan.controller;

import HCI.fresh_scan.entity.RecognitionResult;
import HCI.fresh_scan.service.RecognitionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class RecognitionResultController {

    @Autowired
    private RecognitionResultService resultService;

    // Create (저장)
    @PostMapping
    public RecognitionResult saveResult(@RequestBody RecognitionResult result) {
        return resultService.saveResult(result.getImagePath(), result.getDetectedData());
    }

    @GetMapping
    public Map<String, Object> getAllResults() {
        List<RecognitionResult> results = resultService.getAllResults();

        // 각 필드에 대한 리스트 생성
        List<Long> ids = new ArrayList<>();
        List<String> registeredDates = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> expiryDates = new ArrayList<>();

        for (RecognitionResult result : results) {
            ids.add(result.getId());
            registeredDates.add(result.getRegisteredDate());

            // detected_labels와 expiration_dates 처리
            List<String> detectedLabels = (List<String>) result.getDetectedData().get("detected_labels");
            names.add(detectedLabels.isEmpty() ? null : detectedLabels.get(0));

            List<String> expirationDates = (List<String>) result.getDetectedData().get("expiration_dates");
            expiryDates.add(expirationDates.isEmpty() ? null : expirationDates.get(0));
        }

        // Map에 리스트 추가
        Map<String, Object> response = new HashMap<>();
        response.put("id", ids);
        response.put("registeredDate", registeredDates);
        response.put("name", names);
        response.put("expiryDate", expiryDates);

        return response;
    }


    // Delete
    @DeleteMapping("/{id}")
    public void deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
    }
}
