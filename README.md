
# Spring AI Music Player
Spring Music Player is a highly scalable, event-driven microservices-based music streaming platform built using Java and Spring Boot. It is designed beyond a traditional API-driven music player and focuses on performance, resilience, and distributed system principles.



## Features

- Implements a custom data extraction mechanism for audio streaming.
- Enhanced search optimization via Elasticsearch with redis layer for efficiency
- YouTube AI verification and meta data extraction
- Services are independently deployable and scalable.
- Youtube search Engine implementation via Feign



## Tech Stack
**Backend:** Java, Spring Boot, AWS - S3, DynamoDb, Elastic Search, Kafka, Redis, Spring AI (MistralAI), OAuth (Google), JWT, Microservice Architecture (Eureka), Youtube Search Client, Server Side Emitter, Docker, JDK-25
## Architecture

                                    Client Layer
                                        |
                                        v
                                API Gateway Layer
                                        |
                                        v
    Security Layer <----------> Authorization Layer ( JWT )
            |                           |
            v                           v
        Google Oauth             Microservices Layer
                                        |
                                        +--> Player / Streaming Service
                                        |        |
                                        |        v
                                        |    Redis Cache (Playback State & Chunks)
                                        |        |
                                        |        v
                                        |    Media Service
                                        |        |
                                        |        v
                                        |    Amazon S3 (Audio Files)
                                        |
                        AI Service  <---+  
                             |          |        
                             v          |
                          Spring AI     |        
                             |          |
                             v          |
                     Mistral AI Model   |    
                                        |         
                                        |
                                        +--> Search Service
                                        |        |
                                        |        v
                                        |    Elasticsearch
                                        |
                                        v
                             OTHER Services (8+ micorservies)
                                   
                                


## Environment Variables
To run this project, you will need to add the following environment variables to your system 

```bash
    # --- Spring AI (Mistral AI) ---
SPRING_AI_MISTRAL_API_KEY=
SPRING_AI_MISTRAL_MODEL=
SPRING_AI_CHAT_CLIENT_ENABLED=

# --- Eureka ---
EUREKA_URL=

# --- AWS S3 ---
AWS_ACCESS_KEY=
AWS_SECRET_KEY=
AWS_REGION=
AWS_BUCKET=
S3_ACCELERATION=
//Create User, Song, Playlist , Admin table with respective Primary & Seconday Key

# --- Apache Kafka ---
SPRING_KAFKA_BOOTSTRAP_SERVERS=
SPRING_KAFKA_TOPIC_YT=
SPRING_KAFKA_TOPIC_OPENSEARCH=

# --- Redis ---
REDIS_HOST=
REDIS_PORT=

# --- Elasticsearch ---
ELASTICSEARCH_HOST=
ELASTICSEARCH_PORT=
ELASTICSEARCH_SCHEME=
ELASTICSEARCH_INDEX=

# --- YouTube API ---
YOUTUBE_API_KEY=

# --- Google OAuth ---
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

# --- JWT ---
JWT_SECRET_KEY=

```


## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://panchamayush.codes)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/pancham-ayush/)


## Support

For support, email panchamayush@gmail.com

