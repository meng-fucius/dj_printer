import 'dart:async';
import 'dart:convert';

import 'package:dj_printer/dj_printer.dart';
import 'package:dj_printer/src/device_model.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<Device> devices = [];

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    var per = await Permission.bluetooth.isGranted;
    if (!per) {
      Permission.bluetooth.request();
    }
    var pers = await Permission.locationWhenInUse.isGranted;
    if (!pers) {
      Permission.locationWhenInUse.request();
    }
    DjPrinter().init();
    DjPrinter().addDiscoveryListen(onReceive: (data) {
      var js = json.decode(data.toString());
      devices.add(Device(
          name: js['name'], address: js['address'], isPaired: js['isPaired']));
      setState(() {});
    }, onStart: () {
      print("————————————————————————");
    }, onFinish: () {
      print('——————————————————————————————');
      DjPrinter().cancelDiscovery();
    });
    DjPrinter().addConnectListen(onConnect: () {
      print("connected");
    }, onDisconnect: () {
      print('disconnected');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              TextButton(
                  onPressed: () {
                    devices.clear();
                    DjPrinter().startSearch;
                  },
                  child: const Text('扫描设备')),
              // TextButton(onPressed: () {}, child: const Text('打印')),
              const SizedBox(
                height: 20,
              ),
              ...devices
                  .map((e) => TextButton(
                      onPressed: () {
                        DjPrinter().connect(e.address);
                      },
                      child: Text(e.name)))
                  .toList(),
              const SizedBox(
                height: 20,
              ),
              TextButton(
                  onPressed: () {
                    DjPrinter().print(
                        code: 'ASSZ2022012500010002',
                        channel: 'cosco定提-月达-卡派',
                        country: '美国',
                        countStr: '10/20',
                        offset: 0,
                        hasPlan: false);
                  },
                  child: const Text('打印'))
            ],
          ),
        ),
      ),
    );
  }
}
