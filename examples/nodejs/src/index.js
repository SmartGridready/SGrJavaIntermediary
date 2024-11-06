import axios from 'axios';

const requestConfig = {
  method: 'POST',
  url: 'http://localhost:8080/eiXml/SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1 - full.xml',
  headers: {
    'Content-Type': 'application/json'
  },
  data: {
    file: '../xml/SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1 - full.xml',
    fileName: 'SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1 - full.xml'
  }
};

axios(requestConfig)
