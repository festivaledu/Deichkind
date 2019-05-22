package edu.festival.deichkind.models

import java.util.*

class Report {

    var id: String = ""
    var dykeId: String = ""
    var title: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var position: String = ""
    var details: ReportDetails = ReportDetails()
    var accountId: String = ""
    var resolved: Boolean = false
    var deleted: Boolean = false
    var createdAt: Date = Date()
    var updatedAt: Date = Date()
    var comments: Array<Comment> = arrayOf()
    var photos: Array<Photo> = arrayOf()

    class ReportDetails {

        var type: String = ""
        var waterLoss: String = ""
        var waterCondition: String = ""
        var leakageType: String = ""
        var deformationType: String = ""

    }

}