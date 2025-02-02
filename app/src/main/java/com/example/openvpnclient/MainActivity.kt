package com.example.openvpnclient

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isVpnConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Обработка кнопки "Подключиться"
        binding.btnConnect.setOnClickListener {
            if (!isVpnConnected) {
                connectToVpn()
            }
        }

        // Обработка кнопки "Отключиться"
        binding.btnDisconnect.setOnClickListener {
            if (isVpnConnected) {
                disconnectFromVpn()
            }
        }
    }

    private fun connectToVpn() {
        // Путь к конфигурационному файлу OpenVPN (*.ovpn)
        val configPath = "/storage/emulated/0/Download/config.ovpn"

        // Проверка существования файла
        if (!java.io.File(configPath).exists()) {
            btnConnect.text = "Файл не найден!"
            return
        }

        // Запуск OpenVPN через Intent
        val intent = Intent("android.intent.action.VIEW")
        intent.setDataAndType(android.net.Uri.parse("file://$configPath"), "application/x-openvpn-profile")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            startActivity(intent)
            isVpnConnected = true
            btnConnect.isEnabled = false
            btnDisconnect.isEnabled = true
        } catch (e: Exception) {
            btnConnect.text = "Ошибка: ${e.message}"
        }
    }

    private fun disconnectFromVpn() {
        // Отключение от VPN
        VpnService.prepare(this)?.let { prepareIntent ->
            startService(Intent(prepareIntent))
        }
        isVpnConnected = false
        btnConnect.isEnabled = true
        btnDisconnect.isEnabled = false
    }
}