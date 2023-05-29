import 'thermal_printer_platform_interface.dart';

class ThermalPrinter {
  Future<String?> getPlatformVersion() {
    return ThermalPrinterPlatform.instance.getPlatformVersion();
  }

  Future<List<String>> scanBluetoothDevices() {
    return ThermalPrinterPlatform.instance.scanBluetoothDevices();
  }

  Future<bool> connectToDevice(String deviceId) {
    return ThermalPrinterPlatform.instance.connectToDevice(deviceId);
  }

  Future<void> sendEPL2Command(String command) {
    return ThermalPrinterPlatform.instance.sendEPL2Command(command);
  }



  Future<String?> getDeviceId(String deviceName) {
    return ThermalPrinterPlatform.instance.getDeviceIdByName(deviceName);
  }


}
