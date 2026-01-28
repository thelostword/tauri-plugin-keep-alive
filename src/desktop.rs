use serde::de::DeserializeOwned;
use tauri::{plugin::PluginApi, AppHandle, Runtime};

use crate::models::*;

pub fn init<R: Runtime, C: DeserializeOwned>(
  app: &AppHandle<R>,
  _api: PluginApi<R, C>,
) -> crate::Result<KeepAlive<R>> {
  Ok(KeepAlive(app.clone()))
}

/// Access to the keep-alive APIs.
pub struct KeepAlive<R: Runtime>(AppHandle<R>);

impl<R: Runtime> KeepAlive<R> {
  pub fn start_keep_alive(&self, _payload: StartKeepAliveRequest) -> crate::Result<KeepAliveResponse> {
    Ok(KeepAliveResponse {
      success: false,
      message: None,
      error: Some("Keep-alive is not supported on desktop platforms".to_string()),
    })
  }

  pub fn stop_keep_alive(&self) -> crate::Result<KeepAliveResponse> {
    Ok(KeepAliveResponse {
      success: false,
      message: None,
      error: Some("Keep-alive is not supported on desktop platforms".to_string()),
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
      error: Some("Keep-alive is not supported on desktop platforms".to_string()),
    })
  }

  pub fn is_battery_optimization_ignored(&self) -> crate::Result<BatteryOptimizationResponse> {
    Ok(BatteryOptimizationResponse {
      ignored: false,
    })
  }
}
