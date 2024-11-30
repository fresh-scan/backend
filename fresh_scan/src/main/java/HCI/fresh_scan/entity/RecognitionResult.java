package HCI.fresh_scan.entity;

import HCI.fresh_scan.JsonConverter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;

import java.util.Map;

@Entity
public class RecognitionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imagePath; // 이미지 경로

    @Column(nullable = false)
    private String registeredDate; // 등록 날짜

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> detectedData; // Python 결과 데이터

    // Getter 및 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Map<String, Object> getDetectedData() {
        return detectedData;
    }

    public void setDetectedData(Map<String, Object> detectedData) {
        this.detectedData = detectedData;
    }
}
