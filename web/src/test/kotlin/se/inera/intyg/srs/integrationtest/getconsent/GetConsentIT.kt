package se.inera.intyg.srs.integrationtest.getconsent

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import se.inera.intyg.srs.integrationtest.BaseIntegrationTest
import se.inera.intyg.srs.integrationtest.util.whenever

class GetConsentIT : BaseIntegrationTest() {

    private val SOAP_ROOT = "Envelope.Body.GetConsentResponse"

    @Test
    fun testPositiveConsent() {
        addConsent("191212121212", true, "abc")

        given()
            .contentType(ContentType.XML)
            .body(getClasspathResourceAsString("getconsent/getConsentRequest.xml"))
        .whenever()
            .post("/services/get-consent")
        .then()
            .statusCode(200)
            .assertThat()
                .body("$SOAP_ROOT.samtycke", equalTo("true"))
    }

    @Test
    fun testMissingConsent() {

        given()
            .contentType(ContentType.XML)
            .body(getClasspathResourceAsString("getconsent/getConsentRequest.xml"))
        .whenever()
            .post("/services/get-consent")
        .then()
            .statusCode(200)
            .assertThat()
                .body("$SOAP_ROOT.samtyckesstatus", equalTo("INGET"))
    }
}