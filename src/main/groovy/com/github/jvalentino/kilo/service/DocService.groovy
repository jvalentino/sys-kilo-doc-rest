package com.github.jvalentino.kilo.service

import com.github.jvalentino.kilo.dto.Doc
import com.github.jvalentino.kilo.dto.DocDto
import com.github.jvalentino.kilo.dto.DocVersion
import com.github.jvalentino.kilo.entity.DocTable
import com.github.jvalentino.kilo.entity.DocVersionTable
import com.github.jvalentino.kilo.eventing.DocProducer
import com.github.jvalentino.kilo.repo.DocRepo
import com.github.jvalentino.kilo.repo.DocVersionRepo
import com.github.jvalentino.kilo.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.nio.ByteBuffer
import java.sql.Timestamp

/**
 * General service for interacting with documents
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
@SuppressWarnings(['DuplicateStringLiteral', 'JavaIoPackageAccess'])
class DocService {

    @Autowired
    DocProducer docProducer

    @Autowired
    DocRepo docRepo

    @Autowired
    DocVersionRepo docVersionRepo

    void populateTestData() {
        DocTable doc = new DocTable()
        doc.with {
            docId = UUID.randomUUID()
            name = 'sample.pdf'
            mimeType = 'application/pdf'
            createdByUserId = '123'
            updatedByUserId = '456'
            createdDateTime = new Timestamp(new Date().time)
            updatedDateTime = new Timestamp(new Date().time)
        }
        docRepo.save(doc)

        DocVersionTable version = new DocVersionTable()
        version.with {
            docVersionId = UUID.randomUUID()
            docId = doc.docId
            name = 'sample.pdf'
            mimeType = 'application/pdf'
            createdByUserId = '123'
            createdDateTime = new Timestamp(new Date().time)
            data = ByteBuffer.wrap(new File('sample.pdf').bytes)
        }

        docVersionRepo.save(version)

        DocVersionTable version2 = new DocVersionTable()
        version2.with {
            docVersionId = UUID.randomUUID()
            docId = doc.docId
            name = 'sample.pdf'
            mimeType = 'application/pdf'
            createdByUserId = '123'
            createdDateTime = new Timestamp(new Date().time)
            data = ByteBuffer.wrap(new File('sample.pdf').bytes)
        }

        docVersionRepo.save(version2)
    }

    Long countDocuments() {
        docRepo.count()
    }

    List<Doc> allDocs() {
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

    Doc retrieveDocVersions(String docIdString) {
        Doc result = new Doc()

        DocTable docRecord = docRepo.findById(UUID.fromString(docIdString)).get()

        result.with {
            docId = docRecord.docId.toString()
            name = docRecord.name
            mimeType = docRecord.mimeType
            createdByUserId = docRecord.createdByUserId
            updatedByUserId = docRecord.updatedByUserId
            createdDateTime = docRecord.createdDateTime
            updatedDateTime = docRecord.updatedDateTime
            versions = []
        }

        List<DocVersionTable> versionRecords = docVersionRepo.findByDocId(docRecord.docId)
        int counter = 1
        for (DocVersionTable versionRecord : versionRecords) {
            DocVersion version = new DocVersion()
            version.with {
                docVersionId = versionRecord.docVersionId.toString()
                versionNum = "${counter}"
                createdDateTime = versionRecord.createdDateTime
                createdByUserId = versionRecord.createdByUserId
            }
            result.versions.add(version)

            counter++
        }

        result
    }

}
