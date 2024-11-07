import axios from 'axios';
import fs from 'fs';
import FormData from 'form-data';

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

axios.request(addXmlConfig).then((response) => {
  console.log("Add XML Response status: ", response.status);
}).catch((error) => {
  console.error("Error occurred while adding an XML: ", error);
});


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
      "val": "192.168.100.144"
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

axios.request(addDeviceConfig).then((response) => {
  console.log("Response: ", response.data);
}).catch((error) => {
  console.error("Error occurred when adding a device: ", error);
});

