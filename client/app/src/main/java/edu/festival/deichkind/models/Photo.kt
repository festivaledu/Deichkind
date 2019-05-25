package edu.festival.deichkind.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Photo : Parcelable {

    var id: String = ""
    var photoMime: String = ""
    var createdAt: Date = Date()
    var updatedAt: Date = Date()

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(photoMime)
        dest.writeLong(createdAt.time)
        dest.writeLong(updatedAt.time)
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(source: Parcel): Photo = Photo().apply {
            id = source.readString() as String
            photoMime = source.readString() as String
            createdAt = Date(source.readLong())
            updatedAt = Date(source.readLong())
        }

        override fun newArray(size: Int): Array<Photo?> = arrayOfNulls(size)
    }
}