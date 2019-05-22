package edu.festival.deichkind.models

import java.util.*

class Comment {

    var id: String = ""
    var dykeId: String = ""
    var reportId: String = ""
    var message: String = ""
    var accountId: String = ""
    var deleted: Boolean = false
    var createdAt: Date = Date()
    var updatedAt: Date = Date()

}