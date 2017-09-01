package se.inera.intyg.srs.vo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import se.inera.intyg.srs.persistence.*
import java.util.concurrent.atomic.AtomicLong

@Service
@Profile("it")
class TestModule(@Autowired private val consentRepo: ConsentRepository,
                 @Autowired private val measureRepo: MeasureRepository,
                 @Autowired private val priorityRepo: MeasurePriorityRepository,
                 @Autowired private val recommendationRepo: RecommendationRepository
                 ) {

    private val currentMeasureId = AtomicLong()

    fun createMeasure(diagnosisId: String, diagnosisText: String, recommendations: List<String>): Measure =
            measureRepo.save(mapToMeasure(diagnosisId, diagnosisText, recommendations))

    private fun mapToMeasure(diagnosisId: String, diagnosisText: String, recommendations: List<String>) =
            Measure(currentMeasureId.incrementAndGet(), diagnosisId, diagnosisText, "1.0", mapToMeasurePriorities(recommendations))

    private fun mapToMeasurePriorities(recommendations: List<String>) =
            recommendations
                    .mapIndexed({ i, recText -> Recommendation(i.toLong(), recText) })
                    .map({ rec -> recommendationRepo.save(rec) })
                    .mapIndexed({ i, rec -> MeasurePriority(i + 1, rec) })
                    .map({ priority -> priorityRepo.save(priority) })
                    .toMutableList()

    fun deleteMeasure(diagnosisId: String) = measureRepo.delete(measureRepo.findByDiagnosisIdStartingWith(diagnosisId))

    fun deleteAllConsents() = consentRepo.deleteAll()

    fun deleteAllMeasures() = measureRepo.deleteAll()

    fun deleteAllRecommendations() = recommendationRepo.deleteAll()

    fun deleteAllPriorities() = priorityRepo.deleteAll()

}