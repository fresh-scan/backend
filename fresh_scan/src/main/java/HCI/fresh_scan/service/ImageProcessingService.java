package HCI.fresh_scan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ImageProcessingService {

    // Python 스크립트를 실행하고 결과를 반환하는 메서드
    public Map<String, Object> processImage(File imageFile) throws Exception {
        // Python 스크립트 경로 설정
        String scriptPath = "c:/HCI_freshScan/integrated_model.py"; // Python 스크립트 파일 위치
        List<String> command = Arrays.asList(
                "python",          // Python 실행 명령
                scriptPath,        // Python 스크립트 경로
                imageFile.getAbsolutePath() // 이미지 파일 경로
        );


        //황지연 가상환경 전용 경로!!! 지우지 말아주세요!!
        // String pythonPath = "D:/kajd0/anaconda3/envs/visualBERT/python.exe"; // Python 실행 파일 절대 경로
        // String scriptPath = "D:/fresh-scan/analysis/integrated_model.py"; // Python 스크립트 파일 위치
        // List<String> command = Arrays.asList(
        //         pythonPath,        // Python 실행 경로
        //         scriptPath,        // Python 스크립트 경로
        //         imageFile.getAbsolutePath() // 이미지 파일 경로
        // );
        //////

        // ProcessBuilder로 Python 스크립트 실행
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        //황지연 가상환경 전용 경로!!! 지우지 말아주세요!!
        //processBuilder.environment().put("PATH", "D:/kajd0/anaconda3/envs/visualBERT/;D:/kajd0/anaconda3/envs/visualBERT/Scripts");

        Process process = processBuilder.start();

        // Python 스크립트 출력 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        // 에러 출력 읽기
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line);
        }

        // 종료 코드 확인
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.out.println("Python Script Error Output: " + errorOutput.toString());
            throw new RuntimeException("Python 스크립트 실행 실패. 종료 코드: " + exitCode + ", 에러: " + errorOutput.toString());
        }

        // Python 출력(JSON 문자열)을 Java Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(output.toString(), Map.class);
    }
}
