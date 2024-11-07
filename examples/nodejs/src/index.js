import axios from 'axios';
import fs from 'fs';
import FormData from 'form-data';

const filePath = './xml/SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1 - full.xml';

const data = new FormData();
data.append('file', fs.createReadStream(filePath));
data.append('fileName', 'SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1 - full.xml');

const requestConfig = {
  method: 'post',
  maxBodyLength: Infinity,
  url: 'http://localhost:8080/eiXml/SGr_00_0016_dddd_Siemens_PAC2200_ModbusTCP_V0.1 - full.xml',
  headers: {
    ...data.getHeaders()
  },
  data: data
};

axios.request(requestConfig).then((response) => {
  console.log("Response: ", response.data);
}).catch((error) => {
  console.error("Error: ", error);
});