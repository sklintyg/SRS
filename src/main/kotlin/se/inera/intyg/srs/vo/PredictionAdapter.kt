package se.inera.intyg.srs.vo

import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus

interface PredictionAdapter {

    fun getPrediction(person: Person, diagnosis: Diagnosis): Prediction
}

class Prediction(val diagnosis: String, val prediction: Double?, val status: Diagnosprediktionstatus)