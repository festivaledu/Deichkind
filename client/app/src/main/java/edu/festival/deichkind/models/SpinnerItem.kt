package edu.festival.deichkind.models

class SpinnerItem(private var key: String, private var display: String) {

    fun getKey(): String {
        return key
    }

    override fun toString(): String {
        return display
    }

}