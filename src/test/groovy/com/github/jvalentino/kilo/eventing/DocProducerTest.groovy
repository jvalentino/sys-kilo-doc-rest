package com.github.jvalentino.kilo.eventing

import com.github.jvalentino.kilo.dto.DocDto
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification
import spock.lang.Subject

class DocProducerTest extends Specification {

    @Subject
    DocProducer subject

    def setup() {
        subject = new DocProducer()
        subject.kafkaTemplate = GroovyMock(KafkaTemplate)
    }

    void "Test produce"() {
        given:
        DocDto doc = new DocDto()
        doc.with {
            docId = 'alpha'
        }

        when:
        subject.produce(doc)

        then:
        1 * subject.kafkaTemplate.send(_, _, _) >> { String t, String k, String j ->
            assert t == 'doc'
            assert k == 'alpha'
            assert j.contains('alpha')
        }
    }

}
