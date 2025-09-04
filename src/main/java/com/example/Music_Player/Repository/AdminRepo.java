package com.example.Music_Player.Repository;

import com.example.Music_Player.Config.DynamoDBEnhancedConfig;
import com.example.Music_Player.Model.Admin;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
@Repository
public class AdminRepo {

    @Autowired
    DynamoDbEnhancedClient client ;

    private  DynamoDbTable<Admin> adminTable;

    @PostConstruct
    public void init() {
        this.adminTable = client.table("Admin", TableSchema.fromBean(Admin.class));
    }
    public Admin saveAdmin(Admin admin) {
        adminTable.putItem(admin);
        return admin;
    }
    public Admin getAdminByEmail(String email) {
        return adminTable.getItem(r -> r.key(k -> k.partitionValue(email)));
    }
    public void deleteAdmin(String email) {
        adminTable.deleteItem(r -> r.key(k -> k.partitionValue(email)));
    }
}
