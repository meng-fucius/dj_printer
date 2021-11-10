
import 'dart:async';

import 'package:flutter/services.dart';

class DjPrinter {
  static const MethodChannel _channel = MethodChannel('dj_printer');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
