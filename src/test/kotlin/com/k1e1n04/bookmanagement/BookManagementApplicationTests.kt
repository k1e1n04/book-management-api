package com.k1e1n04.bookmanagement

import com.k1e1n04.bookmanagement.config.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class BookManagementApplicationTests {
    @Test
    fun contextLoads() {
    }
}
