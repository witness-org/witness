@echo off
REM Tests are only run on device (e.g. emulator) if they are contained in the "integration_test" folder
robocopy . ..\..\integration_test\integration /s /e /NFL /NDL /NJH /NJS /nc /ns /np
robocopy ..\common ..\..\integration_test\common /s /e /NFL /NDL /NJH /NJS /nc /ns /np

REM They also have to be executed from the project's root folder
cd ..\..
call flutter test --reporter expanded integration_test

REM Cleanup and return to initial directory
rmdir /s /q integration_test
cd test\integration