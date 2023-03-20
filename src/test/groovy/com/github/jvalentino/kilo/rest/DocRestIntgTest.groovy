package com.github.jvalentino.kilo.rest

import com.github.jvalentino.kilo.repo.DocRepo
import com.github.jvalentino.kilo.util.BaseIntg
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.cassandra.SessionFactory
import org.springframework.data.cassandra.core.convert.CassandraConverter
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DocRestIntgTest extends BaseIntg {

    /*@MockBean
    CassandraDataAutoConfiguration cassandraDataAutoConfiguration

    @MockBean
    CassandraCustomConversions cassandraCustomConversions

    @MockBean
    CassandraMappingContext cassandraMappingContext

    @MockBean
    CassandraConverter cassandraConverter

    @MockBean
    SessionFactory sessionFactory*/

    @MockBean
    DocRepo docRepo

    def "test doc/all"() {
        when:
        MvcResult response = mvc.perform(
                get("/doc/all").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        true
    }
}
