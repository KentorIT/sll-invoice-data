@REM ---------------------------------------------------------------------------------
@REM Generates c# WCF service contracts (interface), client proxies and wcf config 
@REM file for ehr:accesscontrol 1.0 WSDLs + XML Schemas, using .Net WCF tool svcuti.exe
@REM ---------------------------------------------------------------------------------
@REM Licensed to Sveriges Kommuner och Landsting under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. Sveriges Kommuner och Landsting licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM 
@REM http://www.apache.org/licenses/LICENSE-2.0
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ---------------------------------------------------------------------------------

CD ..
SET OUTFILE20=/out:wcf\generated-src\RIVTABP20\ItIntegrationMonitoringInteractions_v1.cs
SET OUTFILE21=/out:wcf\generated-src\RIVTABP21\ItIntegrationMonitoringInteractions_v1.cs
SET APPCONFIG20=/config:wcf\generated-src\RIVTABP20\app.config
SET APPCONFIG21=/config:wcf\generated-src\RIVTABP21\app.config
SET NAMESPACE=/namespace:*,Riv.ItIntegration.Monitoring.Schemas.v1
SET SCHEMADIR="schemas\interactions"
SET SVCUTIL="svcutil.exe"

SET W1=%SCHEMADIR%\PingForConfigurationInteraction\PingForConfigurationInteraction_1.0_RIVTABP20.wsdl
SET W2=%SCHEMADIR%\PingForConfigurationInteraction\PingForConfigurationInteraction_1.0_RIVTABP21.wsdl
SET X1=schemas\core_components\*.xsd
SET X2=%SCHEMADIR%\PingForConfigurationInteraction\*.xsd

SET SCHEMAS20=%W1% %X1% %X2%
SET SCHEMAS21=%W2% %X1% %X2%

ECHO Kör SVCUTIL för PingForConfigurationInteraction_1.0_RIVTABP20.wsdl
%SVCUTIL% /language:cs /wrapped %OUTFILE20% %APPCONFIG20% %NAMESPACE% %SCHEMAS20%

ECHO Kör SVCUTIL för PingForConfigurationInteraction_1.0_RIVTABP21.wsdl
%SVCUTIL% /language:cs /wrapped %OUTFILE21% %APPCONFIG21% %NAMESPACE% %SCHEMAS21%

CD wcf

ECHO I DotNetprojektet ska du lagga till en referens till System.ServiceModel för 21-interfacet (eller 20-interfacet om du har det sedan tidigare och inte vill uppgradera)