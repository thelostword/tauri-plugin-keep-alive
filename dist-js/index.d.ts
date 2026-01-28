/**
 * 启动保活服务（仅在 Android 上生效）
 * @param options 可选配置项
 * @param options.title 通知标题
 * @param options.message 通知消息
 * @returns Promise<{success: boolean, message?: string, error?: string}>
 */
export declare function startKeepAlive(options?: {
    title?: string;
    message?: string;
}): Promise<{
    success: boolean;
    message?: string;
    error?: string;
}>;
/**
 * 停止保活服务
 * @returns Promise<{success: boolean, message?: string, error?: string}>
 */
export declare function stopKeepAlive(): Promise<{
    success: boolean;
    message?: string;
    error?: string;
}>;
/**
 * 检查保活服务是否正在运行
 * @returns Promise<{running: boolean}>
 */
export declare function isKeepAliveRunning(): Promise<{
    running: boolean;
}>;
/**
 * 请求电池优化豁免（仅 Android 6.0+）
 * 会打开系统对话框让用户授权
 * @returns Promise<{success: boolean, message?: string, error?: string}>
 */
export declare function requestBatteryOptimization(): Promise<{
    success: boolean;
    message?: string;
    error?: string;
}>;
/**
 * 检查是否已豁免电池优化
 * @returns Promise<{ignored: boolean}>
 */
export declare function isBatteryOptimizationIgnored(): Promise<{
    ignored: boolean;
}>;
