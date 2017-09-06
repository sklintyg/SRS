package se.inera.intyg.srs.vo

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import se.inera.intyg.srs.controllers.TestController
import se.inera.intyg.srs.persistence.ConsentRepository
import se.inera.intyg.srs.persistence.DiagnosisRepository
import se.inera.intyg.srs.persistence.InternalStatistic
import se.inera.intyg.srs.persistence.Measure
import se.inera.intyg.srs.persistence.MeasurePriority
import se.inera.intyg.srs.persistence.MeasurePriorityRepository
import se.inera.intyg.srs.persistence.MeasureRepository
import se.inera.intyg.srs.persistence.PredictionDiagnosis
import se.inera.intyg.srs.persistence.PredictionPriority
import se.inera.intyg.srs.persistence.PredictionPriorityRepository
import se.inera.intyg.srs.persistence.PredictionQuestion
import se.inera.intyg.srs.persistence.PredictionResponse
import se.inera.intyg.srs.persistence.QuestionRepository
import se.inera.intyg.srs.persistence.Recommendation
import se.inera.intyg.srs.persistence.RecommendationRepository
import se.inera.intyg.srs.persistence.ResponseRepository
import se.inera.intyg.srs.persistence.StatisticRepository
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

@Service
@Profile("it")
class TestModule(private val consentRepo: ConsentRepository,
                 private val measureRepo: MeasureRepository,
                 private val priorityRepo: MeasurePriorityRepository,
                 private val recommendationRepo: RecommendationRepository,
                 private val statisticsRepo: StatisticRepository,
                 private val diagnosisRepo: DiagnosisRepository,
                 private val predictionPriorityRepo: PredictionPriorityRepository,
                 private val questionRepo: QuestionRepository,
                 private val responseRepo: ResponseRepository) {

    private val uniqueId = AtomicLong()

    fun createMeasure(diagnosisId: String, diagnosisText: String, recommendations: List<String>): Measure =
            measureRepo.save(mapToMeasure(diagnosisId, diagnosisText, recommendations))

    private fun mapToMeasure(diagnosisId: String, diagnosisText: String, recommendations: List<String>) =
            Measure(uniqueId.incrementAndGet(), diagnosisId, diagnosisText, "1.0", mapToMeasurePriorities(recommendations))

    private fun mapToMeasurePriorities(recommendations: List<String>) =
            recommendations
                    .mapIndexed { i, recText -> Recommendation(i.toLong(), recText) }
                    .map { rec -> recommendationRepo.save(rec) }
                    .mapIndexed { i, rec -> MeasurePriority(i + 1, rec) }
                    .map { priority -> priorityRepo.save(priority) }
                    .toMutableList()

    fun createStatistic(diagnosisId: String, pictureUrl: String): InternalStatistic =
            statisticsRepo.save(InternalStatistic(diagnosisId, pictureUrl, LocalDateTime.now(), uniqueId.incrementAndGet()))

    fun createPredictionQuestion(request: TestController.PredictionQuestionRequest): PredictionDiagnosis =
        diagnosisRepo.save(PredictionDiagnosis(uniqueId.incrementAndGet(),
                request.diagnosisId, request.prevalence, mapToPredictions(request.questions)))


    private fun mapToPredictions(questions: List<TestController.PredictionQuestion>) =
        questions
                .mapIndexed { i, (question, helpText, predictionId, responses) -> PredictionPriority(i + 1,
                    questionRepo.save(PredictionQuestion(uniqueId.incrementAndGet(), question, helpText, predictionId, mapToResponses(responses)))) }
                .map { predictionPriorityRepo.save(it) }

    private fun mapToResponses(responses: Collection<TestController.PredictionResponse>) =
        responses
                .mapIndexed { i, (answer, predictionId, default) -> PredictionResponse(uniqueId.incrementAndGet(), answer, predictionId, default, i + 1) }
                .map { responseRepo.save(it) }

    fun deleteMeasure(diagnosisId: String) = measureRepo.delete(measureRepo.findByDiagnosisIdStartingWith(diagnosisId))

    fun deleteAllConsents() = consentRepo.deleteAll()

    fun deleteAllMeasures() = measureRepo.deleteAll()

    fun deleteAllRecommendations() = recommendationRepo.deleteAll()

    fun deleteAllPriorities() = priorityRepo.deleteAll()

    fun deleteAllStatistics() = statisticsRepo.deleteAll()

}