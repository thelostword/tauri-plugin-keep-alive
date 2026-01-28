use tauri::{AppHandle, command, Runtime};

use crate::models::*;
use crate::Result;
use crate::KeepAliveExt;

#[command]
pub(crate) async fn start_keep_alive<R: Runtime>(
    app: AppHandle<R>,
    payload: StartKeepAliveRequest,
) -> Result<KeepAliveResponse> {
    app.keep_alive().start_keep_alive(payload)
}

#[command]
pub(crate) async fn stop_keep_alive<R: Runtime>(
    app: AppHandle<R>,
) -> Result<KeepAliveResponse> {
    app.keep_alive().stop_keep_alive()
}

#[command]
pub(crate) async fn is_keep_alive_running<R: Runtime>(
    app: AppHandle<R>,
) -> Result<IsRunningResponse> {
    app.keep_alive().is_keep_alive_running()
}

#[command]
pub(crate) async fn request_battery_optimization<R: Runtime>(
    app: AppHandle<R>,
) -> Result<KeepAliveResponse> {
    app.keep_alive().request_battery_optimization()
}

#[command]
pub(crate) async fn is_battery_optimization_ignored<R: Runtime>(
    app: AppHandle<R>,
) -> Result<BatteryOptimizationResponse> {
    app.keep_alive().is_battery_optimization_ignored()
}
