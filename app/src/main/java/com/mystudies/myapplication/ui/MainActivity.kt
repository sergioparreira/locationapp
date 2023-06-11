package com.mystudies.myapplication.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mystudies.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mViewBinding: ActivityMainBinding
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val requestMulitplesPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                capturarLocalizacaoAtual()
            } else {
                onPermissionDeny("É necessário as permissões em primeiro plano para utilizar essa funcionalidade")
            }
        }

    private val requestUniquePermissionLaunche = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            monitorarLocalizacaoAtual()
        } else {
            onPermissionDeny("é necessário as permissões em segundo plano e acesso ao tempo todo da localização para utilizar essa funcionalidade")
        }
    }

    private fun capturarLocalizacaoAtual() {
        Toast.makeText(this, "capturandoLoc", Toast.LENGTH_LONG).show()
    }

    private fun monitorarLocalizacaoAtual(){
        Toast.makeText(this, "monitorandoLoc", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        onClick()
    }

    private fun onPermissionDeny(message: String) {
        AlertDialog.Builder(this@MainActivity).setTitle("Atenção")
            .setMessage("O App não funcionará sem as permissões de localizações")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun onClick() {
        mViewBinding.buttonCapturarLocAtual.setOnClickListener {
            requestMulitplesPermissionLauncher.launch(permissions)
        }
        mViewBinding.buttonMonitorarLocalizacao.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestUniquePermissionLaunche.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                monitorarLocalizacaoAtual()
            }
        }
    }

}