package com.intuit.orderManagementSystem.order.management.system.utils.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.orderManagementSystem.order.management.system.exception.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducer {

    @Autowired
    private KafkaTemplate <String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String topic, Object obj){
        try{
            log.info("Serializing order object");
            String objToString = objectMapper.writeValueAsString(obj);

            kafkaTemplate.send(topic, objToString);
            log.info("Kafka Message pushed successfully");

        }catch(Exception e){
            log.info("Something went wrong while pushing order to Kafka {}", e.getMessage(), e);
            throw new UnexpectedException(e.getMessage());
        }
    }
}
