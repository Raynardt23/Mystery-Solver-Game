@echo off
set SRC=src
set BIN=bin
set LIB=lib/*

rem Make sure bin exists
if not exist %BIN% mkdir %BIN%

rem Compile all Java files recursively
for /R %SRC% %%f in (*.java) do (
    javac -d %BIN% -cp "%LIB%" "%%f"
)

echo Compilation finished.
pause