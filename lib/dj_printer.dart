import 'dart:async';

import 'package:dj_printer/src/status_enum.dart';
import 'package:flutter/services.dart';

class DjPrinter {
  static const MethodChannel _channel = MethodChannel('dj_printer');
  static const EventChannel _deviceChannel =
      EventChannel("com.discovery.devices");
  static StreamSubscription? _discoveryStream;

  static Future<StreamSubscription> addDiscoveryListen(
      {required void Function(dynamic data) onReceive,
      void Function()? onStart,
      void Function()? onFinish}) async {
    if (_discoveryStream == null) {
      return _deviceChannel.receiveBroadcastStream().listen((data) {
        if (data == "start" && onStart != null) {
          onStart();
        } else if (data == "finish" && onFinish != null) {
          onFinish();
        } else {
          onReceive(data);
        }
      });
    } else {
      return _discoveryStream!;
    }
  }

  static void cancelDiscovery() {
    if (_discoveryStream != null) {
      _discoveryStream!.cancel();
      _discoveryStream = null;
    }
  }

  static const EventChannel _connectChannel = EventChannel("com.connect");
  static StreamSubscription? _connectStream;

  static Future<StreamSubscription> addConnectListen(
      {required void Function() onConnect,
      required void Function() onDisconnect}) async {
    if (_connectStream == null) {
      return _connectChannel.receiveBroadcastStream().listen((data) {
        if (data == 'connected') {
          onConnect();
        } else if (data == 'disconnected') {
          onDisconnect();
        }
      });
    } else {
      return _connectStream!;
    }
  }

  static void cancelConnect() {
    if (_connectStream != null) {
      _connectStream!.cancel();
      _connectStream = null;
    }
  }

  static Future<bool?> get startSearch async {
    final res = await _channel.invokeMethod('startSearch');
    return res;
  }

  static Future<bool?> connect(String address) async {
    final res = await _channel.invokeMethod('connect', {'address': address});
    return res;
  }

  static Future<bool?> init() async {
    final res = await _channel.invokeMethod('init');
    return res;
  }

  //0 normal
  //1 busy
  //2 paper empty
  //4 cover open
  //8 battery low
  static Future<PRINT_STATUS?> getStatus() async {
    final res = await _channel.invokeMethod('getStatus');
    switch (res) {
      case 0:
        return PRINT_STATUS.normal;
      case 1:
        return PRINT_STATUS.busy;
      case 2:
        return PRINT_STATUS.paperEmpty;
      case 4:
        return PRINT_STATUS.coverOpen;
      case 8:
        return PRINT_STATUS.batteryLow;
      default:
        return null;
    }
  }

  static Future<bool?> print(
      {required String code,
      required String channel,
      required String country,
      required String countStr,
      required int offset,
      required bool hasPlan}) async {
    final res = await _channel.invokeMethod('print', {
      'code': code,
      'channel': channel,
      'country': country,
      'countStr': countStr,
      'offset': offset,
      'hasPlan': hasPlan,
    });
    return res;
  }
}
