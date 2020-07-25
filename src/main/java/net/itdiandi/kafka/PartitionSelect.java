package net.itdiandi.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class PartitionSelect {
    public static void main(String[] args) {
    	String topicName = "test";
    	// 设置配置属性
    	Properties props = new Properties();
    	props.put("bootstrap.servers", "10.7.6.25:9092");
    	props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    	props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    	props.put("request.required.acks", "1");
    	// 创建producer
    	Producer<String, String> producer = new KafkaProducer<String, String>(props);

    	for (int i = 0; i < 10; i++) {
    		producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(i), Integer.toString(i)));
    	}

    	System.out.println("Message sent successfully");
    	producer.close();
    }
}
