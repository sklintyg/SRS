package se.inera.intyg.srs.persistence.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Measure(val diagnosisId: String,
              val diagnosisText: String,
              val version: String,
              @OneToMany(fetch = FetchType.EAGER, mappedBy = "measure")
              val priorities: Collection<MeasurePriority> = emptyList(),
              @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
              val id: Long = -1
            ) {

    override fun toString() =
            "Measure(id=$id, diagnosisId='$diagnosisId', diagnosisText='$diagnosisText', version='$version', priorities='$priorities')"

}
