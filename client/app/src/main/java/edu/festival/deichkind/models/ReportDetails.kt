package edu.festival.deichkind.models

import android.os.Parcel
import android.os.Parcelable

class ReportDetails : Parcelable {

    var type: String = ""
    var waterLoss: String = ""
    var waterCondition: String = ""
    var leakageType: String = ""
    var deformationType: String = ""

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(type)
        dest.writeString(waterLoss)
        dest.writeString(waterCondition)
        dest.writeString(leakageType)
        dest.writeString(deformationType)
    }

    companion object CREATOR : Parcelable.Creator<ReportDetails> {
        override fun createFromParcel(source: Parcel): ReportDetails = ReportDetails().apply {
            type = source.readString() as String
            waterLoss = source.readString() as String
            waterCondition = source.readString() as String
            leakageType = source.readString() as String
            deformationType = source.readString() as String
        }

        override fun newArray(size: Int): Array<ReportDetails?> = arrayOfNulls(size)
    }
}