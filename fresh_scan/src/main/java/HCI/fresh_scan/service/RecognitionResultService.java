package HCI.fresh_scan.service;

import HCI.fresh_scan.entity.RecognitionResult;
import HCI.fresh_scan.repository.RecognitionResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;

@Service
public class RecognitionResultService {

    @Autowired
    private RecognitionResultRepository repository;

    public RecognitionResult saveResult(String imagePath, Map<String, Object> detectedData) {
        RecognitionResult result = new RecognitionResult();

        // 엔티티 필드 설정
        result.setImagePath(imagePath);
        result.setRegisteredDate(LocalDate.now().toString()); // 현재 날짜를 등록 날짜로 설정
        result.setDetectedData(detectedData);

        return repository.save(result); // 데이터 저장
    }

    public List<RecognitionResult> getAllResults() {
        return repository.findAll();
    }

    public RecognitionResult getResultById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    public void deleteResult(Long id) {
        repository.deleteById(id);
    }
}
