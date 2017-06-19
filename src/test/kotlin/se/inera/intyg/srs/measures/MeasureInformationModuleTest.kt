package se.inera.intyg.srs.measures

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import se.inera.intyg.srs.persistence.Measure
import se.inera.intyg.srs.persistence.MeasureRepository
import se.inera.intyg.srs.persistence.Priority
import se.inera.intyg.srs.persistence.Recommendation
import se.inera.intyg.srs.vo.Diagnosis
import se.inera.intyg.srs.vo.Extent
import se.inera.intyg.srs.vo.MeasureInformationModule
import se.inera.intyg.srs.vo.Person
import se.inera.intyg.srs.vo.Sex

class MeasureInformationModuleTest {

    private val DIAGNOSIS_A1234 = "A1234"
    private val DIAGNOSIS_A12 = "A12"
    private val DIAGNOSIS_B12 = "B12"

    lateinit var measureRepo: MeasureRepository

    lateinit var module: MeasureInformationModule

    @Before
    fun setUp() {
        measureRepo = mock()
        module = MeasureInformationModule(measureRepo)
        insertMeasureData()
    }

    fun insertMeasureData() {
        whenever(measureRepo.findByDiagnosisIdStartingWith("A12")).thenReturn(listOf(Measure(DIAGNOSIS_A12, "Depression", "1.0",
                listOf((Priority(1, Recommendation("Softa")))))))
        whenever(measureRepo.findByDiagnosisIdStartingWith("B12")).thenReturn(listOf(Measure(DIAGNOSIS_B12, "Benbrott", "1.0",
                listOf((Priority(1, Recommendation("Hoppa på ett ben")))))))
    }

    @Test
    fun diagnosisCodeIsShortenedUntilItMatches() {
        val person: Person = Person("1212121212", 35, Sex.MAN, Extent.HELT_NEDSATT, listOf(Diagnosis(DIAGNOSIS_A1234)))
        val result = module.getInfo(listOf(person))
        assertEquals(DIAGNOSIS_A12, result.get(person)!!.rekommendation.get(0).diagnos.code)
    }

    @Test
    fun measureShouldBeReturnedForEachMatchingDiagnosis() {

    }
}