package com.github.jvalentino.kilo

import com.github.jvalentino.kilo.KiloDocRestApp
import org.springframework.boot.SpringApplication
import spock.lang.Specification

class KiloDocRestAppTest extends Specification {

    def setup() {
        GroovyMock(SpringApplication, global:true)
    }

    def "test main"() {
        when:
        KiloDocRestApp.main(null)

        then:
        1 * SpringApplication.run(KiloDocRestApp, null)
    }

}
