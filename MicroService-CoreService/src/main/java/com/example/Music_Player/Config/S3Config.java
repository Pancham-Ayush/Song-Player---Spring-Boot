package com.example.Music_Player.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtAsyncClient;

@Configuration
public class S3Config {
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.credentials.access-key}")
    String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    String secretKey;
    @Value("${cloud.s3.accelerated:false}")
    boolean accelerated;

    @Bean
    public S3AsyncClient s3Client1() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return  S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
    @Bean
    public S3Client s3Client() {

//        S3Configuration s3Configuration = S3Configuration.builder()
//                .accelerateModeEnabled(accelerated)
//                .build();

        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client
                .builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
//                .serviceConfiguration(s3Configuration)
                .build();
    }
}
