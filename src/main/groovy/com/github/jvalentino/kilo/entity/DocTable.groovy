package com.github.jvalentino.kilo.entity

import groovy.transform.CompileDynamic
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

import java.sql.Timestamp

/**
 * The doc table in cassandra
 * @author john.valentino
 */
@CompileDynamic
@Table(value='doc')
class DocTable {

    @PrimaryKeyColumn(name = 'doc_id', type = PrimaryKeyType.PARTITIONED)
    UUID docId

    @Column
    String name

    @Column(value = 'mime_type')
    String mimeType

    @Column(value = 'created_by_user_id')
    String createdByUserId

    @Column(value = 'updated_by_user_id')
    String updatedByUserId

    @Column(value = 'created_date_time')
    Timestamp createdDateTime

    @Column(value = 'updated_date_time')
    Timestamp updatedDateTime

}
