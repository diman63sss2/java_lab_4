package com.example.dezdemoniyslab.jms;

import com.example.dezdemoniyslab.models.Log;
import com.example.dezdemoniyslab.models.LogType;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;




@Component
@RequiredArgsConstructor
public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.key.log}")
    private String routingKeyLog;

    public void sendMessage(Object entity, LogType logType, String  description){
        Log logTable = Log.builder()
                .logType(logType)
                .tableName(entity.getClass().getAnnotation(Table.class).name())
                .description(description)
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKeyLog, logTable);
        LOGGER.info(String.format("message sent :: %s", logTable.getLogType()));
    }
}