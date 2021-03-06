package se.inera.intyg.srs.persistence.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class PredictionPriority(val priority: Int,
                         @ManyToOne
                         val question: PredictionQuestion,
                         @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                         val id: Long = -1)
