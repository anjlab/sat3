ECHO OFF

SETLOCAL ENABLEDELAYEDEXPANSION

SET CP=
FOR %%j IN (lib\*.jar) DO IF DEFINED CP (SET CP=!CP!;%%j) ELSE (SET CP=%%j)

ECHO *******************************************************
ECHO * Java classpath: %CP%

SET SOLVER_MAIN_CLASS=com.anjlab.sat3.Program

ECHO *******************************************************
ECHO * Running program

java -cp %CP% %SOLVER_MAIN_CLASS% %*

PAUSE
