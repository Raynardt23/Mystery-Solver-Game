@echo off
REM Set the path to your compiled classes
set BIN_DIR=bin

REM Set the path to your MySQL connector JAR
set LIB_DIR=lib\mysql-connector-j-9.4.0.jar

REM Run the game
java -cp "%BIN_DIR%;%LIB_DIR%" com.OblivionNexusDetective.main.Main

pause