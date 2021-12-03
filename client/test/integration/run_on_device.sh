#!/usr/bin/env bash
# Tests are only run on device (e.g. emulator) if they are contained in the "integration_test" folder
mkdir -p ../../integration_test/integration && cp -r . ../../integration_test/integration
mkdir -p ../../integration_test && cp -r ../common ../../integration_test

# They also have to be executed from the project's root folder
pushd ../.. > /dev/null
flutter test --reporter expanded integration_test

# Cleanup and return to initial directory
rm -rf integration_test
popd > /dev/null