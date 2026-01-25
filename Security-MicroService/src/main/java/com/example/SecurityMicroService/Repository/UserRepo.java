package com.example.SecurityMicroService.Repository;

import com.example.SecurityMicroService.Model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class UserRepo {

    private final DynamoDbTable<User> userTable;

    public UserRepo(DynamoDbEnhancedClient enhancedClient) {
        this.userTable = enhancedClient.table("User", TableSchema.fromBean(User.class));
    }

    public User save(User user) {
        userTable.putItem(user);
        return user;
    }

    public User findByEmail(String email) {
        Key key = Key.builder().partitionValue(email).build();
        return userTable.getItem(key);
    }

}