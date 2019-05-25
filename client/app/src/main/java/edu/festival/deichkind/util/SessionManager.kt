package edu.festival.deichkind.util

import android.content.Context
import edu.festival.deichkind.models.Session

class SessionManager private constructor(context: Context?) {
    init {
    }

    var session: Session? = null

    companion object : SingletonHolder<SessionManager, Context?>(::SessionManager)
}