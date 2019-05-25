package edu.festival.deichkind.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Comment : Parcelable {

    var id: String = ""
    var dykeId: String = ""
    var reportId: String = ""
    var message: String = ""
    var accountId: String = ""
    var deleted: Boolean = false
    var createdAt: Date = Date()
    var updatedAt: Date = Date()

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(dykeId)
        dest.writeString(reportId)
        dest.writeString(message)
        dest.writeString(accountId)
        dest.writeInt(if (deleted) 1 else 0)
        dest.writeLong(createdAt.time)
        dest.writeLong(updatedAt.time)
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(source: Parcel): Comment = Comment().apply {
            id = source.readString() as String
            dykeId = source.readString() as String
            reportId = source.readString() as String
            message = source.readString() as String
            accountId = source.readString() as String
            deleted = source.readInt() == 1
            createdAt = Date(source.readLong())
            updatedAt = Date(source.readLong())
        }

        override fun newArray(size: Int): Array<Comment?> = arrayOfNulls(size)
    }

}