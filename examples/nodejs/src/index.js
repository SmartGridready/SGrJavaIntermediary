import axios from 'axios';
import fs from 'fs';
import FormData from 'form-data';

async function fetchData(deviceName, functionProfile, dataPointName) {
  let config = {
    method: 'get',
    maxBodyLength: Infinity,
    url: `http://localhost:8080/value/${deviceName}/${functionProfile}/${dataPointName}`,
    headers: { }
  };

  try {
    const response = await axios.request(config);
    console.log("Reading device value: " + response.data + " Status: " + response.status);
    return response.data;
  }
  catch (e) {
    console.error("Error occurred when fetching data: ", e);
  }
}

async function addXmlFile() {
  const filePath = './xml/SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1.xml';

  const xmlFormData = new FormData();
  xmlFormData.append('file', fs.createReadStream(filePath));
  xmlFormData.append('fileName', 'SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1.xml');

  const addXmlConfig = {
    method: 'post',
    maxBodyLength: Infinity,
    url: 'http://localhost:8080/eiXml/SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1.xml',
    headers: {
      ...xmlFormData.getHeaders()
    },
    data: xmlFormData
  };

  try {
    const response = await axios.request(addXmlConfig);
    console.log("Adding XML : ", response.status);
  } catch (error) {
    console.error("Error occurred while adding an XML: ", error);
  }
}

async function addDevice() {
  let deviceData = JSON.stringify({
    "name": "Siemens-PAC2200-1",
    "eiXmlName": "SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1.xml",
    "configurationValues": [
      {
        "name": "tcp_port",
        "val": 502
      },
      {
        "name": "tcp_address",
        "val": "192.168.141.126"
      },
      {
        "name": "slave_id",
        "val": "1"
      }
    ]
  });

  let addDeviceConfig = {
    method: 'post',
    maxBodyLength: Infinity,
    url: 'http://localhost:8080/device',
    headers: {
      'Content-Type': 'application/json'
    },
    data : deviceData
  };

  try {
    const response = await axios.request(addDeviceConfig);
    console.log("Adding device: ", response.data);
  } catch (error) {
    console.error("Error occurred when adding a device: ", error);
  }
}

await addXmlFile();
await addDevice();


const L1 = await fetchData("Siemens-PAC2200-1", "VoltageAC", "VoltageACL1_N")
const L2 = await fetchData("Siemens-PAC2200-1", "VoltageAC", "VoltageACL2_N")
const L3 = await fetchData("Siemens-PAC2200-1", "VoltageAC", "VoltageACL3_N")


console.log("VoltageACL1_N: ", L1);
console.log("VoltageACL2_N: ", L2);
console.log("VoltageACL3_N: ", L3);