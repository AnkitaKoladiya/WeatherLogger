package com.weather.logger.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
class WeatherInfoEntity {
    @PrimaryKey
    var id: Long = 0
    var dt: Long? = 0L
    var name: String? = null
    var cod: String? = null
    var base: String? = null

    var temp: String? = null
    var temp_min: String? = null
    var humidity: String? = null
    var pressure: String? = null
    var feels_like: String? = null
    var temp_max: String? = null

}