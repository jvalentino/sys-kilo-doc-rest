package com.github.jvalentino.kilo.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.github.jvalentino.kilo.util.DateUtil
import groovy.transform.CompileDynamic

import java.sql.Timestamp

/**
 * A task associated with a document
 * @author john.valentino
 */
@CompileDynamic
class DocTask {

    Long docTaskId

    Doc doc

    String name

    String status

    String content

    Object createdByUser

    Long createdByUserId

    Object updatedByUser

    Long updatedByUserId

    @JsonFormat(pattern= DateUtil.ISO)
    Timestamp createdDateTime

    @JsonFormat(pattern=DateUtil.ISO)
    Timestamp updatedDateTime

}
