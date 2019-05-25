package edu.festival.deichkind.util

import android.content.Context
import edu.festival.deichkind.models.Dyke

class DykeManager private constructor(context: Context?) {
    init {
    }

    var dykes: Array<Dyke> = arrayOf()

    companion object : SingletonHolder<DykeManager, Context?>(::DykeManager)
}