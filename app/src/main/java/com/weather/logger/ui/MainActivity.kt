package com.weather.logger.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlinpermissions.KotlinPermissions
import com.weather.logger.R
import com.weather.logger.adapter.WeatherListAdapter
import com.weather.logger.database.AppDatabase
import com.weather.logger.database.entity.WeatherInfoEntity
import com.weather.logger.model.WeatherData
import com.weather.logger.retrofit.ApiClient
import com.weather.logger.retrofit.ApiInterface
import com.weather.logger.utils.GPSTracker
import com.weather.logger.utils.OnDeleteWeatherClickListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnDeleteWeatherClickListener {

    lateinit var weatherData: WeatherData
    lateinit var appDatabase: AppDatabase
    private lateinit var adapter: WeatherListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appDatabase = AppDatabase.getInstance(this)
        val gpsTracker = GPSTracker(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvList.layoutManager = linearLayoutManager
        adapter = WeatherListAdapter(this@MainActivity, this)
        rvList.adapter = adapter
        adapter.updateWeatherList(appDatabase.weatherDataDao()!!.fetchWeatherInfo())

        KotlinPermissions.with(this@MainActivity)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .onAccepted {
                if (gpsTracker.getIsGPSTrackingEnabled()) {
                    getWeatherData(gpsTracker.getLongitude(), gpsTracker.getLatitude())
                } else {
                    gpsTracker.showSettingsAlert();
                }
            }
            .onDenied {
            }
            .onForeverDenied {
            }
            .ask()

        tvMoreDetails.setOnClickListener {
            if (llMoreDetail.visibility == GONE) {
                tvMoreDetails.text = getString(R.string.lessDetails)
                btnSave.visibility = GONE
                llMoreDetail.visibility = VISIBLE
            } else {
                tvMoreDetails.text = getString(R.string.more_details)
                btnSave.visibility = VISIBLE
                llMoreDetail.visibility = GONE
            }
        }

        btnSave.setOnClickListener {
            addDateToLocalDatabase()
        }

        ivRefresh.setOnClickListener {
            ivRefresh.visibility = GONE
            progressBar.visibility = VISIBLE
            getWeatherData(gpsTracker.getLongitude(), gpsTracker.getLatitude())
        }
    }

    private fun getWeatherData(longitude: Double, latitude: Double) {
        val apiInterface = ApiClient.client!!.create(ApiInterface::class.java)
        val callApi = apiInterface.getWeatherData(
            latitude.toString(),
            longitude.toString(),
            getString(R.string.API_KEY)
        )
        callApi.enqueue(object : Callback<WeatherData> {

            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.body() != null) {
                    weatherData = response.body()!!
                    setupUI(weatherData)
                    ivRefresh.visibility = VISIBLE
                    progressBar.visibility = GONE
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.e("onFailure", t.localizedMessage)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun setupUI(weatherData: WeatherData) {
        val date = Date(weatherData.dt!! * 1000)
        val df = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        df.timeZone = TimeZone.getDefault()
        val formattedDate: String = df.format(date)
        tvDate.text = formattedDate

        val temperature: Double = weatherData.main!!.temp!!.toDouble() - 273.15
        tvTemp.text = DecimalFormat("###").format(temperature) + "\u2103"
        tvCity.text = weatherData.name
        tvMaxTemp.text =
            DecimalFormat("###").format(weatherData.main!!.temp_max!!.toDouble() - 273.15) + "\u2103"
        tvMinTemp.text =
            DecimalFormat("###").format(weatherData.main!!.temp_min!!.toDouble() - 273.15) + "\u2103"
        tvHumidity.text = String.format(getString(R.string.humidity), weatherData.main!!.humidity)
    }

    private fun addDateToLocalDatabase() {
        val weatherInfoEntity: WeatherInfoEntity = WeatherInfoEntity()
        weatherInfoEntity.id = Date().time
        weatherInfoEntity.dt = weatherData.dt
        weatherInfoEntity.name = weatherData.name
        weatherInfoEntity.cod = weatherData.cod
        weatherInfoEntity.base = weatherData.base
        weatherInfoEntity.temp = weatherData.main!!.temp
        weatherInfoEntity.temp_min = weatherData.main!!.temp_min
        weatherInfoEntity.humidity = weatherData.main!!.humidity
        weatherInfoEntity.pressure = weatherData.main!!.pressure
        weatherInfoEntity.feels_like = weatherData.main!!.feels_like
        weatherInfoEntity.temp_max = weatherData.main!!.temp_max

        appDatabase.weatherDataDao()!!.insertDayInfo(weatherInfoEntity)
        Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
        adapter.updateWeatherList(appDatabase.weatherDataDao()!!.fetchWeatherInfo())
    }

    override fun onDeleteWeatherDataClick(id: Long?) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Delete Weather Data?")
            .setMessage("Are you sure you want to delete this data?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                appDatabase.weatherDataDao().deleteData(id!!)
                adapter.updateWeatherList(appDatabase.weatherDataDao()!!.fetchWeatherInfo())
            }
            .setNegativeButton(android.R.string.no, null)
            .show()

    }
}

