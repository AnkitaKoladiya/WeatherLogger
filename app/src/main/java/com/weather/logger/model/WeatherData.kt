package com.weather.logger.model

class WeatherData {
    var visibility: String? = null
    var timezone: String? = null
    var main: Main? = null
    var clouds: Clouds? = null
    var sys: Sys? = null

    var dt: Long? =0L
    var coord: Coord? = null
    var weather: Array<Weather>?= emptyArray()
    var name: String? = null
    var cod: String? = null
    var id: String? = null
    var base: String? = null
    var wind: Wind? = null

}