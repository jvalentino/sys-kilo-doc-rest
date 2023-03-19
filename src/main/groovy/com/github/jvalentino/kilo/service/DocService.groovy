package com.github.jvalentino.kilo.service

import com.github.jvalentino.kilo.dto.DocDto
import com.github.jvalentino.kilo.repo.DocProducer
import com.github.jvalentino.kilo.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * General service for interacting with documents
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
class DocService {

    @Autowired
    DocProducer docProducer

    void uploadNewDoc(String userId, DocDto file, Date date) {
        file.docId = UUID.randomUUID().toString()
        file.userId = userId
        file.dateTime = DateUtil.fromDate(date)

        docProducer.produce(file)
    }

}
