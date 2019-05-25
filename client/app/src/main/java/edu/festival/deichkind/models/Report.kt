package edu.festival.deichkind.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Report : Parcelable {

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

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(id)
            writeString(dykeId)
            writeString(title)
            writeDouble(latitude)
            writeDouble(longitude)
            writeString(position)
            writeTypedObject(details, flags)
            writeString(accountId)
            writeInt(if (resolved) 1 else 0)
            writeInt(if (deleted) 1 else 0)
            writeLong(createdAt.time)
            writeLong(updatedAt.time)
            writeTypedArray(comments, flags)
            writeTypedArray(photos, flags)
        }
    }

    companion object CREATOR : Parcelable.Creator<Report> {
        override fun createFromParcel(source: Parcel): Report = Report().apply {
            id = source.readString() as String
            dykeId = source.readString() as String
            title = source.readString() as String
            latitude = source.readDouble()
            longitude = source.readDouble()
            position = source.readString() as String
            details = source.readTypedObject(ReportDetails.CREATOR) as ReportDetails
            accountId = source.readString() as String
            resolved = source.readInt() == 1
            deleted = source.readInt() == 1
            createdAt = Date(source.readLong())
            updatedAt = Date(source.readLong())
            comments = source.createTypedArray(Comment.CREATOR) as Array<Comment>
            photos = source.createTypedArray(Photo.CREATOR) as Array<Photo>
        }

        override fun newArray(size: Int): Array<Report?> = arrayOfNulls(size)
    }
}