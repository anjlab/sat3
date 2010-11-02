SET M2_REPO=c:\dev\m2\r
SET CP=.;..\..\..\3-sat-core\target\classes;%M2_REPO%\colt\colt\1.2.0\colt-1.2.0.jar;%M2_REPO%\commons-cli\commons-cli\1.2\commons-cli-1.2.jar;%M2_REPO%\concurrent\concurrent\1.3.4\concurrent-1.3.4.jar;%M2_REPO%\org\slf4j\slf4j-api\1.6.1\slf4j-api-1.6.1.jar;%M2_REPO%\org\slf4j\slf4j-log4j12\1.6.1\slf4j-log4j12-1.6.1.jar;%M2_REPO%\log4j\log4j\1.2.12\log4j-1.2.12.jar

REM ..\..\..\..\..\tests\uf20

SET CNF_FOLDER=%1
SET MAIN_CLASS=com.anjlab.sat3.Program
IF NOT EXIST %CNF_FOLDER% THEN GOTO invalid_cnf_folder
FOR /f "tokens=*" %%a IN ('DIR /b %CNF_FOLDER%\*.cnf') DO IF NOT EXIST %CNF_FOLDER%\%%a-results.txt java -cp %CP% %MAIN_CLASS% %CNF_FOLDER%\%%a

GOTO exit

:invalid_cnf_folder

ECHO Invalid CNF folder "%CNF_FOLDER%". Please pass valid folder name containing *.cnf files as argument to this batch.

:exit

PAUSE