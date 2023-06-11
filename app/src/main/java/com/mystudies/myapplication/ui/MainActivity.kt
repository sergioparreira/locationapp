package com.mystudies.myapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mystudies.myapplication.R
import com.mystudies.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mViewBinding: ActivityMainBinding
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private val intervalTime: Long = 3000
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var buttonClicked = "non"

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0.lastLocation?.let {
                updateLatilonMonitoramento("${it.latitude}, ${it.longitude}")
            } ?: run {
                updateLatilonFixa(getString(R.string.zero_latilon))
            }
        }
    }

    private val requestMulitplesPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                fluxoLocPermissao()
            } else {
                onPermissionDeny(getString(R.string.first_plan_permission_needed))
            }
        }

    private val requestUniquePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (buttonClicked == getString(R.string.monitoring_button)) {
                monitorarLocalizacaoAtual()
            }
        } else {
            onPermissionDeny(getString(R.string.second_plan_permission_needed))
        }
    }

    private fun fluxoLocPermissao() {
        if (buttonClicked == getString(R.string.current_button)) {
            capturarLocalizacaoAtual()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestUniquePermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else if (buttonClicked ==  getString(R.string.monitoring_button)) {
                monitorarLocalizacaoAtual()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun capturarLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                location?.let {
                    updateLatilonFixa("${it.latitude}, ${it.longitude}")
                } ?: run {
                    updateLatilonFixa(getString(R.string.zero_latilon))
                }
            }
            ?.addOnFailureListener {
                updateLatilonFixa(getString(R.string.zero_latilon))
            }
    }

    private fun updateLatilonFixa(latilon: String) {
        mViewBinding.latilonFixa.text = latilon
    }

    private fun updateLatilonMonitoramento(latilon: String) {
        mViewBinding.latilonMonitoramento.text = latilon
    }


    private fun monitorarLocalizacaoAtual() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalTime)
            .setWaitForAccurateLocation(false)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        onClick()
    }

    private fun onPermissionDeny(message: String) {
        AlertDialog.Builder(this@MainActivity).setTitle(getString(R.string.atencao))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun onClick() {
        mViewBinding.buttonCapturarLocAtual.setOnClickListener {
            buttonClicked = getString(R.string.current_button)
            requestMulitplesPermissionLauncher.launch(permissions)
        }
        mViewBinding.buttonMonitorarLocalizacao.setOnClickListener {
            buttonClicked = getString(R.string.monitoring_button)
            requestMulitplesPermissionLauncher.launch(permissions)
        }
    }
}

