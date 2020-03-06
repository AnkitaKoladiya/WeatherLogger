package com.weather.logger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weather.logger.R
import com.weather.logger.database.entity.WeatherInfoEntity
import com.weather.logger.utils.OnDeleteWeatherClickListener
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class WeatherListAdapter internal constructor(
    var context: Context,
    var onDeleteWeatherClickListener: OnDeleteWeatherClickListener
) :
    RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var weatherList = emptyList<WeatherInfoEntity>()

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemView = inflater.inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherInfoEntity = weatherList[position]

        val temperature: Double = weatherInfoEntity.temp!!.toDouble() - 273.15
        holder.tvTemp.text = String.format(
            context.getString(R.string.temperature),
            DecimalFormat("###").format(temperature)
        )

        val date = Date(weatherInfoEntity.dt!! * 1000)
        val df = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        df.timeZone = TimeZone.getDefault()
        val formattedDate: String = df.format(date)
        holder.tvDate.text = formattedDate

        holder.ivDelete.setOnClickListener {
            onDeleteWeatherClickListener.onDeleteWeatherDataClick(weatherInfoEntity.id)
        }
    }

    internal fun updateWeatherList(userList: List<WeatherInfoEntity>) {
        this.weatherList = userList
        notifyDataSetChanged()
    }

    override fun getItemCount() = weatherList.size
}