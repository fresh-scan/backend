package HCI.fresh_scan.repository;

import HCI.fresh_scan.entity.RecognitionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecognitionResultRepository extends JpaRepository<RecognitionResult, Long> {
}
