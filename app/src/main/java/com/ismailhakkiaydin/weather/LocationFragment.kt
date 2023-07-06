package com.ismailhakkiaydin.weather


import android.annotation.SuppressLint
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
import com.ismailhakkiaydin.weather.databinding.FragmentLocationBinding
import com.ismailhakkiaydin.weather.util.Constant
import com.ismailhakkiaydin.weather.util.dateConverter
import com.ismailhakkiaydin.weather.util.timeConverter
import com.ismailhakkiaydin.weather.viewmodel.LocationViewModel
import im.delight.android.location.SimpleLocation

class LocationFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 1
    }

    private lateinit var viewModel: LocationViewModel
    private var _dataBinding: FragmentLocationBinding? = null
    private val dataBinding get() = _dataBinding!!

    private var location: SimpleLocation? = null
    private var latitude: String? = null
    private var longitude: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)
        return dataBinding.root
    }

    @SuppressLint("DiscouragedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[LocationViewModel::class.java]

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
        viewModel.getWeatherDataWithGPS(latitude!!, longitude!!, Constant.METRIC)

        viewModel.locationData.observe(viewLifecycleOwner) { locationGps ->
            locationGps?.let {
                dataBinding.lytLocation.visibility = View.VISIBLE
                dataBinding.locationGPS = locationGps
                dataBinding.tvTemperature.text = locationGps.main!!.temp.toInt().toString()
                dataBinding.tvDate.text = dateConverter()
                dataBinding.tvSunrise.text = timeConverter((locationGps.sys!!.sunrise).toLong())
                dataBinding.tvSunset.text = timeConverter((locationGps.sys!!.sunset).toLong())
                dataBinding.imgState.setImageResource(
                    resources.getIdentifier(
                        "ic_" + locationGps.weather?.get(
                            0
                        )?.icon, "drawable", view.context.packageName
                    )
                )

            }
        }

        viewModel.locationLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                if (it) {
                    dataBinding.locationLoading.visibility = View.VISIBLE
                    dataBinding.lytLocation.visibility = View.GONE
                } else {
                    dataBinding.locationLoading.visibility = View.GONE
                }
            }
        }

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

                viewModel.getWeatherDataWithGPS(latitude!!, longitude!!, Constant.METRIC)

            } else {
                Toast.makeText(context, "İzin vereydin de konumunu bulaydık :P", Toast.LENGTH_LONG)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
