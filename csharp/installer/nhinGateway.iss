; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
ArchitecturesInstallIn64BitMode=x64 ia64
AppId={{995D337A-5620-4537-9704-4B19EC628A39}
AppName=Direct Project .NET Gateway
AppVerName=Direct Project .NET Gateway 1.0
AppPublisher=The Direct Project (nhindirect.org)
AppPublisherURL=http://nhindirect.org
AppSupportURL=http://nhindirect.org
AppUpdatesURL=http://nhindirect.org
DefaultDirName={pf}\Direct Project .NET Gateway
DefaultGroupName=Direct Project .NET Gateway
AllowNoIcons=yes
OutputDir=.
OutputBaseFilename=nhinDirect Gateway
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

;[Components]
;Name: core; Description: "Core Components";
;Name: configservice; Description: "Configuration Service";

[Files]
Source: "..\bin\debug\*.dll"; DestDir: "{app}"; Excludes: "xunit*.dll,*tests.dll"; Flags: ignoreversion;
Source: "..\bin\debug\Win32\smtpEventHandler.dll"; DestDir: "{app}"; Flags: ignoreversion; Check: IsX86;
Source: "..\bin\debug\x64\smtpEventHandler.dll"; DestDir: "{app}"; Flags: ignoreversion; Check: IsX64 or IsIA64;
Source: "..\bin\debug\*.config"; DestDir: "{app}"; Flags: ignoreversion;
Source: "..\bin\debug\*.exe"; DestDir: "{app}"; Flags: ignoreversion;
Source: "..\bin\debug\Certificates\*"; DestDir: "{app}\Certificates"; Flags: ignoreversion recursesubdirs;
Source: "..\bin\debug\ConfigConsoleSettings.xml"; DestDir: "{app}"; Flags: ignoreversion;

Source: "..\config\service\*.svc"; DestDir: "{app}\ConfigService"; Flags: ignoreversion;
Source: "..\config\service\*.aspx"; DestDir: "{app}\ConfigService"; Flags: ignoreversion;
Source: "..\config\service\*.config"; DestDir: "{app}\ConfigService"; Flags: ignoreversion;
Source: "..\config\service\bin\*.dll"; DestDir: "{app}\ConfigService\bin"; Flags: ignoreversion recursesubdirs;

Source: "..\gateway\install\*.vbs"; DestDir: "{app}"; Flags: ignoreversion;
Source: "..\gateway\install\*.bat"; DestDir: "{app}"; Excludes: "backup.bat,copybins.bat"; Flags: ignoreversion;

Source: "..\gateway\devInstall\install.bat"; DestDir: "{app}"; Flags: ignoreversion;
Source: "..\gateway\devInstall\*.xml"; DestDir: "{app}"; Flags: ignoreversion;
Source: "..\gateway\devInstall\setupdomains.txt"; DestDir: "{app}"; Flags: ignoreversion;
Source: "..\gateway\devInstall\simple.eml"; DestDir: "{app}\Samples"; Flags: ignoreversion;

Source: "..\external\microsoft\vcredist\vcredist_x86.exe"; DestDir: "{app}\Libraries"; DestName: "vcredist.exe"; Flags: ignoreversion recursesubdirs; Check: IsX86;
Source: "..\external\microsoft\vcredist\vcredist_x64.exe"; DestDir: "{app}\Libraries"; DestName: "vcredist.exe"; Flags: ignoreversion recursesubdirs; Check: IsX64 or IsIA64;

Source: "createdatabase.bat"; DestDir: "{app}\SQL"; Flags: ignoreversion;
Source: "..\config\store\Schema.sql"; DestDir: "{app}\SQL"; Flags: ignoreversion;
Source: "createuser.sql"; DestDir: "{app}\SQL"; Flags: ignoreversion;

Source: "createconfigsvc.bat"; DestDir: "{app}"; Flags: ignoreversion;
Source: "configconsole.bat"; DestDir: "{app}"; Flags: ignoreversion;

[Icons]
Name: "{group}\NHIN Config Console"; Filename: "{app}\ConfigConsole.exe"; WorkingDir: "{app}";
Name: "{group}\Test Database"; Filename: "http://localhost/ConfigService/TestService.aspx";
Name: "{group}\{cm:UninstallProgram,NHIN .NET Gateway}"; Filename: "{uninstallexe}";

[Run]
Filename: "{app}\Libraries\vcredist.exe"; Description: "Microsoft Visual C++ 2008 Redistributable Package"; Flags: postinstall runascurrentuser unchecked;
Filename: "{app}\SQL\createdatabase.bat"; Parameters: ".\sqlexpress NHINDConfig ""{app}\SQL\Schema.sql"" ""{app}\SQL\createuser.sql"""; Description: Install Database; Flags: postinstall runascurrentuser unchecked;
Filename: "{app}\createconfigsvc.bat"; Parameters: """{app}\ConfigService"""; Description: Create Config Service; Flags: postinstall runascurrentuser unchecked;
Filename: "{app}\install.bat"; Parameters: """{app}\DevAgentWithServiceConfig.xml"" nocopy"; Description: "Register NHIN Direct Gateway"; Flags: postinstall runascurrentuser unchecked;
Filename: "{app}\configConsole.bat"; WorkingDir: "{app}"; Parameters: "batch setupdomains.txt"; Description: "Install Certs in Configuration Service"; Flags: postinstall runascurrentuser unchecked;

[UninstallRun]
Filename: "{app}\unregisterGateway.bat"; Flags: shellexec runascurrentuser;

[Code]
function IsX64: Boolean;
begin
  Result := Is64BitInstallMode and (ProcessorArchitecture = paX64);
end;

function IsIA64: Boolean;
begin
  Result := Is64BitInstallMode and (ProcessorArchitecture = paIA64);
end;

function IsX86: Boolean;
begin
  Result := (ProcessorArchitecture = paX86);
end;

