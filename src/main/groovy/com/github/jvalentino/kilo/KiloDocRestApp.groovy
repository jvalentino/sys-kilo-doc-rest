package com.github.jvalentino.kilo

import groovy.transform.CompileDynamic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main application
 */
@SpringBootApplication
@CompileDynamic
class KiloDocRestApp {

    static void main(String[] args) {
        SpringApplication.run(KiloDocRestApp, args)
    }

}
