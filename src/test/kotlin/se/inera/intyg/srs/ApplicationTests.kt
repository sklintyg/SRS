package se.inera.intyg.srs

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
class ApplicationTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun dummyTest() {
    }
}
