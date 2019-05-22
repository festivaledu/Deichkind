package edu.festival.deichkind

import android.content.Context
import edu.festival.deichkind.models.Dyke
import edu.festival.deichkind.util.SingletonHolder

class DykeManager private constructor(context: Context?) {
    init {
    }

    var dykes: Array<Dyke> = arrayOf()

    companion object : SingletonHolder<DykeManager, Context?>(::DykeManager)
}