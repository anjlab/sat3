ECHO OFF

SETLOCAL ENABLEDELAYEDEXPANSION

SET CNF_FOLDER=%1

IF NOT EXIST %CNF_FOLDER% THEN GOTO invalid_cnf_folder

SET CP=
FOR %%j IN (lib\*.jar) DO IF DEFINED CP (SET CP=!CP!;%%j) ELSE (SET CP=%%j)

ECHO *******************************************************
ECHO * Java classpath: %CP%

SET SOLVER_MAIN_CLASS=com.anjlab.sat3.Program

ECHO *******************************************************
ECHO * Solving instances

FOR %%f IN (%CNF_FOLDER%\*.cnf) DO IF NOT EXIST %CNF_FOLDER%\%%f-results.txt (java -cp %CP% %SOLVER_MAIN_CLASS% %%f)

ECHO *******************************************************
ECHO * Aggregating results

java -cp %CP% com.anjlab.sat3.ResultsAggregator %CNF_FOLDER% 

GOTO exit

:invalid_cnf_folder

ECHO *******************************************************
ECHO * Invalid CNF folder "%CNF_FOLDER%". Please pass valid folder name containing *.cnf files as argument to this batch.

:exit

PAUSE
