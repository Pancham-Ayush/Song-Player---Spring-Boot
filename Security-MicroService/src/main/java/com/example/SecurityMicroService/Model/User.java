package com.example.SecurityMicroService.Model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    private String name;
    private String email;
    private String password;
    private String role;

    @DynamoDbPartitionKey
    public String getEmail() {
        return email;
    }


}
