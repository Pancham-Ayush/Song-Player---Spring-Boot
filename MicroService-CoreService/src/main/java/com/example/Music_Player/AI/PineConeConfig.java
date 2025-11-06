
package com.example.Music_Player.AI;

import io.pinecone.clients.Pinecone;
import io.pinecone.configs.PineconeConfig;
import io.pinecone.configs.PineconeConnection;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PineConeConfig {
    @Value("${spring.ai.vectorstore.pinecone.api-key}")
    String pineconeKey;
    @Value("${spring.ai.vectorstore.pinecone.index-name}")
    String indexName;
    @Value("${spring.ai.vectorstore.pinecone.host}")
    private String host;

    @Bean({"vector"})
    public VectorStore vectorStore(MistralAiEmbeddingModel embeddingModel) {
        return PineconeVectorStore.builder(embeddingModel).apiKey(this.pineconeKey).indexName(this.indexName).build();
    }

    @Bean
    Pinecone pinecone() {
        return (new Pinecone.Builder(this.pineconeKey)).build();
    }

    @Bean
    public PineconeConfig pineconeConfig() {
        PineconeConfig config = new PineconeConfig(this.pineconeKey);
        config.setHost(this.host);
        config.setApiKey(this.pineconeKey);
        config.setTLSEnabled(true);
        return config;
    }

    @Bean
    public PineconeConnection pineconeConnection(PineconeConfig config) {
        return new PineconeConnection(config);
    }
}
