package com.ismailhakkiaydin.weather.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ismailhakkiaydin.weather.api.WeatherAPIClient
import com.ismailhakkiaydin.weather.model.WeatherResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val apiClient = WeatherAPIClient()
    private val disposable = CompositeDisposable()

    val locationData = MutableLiveData<WeatherResponse>()
    val locationLoading = MutableLiveData<Boolean>()

    fun getWeatherDataWithGPS(latitude: String, longitude: String, units: String) {
        locationLoading.value = true
        disposable.add(
            apiClient.getDataFromGps(latitude, longitude, units)
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherResponse>() {
                    override fun onSuccess(t: WeatherResponse) {
                        locationData.value = t
                        locationLoading.value = false
                        Log.i("BİLGİ : ", "CALIŞTI")
                    }

                    override fun onError(e: Throwable) {
                        Log.i("BİLGİ : ", "HATA" + e.message + " " + e.printStackTrace())

                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}