use serde::de::DeserializeOwned;
use tauri::{
  plugin::{PluginApi, PluginHandle},
  AppHandle, Runtime,
};

use crate::models::*;

#[cfg(target_os = "ios")]
tauri::ios_plugin_binding!(init_plugin_keep_alive);

// initializes the Kotlin or Swift plugin classes
pub fn init<R: Runtime, C: DeserializeOwned>(
  #[cfg(target_os = "android")]
  _app: &AppHandle<R>,
  #[cfg(target_os = "ios")]
  app: &AppHandle<R>,
  api: PluginApi<R, C>,
) -> crate::Result<KeepAlive<R>> {
  #[cfg(target_os = "android")]
  let handle = api.register_android_plugin("com.plugin.keep_alive", "KeepAlivePlugin")?;
  #[cfg(target_os = "ios")]
  let handle = app.clone();
  Ok(KeepAlive(handle))
}

/// Access to the keep-alive APIs.
#[cfg(target_os = "android")]
pub struct KeepAlive<R: Runtime>(PluginHandle<R>);
#[cfg(target_os = "ios")]
pub struct KeepAlive<R: Runtime>(AppHandle<R>);

#[cfg(target_os = "android")]
impl<R: Runtime> KeepAlive<R> {
  pub fn start_keep_alive(&self, payload: StartKeepAliveRequest) -> crate::Result<KeepAliveResponse> {
    self
      .0
      .run_mobile_plugin("startKeepAlive", payload)
      .map_err(Into::into)
  }

  pub fn stop_keep_alive(&self) -> crate::Result<KeepAliveResponse> {
    self
      .0
      .run_mobile_plugin("stopKeepAlive", ())
      .map_err(Into::into)
  }

  pub fn is_keep_alive_running(&self) -> crate::Result<IsRunningResponse> {
    self
      .0
      .run_mobile_plugin("isKeepAliveRunning", ())
      .map_err(Into::into)
  }

  pub fn request_battery_optimization(&self) -> crate::Result<KeepAliveResponse> {
    self
      .0
      .run_mobile_plugin("requestBatteryOptimization", ())
      .map_err(Into::into)
  }

  pub fn is_battery_optimization_ignored(&self) -> crate::Result<BatteryOptimizationResponse> {
    self
      .0
      .run_mobile_plugin("isBatteryOptimizationIgnored", ())
      .map_err(Into::into)
  }
}

#[cfg(target_os = "ios")]
impl<R: Runtime> KeepAlive<R> {
  pub fn start_keep_alive(&self, _payload: StartKeepAliveRequest) -> crate::Result<KeepAliveResponse> {
    Ok(KeepAliveResponse {
      success: false,
      message: None,
      error: Some("iOS platform does not support keep-alive service".to_string()),
    })
  }

  pub fn stop_keep_alive(&self) -> crate::Result<KeepAliveResponse> {
    Ok(KeepAliveResponse {
      success: false,
      message: None,
      error: Some("iOS platform does not support keep-alive service".to_string()),
    })
  }

  pub fn is_keep_alive_running(&self) -> crate::Result<IsRunningResponse> {
    Ok(IsRunningResponse {
      running: false,
    })
  }

  pub fn request_battery_optimization(&self) -> crate::Result<KeepAliveResponse> {
    Ok(KeepAliveResponse {
      success: false,
      message: None,
      error: Some("iOS platform does not support battery optimization".to_string()),
    })
  }

  pub fn is_battery_optimization_ignored(&self) -> crate::Result<BatteryOptimizationResponse> {
    Ok(BatteryOptimizationResponse {
      ignored: false,
    })
  }
}
