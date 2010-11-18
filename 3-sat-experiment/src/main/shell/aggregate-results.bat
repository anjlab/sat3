ECHO OFF

SETLOCAL ENABLEDELAYEDEXPANSION

SET RESULTS_FOLDER=%1

IF NOT EXIST %RESULTS_FOLDER% THEN GOTO invalid_results_folder

SET CP=
FOR %%j IN (lib\*.jar) DO IF DEFINED CP (SET CP=!CP!;%%j) ELSE (SET CP=%%j)

ECHO *******************************************************
ECHO * Java classpath: %CP%

ECHO *******************************************************
ECHO * Aggregating results

java -cp %CP% com.anjlab.sat3.ResultsAggregator %RESULTS_FOLDER% 

GOTO exit

:invalid_results_folder

ECHO *******************************************************
ECHO * Invalid results folder "%RESULTS_FOLDER%". Please pass valid folder name containing *-results.txt files as argument to this batch.

:exit

PAUSE
