import axios from 'axios';

const baseURL = 'http://localhost:8080';
const eidXmlName = 'SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1.xml';

async function fetchData(deviceName, functionalProfileName, dataPointName) {
  const config = {
    method: 'get',
    maxBodyLength: Infinity,
    url: `${baseURL}/value/${deviceName}/${functionalProfileName}/${dataPointName}`,
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
  const requestBody = JSON.stringify({
    "eiXmlName": eidXmlName
  })

  const addXmlConfig = {
    method: 'post',
    maxBodyLength: Infinity,
    url: `${baseURL}/eiXml/sgr-library`,
    headers: {
      'Content-Type': 'application/json'
    },
    data: requestBody
  };

  try {
    const response = await axios.request(addXmlConfig);
    console.log("Adding XML : ", response.status);
  } catch (error) {
    console.error("Error occurred while adding an XML: ", error);
  }
}

async function addDevice() {
  const requestBody = JSON.stringify({
    "name": "Siemens-PAC2200-1",
    "eiXmlName": eidXmlName,
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
        "val": 1
      }
    ]
  });

  const addDeviceConfig = {
    method: 'post',
    maxBodyLength: Infinity,
    url: `${baseURL}/device`,
    headers: {
      'Content-Type': 'application/json'
    },
    data : requestBody
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
