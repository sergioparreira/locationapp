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


    private var buttonClicked = "non"

    private val requestMulitplesPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                fluxoLocPermissao()
            } else {
                onPermissionDeny("É necessário as permissões em primeiro plano para utilizar todas as funcionalidade")
            }
        }

    private val requestUniquePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (buttonClicked == "monitoramento") {
                monitorarLocalizacaoAtual()
            }
        } else {
            onPermissionDeny("é necessário as permissões em segundo plano para utilizar todas as funcionalidade")
        }
    }

    private fun fluxoLocPermissao() {
        if (buttonClicked == "locatual") {
            capturarLocalizacaoAtual()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestUniquePermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else if (buttonClicked == "monitoramento") {
                monitorarLocalizacaoAtual()
            }
        }
    }

    private fun capturarLocalizacaoAtual() {
        Toast.makeText(this, "capturandoLoc", Toast.LENGTH_LONG).show()
    }

    private fun monitorarLocalizacaoAtual() {
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
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun onClick() {
        mViewBinding.buttonCapturarLocAtual.setOnClickListener {
            buttonClicked = "locatual"
            requestMulitplesPermissionLauncher.launch(permissions)
        }
        mViewBinding.buttonMonitorarLocalizacao.setOnClickListener {
            buttonClicked = "monitoramento"
            requestMulitplesPermissionLauncher.launch(permissions)
        }
    }
}

