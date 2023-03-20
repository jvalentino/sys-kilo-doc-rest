package com.github.jvalentino.kilo.service

import com.github.jvalentino.kilo.dto.Doc
import com.github.jvalentino.kilo.dto.DocDto
import com.github.jvalentino.kilo.entity.DocTable
import com.github.jvalentino.kilo.eventing.DocProducer
import com.github.jvalentino.kilo.repo.DocRepo
import com.github.jvalentino.kilo.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.sql.Timestamp

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

    @Autowired
    DocRepo docRepo

    List<Doc> allDocs() {
        /*DocTable doc = new DocTable()
        doc.with {
            docId = UUID.randomUUID()
            name = 'sample.pdf'
            mimeType = 'application.pdf'
            createdByUserId = '123'
            updatedByUserId = '123'
            createdDateTime = new Timestamp(new Date().time)
            updatedDateTime = new Timestamp(new Date().time)
        }
        docRepo.save(doc)*/

        List<DocTable> docs = docRepo.findAll()
        List<Doc> results = []

        for (DocTable input : docs) {
            Doc output = new Doc()
            output.with {
                docId = input.docId
                name = input.name
                mimeType = input.mimeType
                createdByUserId = input.createdByUserId
                updatedByUserId = input.updatedByUserId
                createdDateTime = input.createdDateTime
                updatedDateTime = input.updatedDateTime
            }

            results.add(output)
        }

        results
    }

    void uploadNewDoc(String userId, DocDto file, Date date, String docId=UUID.randomUUID().toString()) {
        file.docId = docId
        file.userId = userId
        file.dateTime = DateUtil.fromDate(date)
        file.docVersionId = UUID.randomUUID().toString()

        docProducer.produce(file)
    }

}
