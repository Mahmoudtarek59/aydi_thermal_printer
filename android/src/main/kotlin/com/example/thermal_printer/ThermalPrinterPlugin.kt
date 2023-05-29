package com.example.thermal_printer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.OutputStream

import java.io.IOException
import java.io.PrintWriter
import java.util.*

class ThermalPrinterPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private lateinit var bluetoothAdapter: BluetoothAdapter
  private var printerDevice: BluetoothDevice? = null
  private var printerSocket: BluetoothSocket? = null
  companion object {
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
  }

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(binding.binaryMessenger, "thermal_printer")
    channel.setMethodCallHandler(this)
    context = binding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "scanBluetoothDevices" -> {
        val devices = scanBluetoothDevices()
        result.success(devices)
      }
      "connectToDevice" -> {
        val deviceId = call.arguments as? String?
        if (deviceId != null) {
          val res = connectToDevice(deviceId)
          result.success(res)
        } else {
          result.error("INVALID_ARGUMENT", "Device ID is required.", null)
        }
      }
      "sendEPL2Command" -> {
        val command = call.arguments as? String
        if (command != null) {
          sendEPL2Command(command)
          result.success(null)
        } else {
          result.error("INVALID_ARGUMENT", "EPL2 command is required.", null)
        }
      }
      "getBluetoothDeviceId" -> {
        val deviceId = getBluetoothDeviceId(call.arguments as? String?)
        if (deviceId != null) {
          result.success(deviceId)
        } else {
          result.error("INVALID_ARGUMENT", "Device ID is required.", null)
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun scanBluetoothDevices(): List<String> {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val pairedDevices = bluetoothAdapter.bondedDevices
    val scannedDevices = mutableListOf<String>()

    // Scan for Bluetooth devices
    bluetoothAdapter.startDiscovery()

    // Add paired devices
    pairedDevices.forEach { device ->
      scannedDevices.add(device.name)
    }

    // Add scanned devices
    val bluetoothReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (BluetoothDevice.ACTION_FOUND == action) {
          val device =
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
          device?.let {
            scannedDevices.add(device.name)
          }
        }
      }
    }

    // Register the receiver for Bluetooth device discovery
    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(bluetoothReceiver, filter)

    // Wait for scanning to finish
    Thread.sleep(5000) // Adjust the delay as needed

    // Unregister the receiver
    context.unregisterReceiver(bluetoothReceiver)

    return scannedDevices
  }


  private fun connectToDevice(deviceId: String): Boolean {
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // Check if Bluetooth is supported on the device
    if (bluetoothAdapter == null) {
      // Bluetooth is not supported
      return false
    }

    // Check if Bluetooth is enabled
    if (!bluetoothAdapter.isEnabled) {
      // Bluetooth is not enabled, request to enable it
      val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
      // Start an activity to enable Bluetooth
      // You can handle the result in onActivityResult() if needed
      context.startActivity(enableBluetoothIntent)
      return false
    }

    // Get the Bluetooth device by name
    val pairedDevices = bluetoothAdapter.bondedDevices
    print(pairedDevices)
    for (device in pairedDevices) {
      if (device.name == deviceId) {
        printerDevice = device
        break
      }
    }

    // Check if the printer device is found
    if (printerDevice == null) {
      // Printer device not found
      return false
    }

    // Establish a Bluetooth connection with the printer device
    val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    try {
      printerSocket = printerDevice?.createRfcommSocketToServiceRecord(uuid)
      printerSocket?.connect()

      // Connection successful, you can perform any necessary operations
      // such as setting the printer settings or initializing the printer
      return  true
    } catch (e: IOException) {
      // Error occurred while connecting to the printer device
      e.printStackTrace()
      return false

    }
  }


  private fun sendEPL2Command(command: String) {
    try {
      // Check if the printer socket is available
      if (printerSocket == null) {
        // Printer socket not available, connection might have failed
        return
      }

      // Create an output stream for writing data to the printer
      val outputStream = printerSocket!!.outputStream
      val writer = PrintWriter(outputStream, true)

      // Send the EPL2 command to the printer
      writer.println(command)

      // Flush and close the writer
      writer.flush()
//      writer.close()

    } catch (e: IOException) {
      // Error occurred while sending the EPL2 command
      e.printStackTrace()
    }
  }





  private fun getBluetoothDeviceId(deviceName: String?): String? {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val pairedDevices = bluetoothAdapter.bondedDevices

    for (device in pairedDevices) {
      if (device.name == deviceName) {
        return device.address
      }
    }

    return null
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
