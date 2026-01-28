use tauri::{
  plugin::{Builder, TauriPlugin},
  Manager, Runtime,
};

pub use models::*;

#[cfg(desktop)]
mod desktop;
#[cfg(mobile)]
mod mobile;

mod commands;
mod error;
mod models;

pub use error::{Error, Result};

#[cfg(desktop)]
use desktop::KeepAlive;
#[cfg(mobile)]
use mobile::KeepAlive;

/// Extensions to [`tauri::App`], [`tauri::AppHandle`] and [`tauri::Window`] to access the keep-alive APIs.
pub trait KeepAliveExt<R: Runtime> {
  fn keep_alive(&self) -> &KeepAlive<R>;
}

impl<R: Runtime, T: Manager<R>> crate::KeepAliveExt<R> for T {
  fn keep_alive(&self) -> &KeepAlive<R> {
    self.state::<KeepAlive<R>>().inner()
  }
}

/// Initializes the plugin.
pub fn init<R: Runtime>() -> TauriPlugin<R> {
  Builder::new("keep-alive")
    .invoke_handler(tauri::generate_handler![
      commands::start_keep_alive,
      commands::stop_keep_alive,
      commands::is_keep_alive_running,
      commands::request_battery_optimization,
      commands::is_battery_optimization_ignored
    ])
    .setup(|app, api| {
      #[cfg(mobile)]
      let keep_alive = mobile::init(app, api)?;
      #[cfg(desktop)]
      let keep_alive = desktop::init(app, api)?;
      app.manage(keep_alive);
      Ok(())
    })
    .build()
}
