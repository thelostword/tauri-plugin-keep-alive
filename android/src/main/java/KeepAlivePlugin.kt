package com.plugin.keep_alive

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin
import app.tauri.plugin.Invoke

@InvokeArg
class StartKeepAliveArgs {
    var title: String? = null
    var message: String? = null
    var autoRestartOnTaskRemoved: Boolean = true
}

@TauriPlugin
class KeepAlivePlugin(private val activity: Activity): Plugin(activity) {

    /**
     * 启动保活服务
     */
    @Command
    fun startKeepAlive(invoke: Invoke) {
        try {
            val args = invoke.parseArgs(StartKeepAliveArgs::class.java)
            val intent = Intent(activity, KeepAliveService::class.java)

            // 将参数传递给 Service
            args.title?.let { intent.putExtra("title", it) }
            args.message?.let { intent.putExtra("message", it) }
            intent.putExtra("autoRestartOnTaskRemoved", args.autoRestartOnTaskRemoved)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent)
            } else {
                activity.startService(intent)
            }

            val ret = JSObject()
            ret.put("success", true)
            ret.put("message", "Keep alive service started")
            invoke.resolve(ret)
        } catch (e: Exception) {
            val ret = JSObject()
            ret.put("success", false)
            ret.put("error", e.message ?: "Failed to start service")
            invoke.reject(e.message ?: "Failed to start service")
        }
    }

    /**
     * 停止保活服务
     */
    @Command
    fun stopKeepAlive(invoke: Invoke) {
        try {
            val intent = Intent(activity, KeepAliveService::class.java)
            activity.stopService(intent)

            val ret = JSObject()
            ret.put("success", true)
            ret.put("message", "Keep alive service stopped")
            invoke.resolve(ret)
        } catch (e: Exception) {
            val ret = JSObject()
            ret.put("success", false)
            ret.put("error", e.message ?: "Failed to stop service")
            invoke.reject(e.message ?: "Failed to stop service")
        }
    }

    /**
     * 检查保活服务是否正在运行
     */
    @Command
    fun isKeepAliveRunning(invoke: Invoke) {
        val ret = JSObject()
        ret.put("running", KeepAliveService.isRunning())
        invoke.resolve(ret)
    }

    /**
     * 请求电池优化豁免
     */
    @Command
    fun requestBatteryOptimization(invoke: Invoke) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = activity.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
                val packageName = activity.packageName

                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = android.net.Uri.parse("package:$packageName")
                    activity.startActivity(intent)

                    val ret = JSObject()
                    ret.put("success", true)
                    ret.put("message", "Battery optimization request dialog opened")
                    invoke.resolve(ret)
                } else {
                    val ret = JSObject()
                    ret.put("success", true)
                    ret.put("message", "Already ignoring battery optimization")
                    invoke.resolve(ret)
                }
            } else {
                val ret = JSObject()
                ret.put("success", false)
                ret.put("error", "Battery optimization is only available on Android 6.0+")
                invoke.resolve(ret)
            }
        } catch (e: Exception) {
            val ret = JSObject()
            ret.put("success", false)
            ret.put("error", e.message ?: "Failed to request battery optimization")
            invoke.reject(e.message ?: "Failed to request battery optimization")
        }
    }

    /**
     * 检查是否已豁免电池优化
     */
    @Command
    fun isBatteryOptimizationIgnored(invoke: Invoke) {
        try {
            val ignored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = activity.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
                powerManager.isIgnoringBatteryOptimizations(activity.packageName)
            } else {
                true // Android 6.0 以下不需要此权限
            }

            val ret = JSObject()
            ret.put("ignored", ignored)
            invoke.resolve(ret)
        } catch (e: Exception) {
            val ret = JSObject()
            ret.put("ignored", false)
            invoke.resolve(ret)
        }
    }
}
