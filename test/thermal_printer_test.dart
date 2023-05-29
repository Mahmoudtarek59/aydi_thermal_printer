import 'package:flutter_test/flutter_test.dart';
import 'package:thermal_printer/thermal_printer.dart';
import 'package:thermal_printer/thermal_printer_platform_interface.dart';
import 'package:thermal_printer/thermal_printer_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockThermalPrinterPlatform
    with MockPlatformInterfaceMixin
    implements ThermalPrinterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final ThermalPrinterPlatform initialPlatform = ThermalPrinterPlatform.instance;

  test('$MethodChannelThermalPrinter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelThermalPrinter>());
  });

  test('getPlatformVersion', () async {
    ThermalPrinter thermalPrinterPlugin = ThermalPrinter();
    MockThermalPrinterPlatform fakePlatform = MockThermalPrinterPlatform();
    ThermalPrinterPlatform.instance = fakePlatform;

    expect(await thermalPrinterPlugin.getPlatformVersion(), '42');
  });
}
