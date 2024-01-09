package com.example.dezdemoniyslab.jms;

import com.example.dezdemoniyslab.models.Log;
import com.example.dezdemoniyslab.reposiotries.LogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerLog {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerLog.class);
    private final LogRepository logRepository;

    @RabbitListener(queues = {"${rabbitmq.queue.log}"})
    public void consume(Log log){
        LOGGER.info(String.format("message received :: %s", log.getLogType()));
        logRepository.save(log);
    }
}