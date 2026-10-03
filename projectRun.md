# server
./gradlew :server:run


# app

# 1. Forward port 5000 from your phone/emulator to your PC
adb reverse tcp:5000 tcp:5000

# 2. Build and install with the backend API URL pointing to localhost:5000
./gradlew :androidApp:installDebug -PPROSTUTI_API_BASE_URL=http://localhost:5000

# 3. Launch MainActivity
adb shell am start -n com.prostuti.app/.MainActivity



# Option 2: Using Preconfigured VS Code Tasks (Easiest & Recommended)
Preconfigured tasks have been set up in .vscode/tasks.json.

1. Open the Command Palette in VS Code: Ctrl + Shift + P (or Cmd + Shift + P on macOS).
2. Type and select Tasks: Run Task.
3. Choose:
-  Run Ktor Server: Starts the backend server on port 5000 in a dedicated background terminal.
-  Run Full App (Bridge + Install + Launch): Automatically runs adb reverse tcp:5000 tcp:5000, builds and installs the app, and launches MainActivity.
4. To view live logs from the app, run task View Android Logs (Logcat).

TIP: You can also press Ctrl + Shift + B at any time to run the default build & launch task.