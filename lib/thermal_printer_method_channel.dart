import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'thermal_printer_platform_interface.dart';

class MethodChannelThermalPrinter extends ThermalPrinterPlatform {
  final MethodChannel methodChannel = const MethodChannel('thermal_printer');

  @override
  Future<List<String>> scanBluetoothDevices() async {
    final devices =
    await methodChannel.invokeListMethod<String>('scanBluetoothDevices');
    return devices ?? [];
  }

  @override
  Future<bool> connectToDevice(String deviceId) async {
    try {
      return await methodChannel.invokeMethod('connectToDevice', deviceId);
    } catch (e) {
      throw Exception('Failed to connect to device: $e');
    }
  }

  @override
  Future<void> sendEPL2Command(String command) async {
    try {
      await methodChannel.invokeMethod('sendEPL2Command', command);
    } catch (e) {
      throw Exception('Failed to send EPL2 command: $e');
    }
  }


  @override
  Future<String?> getPlatformVersion() async {
    final version =
    await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> getDeviceIdByName(String deviceName) async {

    return methodChannel.invokeMethod<String>('getBluetoothDeviceId', deviceName);
  }


}
