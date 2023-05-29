import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'thermal_printer_method_channel.dart';

abstract class ThermalPrinterPlatform extends PlatformInterface {
  ThermalPrinterPlatform() : super(token: _token);

  static final Object _token = Object();

  static ThermalPrinterPlatform _instance = MethodChannelThermalPrinter();

  static ThermalPrinterPlatform get instance => _instance;

  static set instance(ThermalPrinterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion();

  Future<List<String>> scanBluetoothDevices();

  Future<bool> connectToDevice(String deviceId);

  Future<void> sendEPL2Command(String command);


  Future<String?> getDeviceIdByName(String deviceName);


}
