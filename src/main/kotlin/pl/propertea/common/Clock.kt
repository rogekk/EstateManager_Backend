package pl.propertea.common

import org.joda.time.DateTime

interface Clock {
    fun getDateTime(): DateTime
}

class SystemClock : Clock {
    override fun getDateTime(): DateTime = DateTime.now()
}
