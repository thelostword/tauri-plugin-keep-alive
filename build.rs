const COMMANDS: &[&str] = &[
    "start_keep_alive",
    "stop_keep_alive",
    "is_keep_alive_running",
    "request_battery_optimization",
    "is_battery_optimization_ignored",
];

fn main() {
  tauri_plugin::Builder::new(COMMANDS)
    .android_path("android")
    // .ios_path("ios")
    .build();
}
