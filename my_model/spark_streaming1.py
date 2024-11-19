import requests
from pyspark.sql import SparkSession
from pyspark.sql.functions import udf, col, to_json, struct
from pyspark.sql.types import StringType
import base64
import gzip
import io
import json
import logging
from concurrent.futures import ThreadPoolExecutor

# Configuration du logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Création de la session Spark
spark = SparkSession.builder \
    .appName("KafkaImageProcessor") \
    .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.1,org.apache.kafka:kafka-clients:3.5.1") \
    .config("spark.driver.extraJavaOptions", "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dlog4j.logger.org.apache.kafka=DEBUG") \
    .config("spark.executor.extraJavaOptions", "-Dlog4j.logger.org.apache.kafka=DEBUG") \
    .getOrCreate()

url = 'http://localhost:5000/extract_features'
executor = ThreadPoolExecutor(max_workers=8)  # Ajuster le nombre de threads selon les capacités de votre machine

def process_and_save_image(base64_compressed_image):
    try:
        # Decode Base64
        compressed_data = base64.b64decode(base64_compressed_image)
        
        # Decompress GZIP
        buffer = io.BytesIO(compressed_data)
        with gzip.GzipFile(fileobj=buffer, mode='rb') as f:
            response = requests.post(url, files={'image': f})
            response.raise_for_status()
            response_json = response.json()
            features = response_json.get('features', [])
            return json.dumps(features)
    except Exception as e:
        logger.error(f"Error processing image: {e}")
        return json.dumps([])

# Register the UDF
process_and_save_image_udf = udf(process_and_save_image, StringType())

# Read from Kafka
df = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe", "my-topic") \
    .option("checkpointLocation", "/tmp/spark_checkpoints") \
    .option("startingOffsets", "latest") \
    .option("maxOffsetsPerTrigger", 100) \
    .load()

# Process the images
processed_df = df.select(
    process_and_save_image_udf(col("value").cast("string")).alias("features")
)

# Define the foreachBatch function to print results to the console
def print_batch(df, epoch_id):
    df.show(truncate=False)
    # Write the results back to Kafka
    df.select(to_json(struct("features")).alias("value")) \
        .write \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .option("topic", "test-topic") \
        .save()

# Start the streaming query with foreachBatch
query = processed_df.writeStream \
    .outputMode("append") \
    .foreachBatch(print_batch) \
    .start()

query.awaitTermination()
