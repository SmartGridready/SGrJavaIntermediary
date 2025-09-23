# SmartGridready (SGr) Intermediary - Dynamic Tariff Retrieval using Node.js

This project demonstrates how to use the SGr Intermediary to retrieve dynamic tariff data using Node.js. The process involves setting up a Docker container for the intermediary service, downloading an EID XML file, and configuring a "device" to fetch the dynamic tariffs.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed.
- [Node.js](https://nodejs.org/en/download) installed.

## Setup

### Step 1: Start the Docker Container

1. **Pull the Docker image**:

   ```bash
   docker pull ghcr.io/smartgridready/sgr-intermediary:master
   ```

2. **Run the Docker container** on port 8080:

   ```bash
   docker run -d -p 8080:8080 --name sgr-intermediary ghcr.io/smartgridready/sgr-intermediary:master
   ```

   The SGr Intermediary service will be accessible at `http://localhost:8080`.

### Step 2: Clone the Repository and Run Node.js

1. Clone this repository:

   ```bash
   git clone https://github.com/SmartGridready/SGrJavaIntermediary.git
   cd SGrJavaIntermediary/examples/python
   ```

2. **Install dependencies and run Node.js** to retrieve the dynamic tariffs:

   ```bash
   npm i
   npm start
   ```

   The script performs the following steps:

   - Downloads an EID XML file from the declaration library, describing the dynamic tariff.
   - Creates a new **device** based on the EID and configuration parameters.
   - Requests tariff data from the device and displays it.

## Code Overview

- **EID XML file and device configuration**: Configured within the JS script, specifying parameters such as device name and time intervals.
- **Endpoints**: The script utilizes endpoints like `/eiXml` for loading EID XML files and `/value` to request tariff data.

## Troubleshooting

- **Port Conflicts**: Ensure port 8080 is free or modify the Docker command to use an alternate port.
- **Docker Permissions**: Make sure Docker has the necessary permissions to pull and run containers.
