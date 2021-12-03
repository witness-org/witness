# Tests are only run on device (e.g. emulator) if they are contained in the "integration_test" folder
Copy-Item -Recurse -Force . ..\..\integration_test\integration
Copy-Item -Recurse -Force ..\common ..\..\integration_test

# They also have to be executed from the project's root folder
cd ..\..
flutter test --reporter expanded integration_test

# Cleanup and return to initial directory
Remove-Item -Recurse -Force integration_test
cd test\integration