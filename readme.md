# SGrJavaIntermediary

## Introduction

The SGrJavaIntermediary application allows access to SmartGridready compliant devices through a
WEB-Service API. Developers of SGr communicator applications such as energy-manager applications
can use the SGrJavaIntermediary instead of integrating a SmartGridready commm-handler library written 
in Java or Python.

The SGrJavaIntermediary acts as a bridge between the communicator application and the SmartGridready
compliant devices.

 ![Architecture-Overview.png](doc/Architecture-Overview.png)

This solution is particularly useful for applications written in programming languages that do not have an available 
SmartGridready commhandler library.

The SGRIntermediary allows a standardized access to arbitrary SGr devices, described by EI-XML files. 
The EI-XML describes details needed to communicate with the device's specific interface.

Adding a new device follows in two steps:
1. Add the device specific EI-XML if it does not already exist.
2. Add the device itself with an arbitrary device name, a reference to the EI-XML and the device specific 
configuration.

### Examples:

Adding an EI-XML for a Wago Smartmeter: 

HTTP POST:  http://localhost:8080/eiXml/SGr_04_0014_0000_WAGO_SmartMeterV0.2.1.xml
```json

{
  "file": "(fileContent..)",
  "fileName": "SGr_04_0014_0000_WAGO_SmartMeterV0.2.1.xml"
}
```
Adding a first Wago device:

HTTP POST: http://localhost:8080/device
```json
{
  {
    "name": "WAGO-Smartmeter-1",
    "eiXmlName" : "SGr_04_0014_0000_WAGO_SmartMeterV0.2.1.xml",
    "configurationValues" : [
        {
            "name" : "comPort",
            "val"  : "COM3"
        },
        {
            "name":"baudRate",
            "val":19200
        },
        {
            "slaveAddr": 1
        }
    ]
  }
}
```

Adding a second Wago device:

HTTP POST: http://localhost:8080/device
```json
{
  {
    "name": "WAGO-Smartmeter-2",
    "eiXmlName" : "SGr_04_0014_0000_WAGO_SmartMeterV0.2.1.xml",
    "configurationValues" : [
        {
            "name" : "comPort",
            "val"  : "COM3"
        },
        {
            "name":"baudRate",
            "val":19200
        },
        {
            "slaveAddr": 2
        }
    ]
  }
}
```

Reading a voltage from WAGO-Smartmeter-1:
```
-- Request format:
{
   HTTP GET: http://localhost:8080/value/<deviceName>/<functionlProfileName>/<dataPointName>
}

-- Example:
{
  HTTP GET: http://localhost:8080/value/WAGI-Smartmeter-1/VoltageAC/VoltageL1
}
```

The API provides a management API for EI-XML files and devices. You can add, update and delete EI-XML and devices.
A documentation of the complete API is available as HTML open-api doc within the project sources. See <a href="https://github.com/SmartGridready/SGrJavaIntermediary/tree/master/openapi/index.html" target="_blank">OpenAPI doc</a>

If you have a running SGrIntermediary Docker container or running the intermediary on your local machine you can open 
the Swagger doc: [Swagger doc](http://localhost:8080/swagger-ui.html)


## Installation

Currently proposed installation variant is to use the SGrIntermediary docker image.

- Install docker e.g. Docker desktop on your machine. 
  - see [Docker desktop Windows](https://docs.docker.com/desktop/install/windows-install/)
  - see [Docker desktop Mac](https://docs.docker.com/desktop/install/mac-install/)
  - see [Docker desktop Linux](https://docs.docker.com/desktop/install/linux/)


- Go to the shell/cmd terminal and pull the smartgridready/sgr-intermediary docker image from the github registry:
  - `docker pull ghcr.io/smartgridready/sgr-intermediary:master`


- Then run the docker image:
  - `docker run -d -p 8080:8080 --name sgr-intermediary ghcr.io/smartgridready/sgr-intermediary:master`

- Check if the 'sgr-intermediary' container is running:
  - `docker container ls -f name=sgr-intermediary`







 
