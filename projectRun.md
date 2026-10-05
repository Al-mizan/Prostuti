# server
./gradlew :server:run


# app (Live Backend: https://api-prostuti.onrender.com)

# 1. Build and install with the live backend API URL
./gradlew :androidApp:installDebug -PPROSTUTI_API_BASE_URL=https://api-prostuti.onrender.com

# 2. Launch MainActivity
adb shell am start -n com.prostuti.app/.MainActivity

# For Local Backend (Optional):
# 1. Forward port 5000 from your phone/emulator to your PC
# adb reverse tcp:5000 tcp:5000
# 2. Build and install with localhost
# ./gradlew :androidApp:installDebug -PPROSTUTI_API_BASE_URL=http://localhost:5000
