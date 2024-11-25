# SystemDeSurveillance

A **real-time face detection and feature extraction system** that leverages **VGGFace**, **Apache Spark**, **Kafka**, and a **Spring Boot application**. This project processes images, extracts facial features, stores them in a database, and streams the results in a distributed system environment. It also provides functionality to compare detected faces with a dataset to find similarities.

---

## Features

- **Face Detection**: Uses `MTCNN` to detect faces from input images.
- **Feature Extraction**: Leverages the **VGGFace** model (SENet50 architecture) to extract face embeddings.
- **Face Similarity Matching**: Compares detected faces with a dataset by matching features stored in `hashed_features1.csv`.
- **Kafka Streaming**: Integrates Kafka to stream images and features across the pipeline.
- **Apache Spark**: Processes and streams data at scale with structured streaming.
- **Spring Boot**: Provides a REST API and manages Kafka communication.
- **Dataset**: Uses GT_DB and augmented with custom face images for processing.

---

## Requirements
### Environment
-- **Java 17**
-- **Python 3.9**
-- **Maven**
-- **Apache Kafka**
-- **Apache Spark**
## Key libraries include:
tensorflow, keras_vggface, mtcnn, flask, requests, numpy, pyspark
## running the project
### model
first you need to run "features_model.ipynb" This runs a Flask server on http://127.0.0.1:5000.
### kafka+spark
to run kafka use those commands:
```batch
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
.\bin\windows\kafka-server-start.bat .\config\server.properties
```
then you need to run the "spark_streaming.py" with this command:
```batch
spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.1,org.apache.kafka:kafka-clients:3.5.1 --conf "spark.pyspark.python=C:/Users/hp/.pyenv/pyenv-win/versions/3.9.0/python.exe" spark_streaming.py
```
dont forget to replace the paths with your own paths
### spring boot application
and finally run the app
```batch
mvn spring-boot:run
```
## Integration Details

### Kafka
- **Producer**: Streams input images (Base64 encoded) to Kafka.
- **Consumer**: Processes streamed data and writes back extracted features.

### Apache Spark
- Reads images from Kafka.
- Sends them to the Flask API for feature extraction.
- Streams processed data back to Kafka.

### Flask API
- Detects and extracts facial features.
- Uses **VGGFace SENet50** for embeddings.
