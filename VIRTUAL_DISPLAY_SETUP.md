# Hosted app windows (real Android apps inside Plasma-style windows)

PocketOS creates one virtual display per app, launches the app on it, and shows it inside
its own Plasma window, so the title bar, corners, shadow and translucency are PocketOS's.
This needs Shizuku, because only the shell user may create trusted virtual displays.

## Manual setup (once per phone boot)
1. Android 13 or newer is required (trusted virtual displays).
2. Install **Shizuku** (Google Play or github.com/RikkaApps/Shizuku/releases).
3. Developer options: turn on **Wireless debugging** (the phone must be on Wi-Fi, any network).
   Xiaomi/HyperOS: also enable "USB debugging (Security settings)".
4. Open Shizuku -> "Start via Wireless debugging" -> Pair with pairing code
   (split-screen or the notification's reply field), then tap **Start**.
   Alternatives: root (Start button), or from a PC: `adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh`.
5. Build and install this project (Gradle sync downloads the Shizuku libraries).
6. Open PocketOS on the phone -> menu (⋮) -> **Shizuku: conectar** -> Allow PocketOS in the Shizuku dialog.
   PocketOS then starts the desktop on the TV itself (needed on Android 17, where a normal
   launch fails with "Permission Denial ... launchDisplayId"). Settings -> Window Decorations
   also shows the status: "Connected".
7. Open any app from the launcher: it appears in a Plasma window. Drag, resize, minimize, close as usual.

Shizuku stops when the phone reboots; repeat steps 3-4 (step 6 is remembered).

## Notes
- Touch, cursor and scroll are forwarded into the app. A physical keyboard types into the focused window.
  The on-screen keyboard does not reach hosted apps.
- Apps that block screen capture (banking, DRM video) may show black.
- If Shizuku is not connected, apps fall back to Android's own freeform windows.
- Diagnostics lists lines starting with "VD:" showing the display id, size and `am start` output.
