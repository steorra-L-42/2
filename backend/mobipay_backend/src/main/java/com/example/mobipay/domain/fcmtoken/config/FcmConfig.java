package com.example.mobipay.domain.fcmtoken.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FcmConfig {

    @Value("${firebase.config}")
    private String firebaseConfig;

    @Value("${firebase.project.id}")
    private String projectId;

    @PostConstruct
    public void initialize() throws IOException {
//        ClassPathResource resource = new ClassPathResource("firebase/mobipay-firebase-key.json");
//        if (!resource.exists()) {
//            throw new IllegalStateException("Firebase 서비스 계정 키 파일을 찾을 수 없습니다");
//        }

        try (InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8))) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}