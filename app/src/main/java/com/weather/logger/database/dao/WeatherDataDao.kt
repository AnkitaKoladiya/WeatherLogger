package com.weather.logger.database.dao

import androidx.room.*
import com.weather.logger.database.entity.WeatherInfoEntity

@Dao
interface WeatherDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDayInfo(weatherInfoEntity: WeatherInfoEntity)

    @Update
    fun updateEntry(weatherInfoEntity: WeatherInfoEntity)

    @Query("SELECT * FROM weather_data")
    fun fetchWeatherInfo(): List<WeatherInfoEntity>

    @Query("DELETE FROM weather_data WHERE id=:id")
    fun deleteData(id: Long)
}
