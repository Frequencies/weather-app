package com.ismailhakkiaydin.weather

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismailhakkiaydin.weather.adapter.CityDailyAdapter
import com.ismailhakkiaydin.weather.databinding.FragmentCityDailyBinding
import com.ismailhakkiaydin.weather.util.Constant
import com.ismailhakkiaydin.weather.viewmodel.CityDailyViewModel
import im.delight.android.location.SimpleLocation

class CityDailyFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 1
    }

    private var location: SimpleLocation? = null
    private var latitude: String? = null
    private var longitude: String? = null

    private lateinit var viewModel: CityDailyViewModel
    private var _dataBinding: FragmentCityDailyBinding? = null
    private val dataBinding get() = _dataBinding!!

    private var cityDailyAdapter = CityDailyAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_city_daily, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dataBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        dataBinding.recyclerView.adapter = cityDailyAdapter

        viewModel = ViewModelProviders.of(this)[CityDailyViewModel::class.java]

        location = SimpleLocation(context)
        if (!location!!.hasLocationEnabled()) {
            SimpleLocation.openSettings(context)
        } else {
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            } else {
                location = SimpleLocation(context)
                latitude = String.format("%.6f", location?.latitude)
                longitude = String.format("%.6f", location?.longitude)
                Log.e("LAT1", "" + latitude)
                Log.e("LONG1", "" + longitude)

            }
        }

        viewModel.getCityDailyWeatherFromGps(latitude!!, longitude!!, Constant.CNT, Constant.METRIC)

        viewModel.cityDailyData.observe(viewLifecycleOwner) { cityDailyWeatherGps ->
            cityDailyWeatherGps.let {
                dataBinding.recyclerView.visibility = View.VISIBLE
                cityDailyAdapter.updateCountryList(cityDailyWeatherGps)
            }
        }

        viewModel.cityDailyLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                if (it) {
                    dataBinding.cityDailyLoading.visibility = View.VISIBLE
                    dataBinding.recyclerView.visibility = View.GONE
                } else {
                    dataBinding.cityDailyLoading.visibility = View.GONE
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBinding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location = SimpleLocation(context)
                latitude = String.format("%.6f", location?.latitude)
                longitude = String.format("%.6f", location?.longitude)
                Log.e("LAT", "" + latitude)
                Log.e("LONG", "" + longitude)

                viewModel.getCityDailyWeatherFromGps(
                    latitude!!,
                    longitude!!,
                    Constant.CNT,
                    Constant.METRIC
                )

            } else {
                Toast.makeText(context, "İzin vereydin de konumunu bulaydık :P", Toast.LENGTH_LONG)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
