package com.pospick.pospick.service;

import com.pospick.pospick.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 파일 업로드/삭제 서비스
 * - 상품 이미지를 S3에 업로드하고 URL 반환
 * - 상품 삭제 시 S3 이미지도 함께 삭제
 */
@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucket;

    public S3Service(
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket}") String bucket) {

        this.bucket = bucket;

        // AWS 자격 증명으로 S3 클라이언트 생성
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    /**
     * 이미지 업로드
     * - 파일을 S3에 업로드하고 접근 가능한 URL 반환
     *
     * @param file 업로드할 이미지 파일
     * @return S3에 저장된 이미지 URL
     */
    public String upload(MultipartFile file) {
        // 파일 검증
        if (file.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
        }

        // 파일 확장자 추출 (예: image.png → png)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 파일명 중복 방지를 위해 UUID로 고유한 파일명 생성
        // 예: products/550e8400-e29b-41d4-a716-446655440000.png
        String key = "products/" + UUID.randomUUID() + extension;

        try {
            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 업로드된 파일의 공개 URL 반환
            // 형식: https://버킷이름.s3.리전.amazonaws.com/파일경로
            String url = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;
            log.info("S3 업로드 완료: {}", url);
            return url;

        } catch (IOException e) {
            log.error("S3 업로드 실패", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }
    }

    /**
     * 이미지 삭제
     * - S3에서 해당 URL의 파일 삭제
     *
     * @param imageUrl 삭제할 이미지 URL
     */
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        // URL에서 S3 키 추출
        // https://버킷.s3.리전.amazonaws.com/products/uuid.png → products/uuid.png
        String key = imageUrl.substring(imageUrl.indexOf("products/"));

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            log.info("S3 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 삭제 실패: {}", key, e);
        }
    }
}
