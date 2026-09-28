server: ./gradlew :server:run
mobileBuildand Install: ./gradlew :androidApp:installDebug -PPROSTUTI_API_BASE_URL=http://localhost:5000


# 1. Bridge the network
adb reverse tcp:5000 tcp:5000
adb reverse --list

# 2. Build and Install
./gradlew :androidApp:installDebug

# 3. Launch
adb shell am start -n com.prostuti.app/.MainActivity