
@echo off
:: BatchGotAdmin (Run as Admin code starts)
REM --> Check for permissions
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
REM --> If error flag set, we do not have admin.
if '%errorlevel%' NEQ '0' (
echo Requesting administrative privileges...
goto UACPrompt
) else ( goto gotAdmin )
:UACPrompt
echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
echo UAC.ShellExecute "%~s0", "", "", "runas", 1 >> "%temp%\getadmin.vbs"
"%temp%\getadmin.vbs"
exit /B
:gotAdmin
if exist "%temp%\getadmin.vbs" ( del "%temp%\getadmin.vbs" )
pushd "%CD%"
CD /D "%~dp0"


for /f "delims=" %%J in ('where java.exe') do set "JAVA_LOCATION=%%J"



start nssm install BPM_AGENT %JAVA_LOCATION% -jar """C:\BPM AGENT\BPM_AGENT.jar"""
nssm set BPM_AGENT AppThrottle 0
nssm set BPM_AGENT AppStdout C:\BPM AGENT\service_install\output.log
nssm set BPM_AGENT AppStderr C:\BPM AGENT\service_install\output.log
nssm start BPM_AGENT
