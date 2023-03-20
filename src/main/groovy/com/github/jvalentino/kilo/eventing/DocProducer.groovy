package com.github.jvalentino.kilo.eventing

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jvalentino.kilo.dto.DocDto
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Used for publishing events to the doc topics
 */
@CompileDynamic
@Component
class DocProducer {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate

    void produce(DocDto doc) {
        kafkaTemplate.send('doc', doc.docId, toJson(doc))
    }

    String toJson(Object obj) {
        new ObjectMapper().writeValueAsString(obj)
    }

}
