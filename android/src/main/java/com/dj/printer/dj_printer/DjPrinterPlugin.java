package com.dj.printer.dj_printer;

import android.content.Context;

import androidx.annotation.NonNull;


import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.jpos.printer.CPCLPrinter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * DjPrinterPlugin
 */
public class DjPrinterPlugin implements FlutterPlugin, MethodCallHandler {
    private MethodChannel channel;
    private EventChannel discoveryChannel;
    private EventChannel connectChannel;
    private Context context;
    AsPrint asPrint;
    private static final String DISCOVERYDEVICE = "com.discovery.devices";
    private static final String CONNECT = "com.connect";

    Blutooth blutooth = new Blutooth();

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "dj_printer");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
        discoveryChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), DISCOVERYDEVICE);

        connectChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), CONNECT);
        discoveryChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                blutooth.createDiscoveryBroadcast(context, events);
            }

            @Override
            public void onCancel(Object arguments) {
                blutooth.cancelDiscoveryResult(context);
            }
        });
        connectChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                blutooth.createConnectBroadcast(context, events);
            }

            @Override
            public void onCancel(Object arguments) {
                blutooth.ExcuteDisconnect(context);
            }
        });
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("startSearch")) {
            blutooth.SearchingBTDevice();
        } else if (call.method.equals("connect")) {
            String arg = call.argument("address");
            try {
                blutooth.btConn(arg);
                result.success(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (call.method.equals("init")) {
            blutooth.init();
            asPrint = new AsPrint();

            result.success(true);
        } else if (call.method.equals("print")) {
            try {
                String code = call.argument("code");
                String channel = call.argument("channel");
                String country = call.argument("country");
                String countStr = call.argument("countStr");
                int offset = call.argument("offset");
                boolean hasPlan = call.argument("hasPlan");
                asPrint.printAsCode(code, channel, country, countStr, offset, hasPlan);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            result.success(true);
        } else if (call.method.equals("getStatus")) {
            int sta = asPrint.Get_Status();
            result.success(sta);
        } else if (call.method.equals("disposeDiscovery")) {
            blutooth.cancelDiscoveryResult(context);
            result.success(true);
        } else if (call.method.equals("disposeConnect")) {
            blutooth.ExcuteDisconnect(context);
            result.success(true);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

}
