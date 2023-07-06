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
import com.ismailhakkiaydin.weather.adapter.HourlyAdapter
import com.ismailhakkiaydin.weather.databinding.FragmentFiveDaysBinding
import com.ismailhakkiaydin.weather.util.Constant
import com.ismailhakkiaydin.weather.viewmodel.FiveDaysViewModel
import im.delight.android.location.SimpleLocation

class FiveDaysFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE = 1
    }

    private var location: SimpleLocation? = null
    private var latitude: String? = null
    private var longitude: String? = null

    private var _dataBinding: FragmentFiveDaysBinding? = null
    private val dataBinding get() = _dataBinding!!

    private lateinit var viewModel: FiveDaysViewModel
    private val hourlyAdapter = HourlyAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_five_days, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProviders.of(this)[FiveDaysViewModel::class.java]

        dataBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        dataBinding.recyclerView.adapter = hourlyAdapter

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
        viewModel.getForecastFromGps(latitude!!, longitude!!, Constant.METRIC)

        viewModel.forecastData.observe(viewLifecycleOwner) { forecastGps ->
            forecastGps?.let {
                dataBinding.crdFiveDays.visibility = View.VISIBLE
                hourlyAdapter.updateHourlyList(forecastGps)

            }
        }

        viewModel.fiveDaysLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                if (it) {
                    dataBinding.fiveDaysLoading.visibility = View.VISIBLE
                    dataBinding.crdFiveDays.visibility = View.GONE
                } else {
                    dataBinding.fiveDaysLoading.visibility = View.GONE
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

                viewModel.getForecastFromGps(latitude!!, longitude!!, Constant.METRIC)

            } else {
                Toast.makeText(context, "İzin vereydin de konumunu bulaydık :P", Toast.LENGTH_LONG)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
