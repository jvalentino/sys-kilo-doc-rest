package com.github.jvalentino.kilo.rest

import com.github.jvalentino.kilo.dto.CountDto
import com.github.jvalentino.kilo.dto.Doc
import com.github.jvalentino.kilo.dto.DocDto
import com.github.jvalentino.kilo.dto.DocListDto
import com.github.jvalentino.kilo.dto.DocVersion
import com.github.jvalentino.kilo.dto.ResultDto
import com.github.jvalentino.kilo.dto.ViewVersionDto
import com.github.jvalentino.kilo.entity.DocTable
import com.github.jvalentino.kilo.entity.DocVersionTable
import com.github.jvalentino.kilo.eventing.DocProducer
import com.github.jvalentino.kilo.repo.DocRepo
import com.github.jvalentino.kilo.repo.DocVersionRepo
import com.github.jvalentino.kilo.util.BaseIntg
import com.github.jvalentino.kilo.util.DateGenerator
import com.github.jvalentino.kilo.util.DateUtil
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult

import java.nio.ByteBuffer

import static org.mockito.Mockito.verify;

import java.sql.Timestamp

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DocRestIntgTest extends BaseIntg {

    @MockBean
    DocRepo docRepo

    @MockBean
    DocVersionRepo docVersionRepo

    @MockBean
    DocProducer docProducer

    @Captor
    ArgumentCaptor<DocDto> docProducerCaptor

    def setup() {
        GroovyMock(DateGenerator, global:true)
    }

    def "test doc/all"() {
        given:
        DocTable doc = new DocTable()
        doc.with {
            docId = UUID.randomUUID()
            name = 'bravo.pdf'
            mimeType = 'application/pdf'
            createdByUserId = 'charlie'
            updatedByUserId = 'delta'
            createdDateTime = new Timestamp(DateUtil.toDate('2022-01-02T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2022-01-02T03:00:00.000+0000').time)
        }
        List<DocTable> docs = [doc]
        org.mockito.Mockito.when(docRepo.findAll()).thenReturn(docs)

        when:
        MvcResult response = mvc.perform(
                get("/doc/all").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        DocListDto result = toObject(response, DocListDto)
        result.documents.size() == 1

        and:
        Doc a = result.documents.first()
        a.docId == doc.docId.toString()
        a.name == doc.name
        a.mimeType == doc.mimeType
        a.createdByUserId == doc.createdByUserId
        a.updatedByUserId == doc.updatedByUserId
        a.createdDateTime == doc.createdDateTime
        a.updatedDateTime == doc.updatedDateTime
    }

    void "Test /doc/count"() {
        given:
        org.mockito.Mockito.when(docRepo.count()).thenReturn(1L)

        when:
        MvcResult response = mvc.perform(
                get("/doc/count").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        CountDto result = toObject(response, CountDto)
        result.value == 1L
    }

    void "Test /doc/upload/user/{userId}"() {
        given:
        DocDto file = new DocDto()
        file.with {
            fileName = 'alpha.txt'
            base64 = 'bravo'.bytes.encodeBase64()
            mimeType = 'plain/text'
        }

        when:
        MvcResult response = mvc.perform(
                post("/doc/upload/user/456")
                        .header('X-Auth-Token', '123')
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJson(file)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        1 * DateGenerator.date() >> DateUtil.toDate('2022-01-02T00:00:00.000+0000')

        and:
        verify(docProducer).produce(docProducerCaptor.capture())
        DocDto sent = docProducerCaptor.getValue()
        sent.docId != null
        sent.docVersionId != sent.docId
        sent.mimeType == 'plain/text'
        sent.dateTime == '2022-01-02T00:00:00.000+0000'
        sent.fileName == 'alpha.txt'
        sent.base64 == 'YnJhdm8='
        sent.userId == '456'

        and:
        ResultDto result = toObject(response, ResultDto)
        result.success
    }

    void "test /doc/version/new/{docId}/user/{userId}"() {
        given:
        DocDto file = new DocDto()
        file.with {
            fileName = 'alpha.txt'
            base64 = 'bravo'.bytes.encodeBase64()
            mimeType = 'plain/text'
        }

        and:
        String docUuid = '8ad96754-d281-403f-b767-c01f31ce470a'

        when:
        MvcResult response = mvc.perform(
                post("/doc/version/new/${docUuid}/user/456")
                        .header('X-Auth-Token', '123')
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJson(file)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        1 * DateGenerator.date() >> DateUtil.toDate('2022-01-02T00:00:00.000+0000')

        and:
        verify(docProducer).produce(docProducerCaptor.capture())
        DocDto sent = docProducerCaptor.getValue()
        sent.docId == docUuid
        sent.docVersionId != sent.docId
        sent.mimeType == 'plain/text'
        sent.dateTime == '2022-01-02T00:00:00.000+0000'
        sent.fileName == 'alpha.txt'
        sent.base64 == 'YnJhdm8='
        sent.userId == '456'

        and:
        ResultDto result = toObject(response, ResultDto)
        result.success
    }

    void "Test /doc/versions/{docId}"() {
        given:
        String docUuid = '8ad96754-d281-403f-b767-c01f31ce470a'
        String versionUuid = 'e189a9c5-9f68-42e5-a746-a9fa60dc9504'
        Timestamp timestamp = new Timestamp(DateUtil.toDate('2022-01-02T00:00:00.000+0000').time)

        and:
        DocTable docRecord = new DocTable()
        docRecord.with {
            docId = UUID.fromString(docUuid)
            name = 'sample.txt'
            mimeType = 'plain/text'
            createdByUserId = '123'
            updatedByUserId = '456'
            createdDateTime = timestamp
            updatedDateTime = timestamp
        }
        Optional<DocTable> optionalDocTable = GroovyMock()
        1 * optionalDocTable.get() >> docRecord
        org.mockito.Mockito.when(docRepo.findById(UUID.fromString(docUuid))).thenReturn(optionalDocTable)

        and:
        DocVersionTable version = new DocVersionTable()
        version.with {
            docVersionId = UUID.fromString(versionUuid)
            docId = docRecord.docId
            name = 'sample.txt'
            mimeType = 'plain/text'
            createdByUserId = '123'
            createdDateTime = timestamp
            data = ByteBuffer.wrap('hello'.bytes)
        }
        List<DocVersionTable> versionRecords = [version]
        org.mockito.Mockito.when(docVersionRepo.findByDocId(docRecord.docId)).thenReturn(versionRecords)

        when:
        MvcResult response = mvc.perform(
                get("/doc/versions/${docUuid}").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        ViewVersionDto result = toObject(response, ViewVersionDto)
        result

        and:
        result.doc
        result.doc.docId == docUuid
        result.doc.name == 'sample.txt'
        result.doc.mimeType == 'plain/text'
        result.doc.createdByUserId == '123'
        result.doc.updatedByUserId == '456'
        result.doc.createdDateTime == timestamp
        result.doc.updatedDateTime == timestamp

        and:
        DocVersion docVersion = result.doc.versions.first()
        docVersion
        docVersion.docVersionId == versionUuid
        docVersion.versionNum == '1'
        docVersion.data == null
        docVersion.createdDateTime == timestamp
        docVersion.createdByUserId == '123'

    }

    void "Test /doc/version/download/{docVersionId}"() {
        given:
        String docVersionId = '8ad96754-d281-403f-b767-c01f31ce470a'

        and:
        DocVersionTable version = new DocVersionTable()
        version.with {
            name = 'sample.txt'
            mimeType = 'plain/text'
            data = ByteBuffer.wrap('hello'.bytes)
        }

        and:
        Optional<DocVersionTable> optional = GroovyMock()
        1 * optional.get() >> version
        org.mockito.Mockito.when(docVersionRepo.findById(UUID.fromString(docVersionId))).thenReturn(optional)

        when:
        MvcResult response = mvc.perform(
                get("/doc/version/download/${docVersionId}")
                        .header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        DocDto result = toObject(response, DocDto)
        result.base64 == 'aGVsbG8='
        result.fileName == 'sample.txt'
        result.mimeType == 'plain/text'
    }
}
