# Tauri Plugin Open Permission Settings Page

A Tauri plugin for keeping the application alive on Android by running a foreground service with a persistent notification.

| Platform | Supported |
| -------- | --------- |
| Linux    | ✗         |
| Windows  | ✗         |
| macOS    | ✗         |
| Android  | ✓         |
| iOS      | ✗         |

## Install

_This plugin requires a Rust version of at least **1.77.2**_

Install the Core plugin by adding the following to your `Cargo.toml` file:

`src-tauri/Cargo.toml`

```toml
[dependencies]
# tauri-plugin-keep-alive = "0.1.0"
# alternatively with Git:
tauri-plugin-keep-alive = { git = "https://github.com/thelostword/tauri-plugin-keep-alive", branch = "main" }
```

You can install the JavaScript Guest bindings using your preferred JavaScript package manager:

```sh
bun add https://github.com/thelostword/tauri-plugin-keep-alive
# or
pnpm add https://github.com/thelostword/tauri-plugin-keep-alive
# or
npm add https://github.com/thelostword/tauri-plugin-keep-alive
# or
yarn add https://github.com/thelostword/tauri-plugin-keep-alive
```

## Usage

First you need to register the core plugin with Tauri:

`src-tauri/src/lib.rs`

```rust
#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_keep_alive::init())
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
```

Second, add the required permissions in the project:

`src-tauri/capabilities/default.json`

```json
{
  "permissions": [
    "tauri-plugin-keep-alive:default"
  ]
}
```

Afterwards all the plugin's APIs are available through the JavaScript guest bindings:

```typescript
import {
  startKeepAlive,
  stopKeepAlive,
  isKeepAliveRunning,
  requestBatteryOptimization,
  isBatteryOptimizationIgnored 
} from 'tauri-plugin-keep-alive';

const status = await isBatteryOptimizationIgnored();
const keepAliveRunning = await isKeepAliveRunning();
console.log('Is keep alive running:', keepAliveRunning);

if (!status.ignored) {
  await requestBatteryOptimization();
}

if (!keepAliveRunning && status.ignored) {
  await startKeepAlive({
    notificationTitle: 'App is running',
    notificationMessage: 'Syncing data in the background...',
  });
}
// To stop the keep-alive service
if (keepAliveRunning) {
  await stopKeepAlive();
}
```

## Credits and Thanks

- [plugins-workspace](https://github.com/tauri-apps/plugins-workspace) - For showing me how to build Tauri Plugins
- This plugin was developed with the assistance of AI technology (GitHub Copilot / Claude)

