package com.github.jvalentino.kilo.rest

import com.github.jvalentino.kilo.dto.CountDto
import com.github.jvalentino.kilo.dto.DocDto
import com.github.jvalentino.kilo.dto.DocListDto
import com.github.jvalentino.kilo.dto.ResultDto
import com.github.jvalentino.kilo.dto.ViewVersionDto
import com.github.jvalentino.kilo.service.DocService
import com.github.jvalentino.kilo.util.DateGenerator
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * The general rest endpoint for accessing all document related things.
 * @author john.valentino
 */
@CompileDynamic
@Slf4j
@RestController
@SuppressWarnings(['UnnecessarySetter', 'UnnecessaryGetter', 'UnusedMethodParameter', 'EmptyMethod'])
class DocRest {

    @Autowired
    DocService docService

    @GetMapping('/doc/all')
    @CircuitBreaker(name = 'DocAll')
    ResponseEntity<DocListDto> dashboard() {
        DocListDto dashboard = new DocListDto()
        dashboard.with {
            documents = docService.allDocs()
        }

        new ResponseEntity<DocListDto>(dashboard, HttpStatus.OK)
    }

    @GetMapping('/doc/populate')
    ResultDto populate() {
        docService.populateTestData()
        new ResultDto()
    }

    @GetMapping('/doc/count')
    @CircuitBreaker(name = 'DocCount')
    CountDto countDocs() {
        CountDto result = new CountDto()
        result.with {
            value = docService.countDocuments()
        }
        result
    }

    @CircuitBreaker(name = 'DocUpload')
    @PostMapping('/doc/upload/user/{userId}')
    ResultDto upload(@RequestBody DocDto file, @PathVariable(value='userId') String userId) {
        docService.uploadNewDoc(userId, file, DateGenerator.date())
        new ResultDto()
    }

    @CircuitBreaker(name = 'DocVersions')
    @GetMapping('/doc/versions/{docId}')
    ViewVersionDto versions(@PathVariable(value='docId') String docId) {
        ViewVersionDto result = new ViewVersionDto()
        result.with {
            doc = docService.retrieveDocVersions(docId)
        }

        log.info("Doc ${docId} has ${result.doc.versions.size()} versions")

        result
    }

    // https://www.baeldung.com/servlet-download-file
    @CircuitBreaker(name = 'DocDownload')
    @GetMapping('/doc/version/download/{docVersionId}')
    DocDto downloadVersion(@PathVariable(value='docVersionId') String docVersionId) {
        //docService.retrieveVersion(docVersionId)
    }

    @CircuitBreaker(name = 'DocVersionNew')
    @PostMapping('/doc/version/new/{docId}/user/{userId}')
    ResultDto upload(@RequestBody DocDto file, @PathVariable(value='docId') String docId,
                     @PathVariable(value='userId') String userId) {
        docService.uploadNewDoc(userId, file, DateGenerator.date(), docId)
        new ResultDto()
    }

}
