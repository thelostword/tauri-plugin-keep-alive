import { invoke } from '@tauri-apps/api/core'

/**
 * 启动保活服务（仅在 Android 上生效）
 * @param options 可选配置项
 * @param options.title 通知标题
 * @param options.message 通知消息
 * @param options.autoRestartOnTaskRemoved 任务移除时是否自动重启服务，默认为 true
 * @returns Promise<{success: boolean, message?: string, error?: string}>
 */
export async function startKeepAlive(options?: {
  title?: string;
  message?: string;
  autoRestartOnTaskRemoved?: boolean;
}): Promise<{ success: boolean; message?: string; error?: string }> {
  return await invoke('plugin:keep-alive|start_keep_alive', {
    payload: {
      title: options?.title,
      message: options?.message,
      autoRestartOnTaskRemoved: options?.autoRestartOnTaskRemoved ?? true,
    },
  });
}

/**
 * 停止保活服务
 * @returns Promise<{success: boolean, message?: string, error?: string}>
 */
export async function stopKeepAlive(): Promise<{
  success: boolean;
  message?: string;
  error?: string;
}> {
  return await invoke('plugin:keep-alive|stop_keep_alive');
}

/**
 * 检查保活服务是否正在运行
 * @returns Promise<{running: boolean}>
 */
export async function isKeepAliveRunning(): Promise<{ running: boolean }> {
  return await invoke('plugin:keep-alive|is_keep_alive_running');
}

/**
 * 请求电池优化豁免（仅 Android 6.0+）
 * 会打开系统对话框让用户授权
 * @returns Promise<{success: boolean, message?: string, error?: string}>
 */
export async function requestBatteryOptimization(): Promise<{
  success: boolean;
  message?: string;
  error?: string;
}> {
  return await invoke('plugin:keep-alive|request_battery_optimization');
}

/**
 * 检查是否已豁免电池优化
 * @returns Promise<{ignored: boolean}>
 */
export async function isBatteryOptimizationIgnored(): Promise<{
  ignored: boolean;
}> {
  return await invoke('plugin:keep-alive|is_battery_optimization_ignored');
}
