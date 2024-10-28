# SmartGridready (SGr) Intermediary - Dynamic Tariff Retrieval

This project demonstrates how to use the SGr Intermediary to retrieve dynamic tariff data using a Python script. The process involves setting up a Docker container for the intermediary service, downloading an XML file, and configuring a device to fetch the dynamic tariffs.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed.
- [Python 3.x](https://www.python.org/downloads/) installed.
- Required Python packages listed in `requirements.txt` (if applicable).

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

### Step 2: Clone the Repository and Run the Python Script

1. Clone this repository:
   ```bash
   git clone https://github.com/SmartGridready/SGrJavaIntermediary.git
   cd SGrJavaIntermediary/examples/python
   ```

2. **Run the Python script** to retrieve the dynamic tariffs:
   ```bash
   python sgr-intermediary-python-example.py
   ```

   The script performs the following steps:
   - Downloads an XML file from a GitHub repository describing the dynamic tariff.
   - Posts the XML data to the intermediary.
   - Configures a device with the tariff information.
   - Requests tariff data and displays it.

## Code Overview

- **XML file and device configuration**: Configured within the Python script, specifying parameters such as device name and time intervals.
- **Endpoints**: The script utilizes endpoints like `/eiXml` for loading XML data and `/value` to request tariff data.

## Troubleshooting

- **Port Conflicts**: Ensure port 8080 is free or modify the Docker command to use an alternate port.
- **Docker Permissions**: Make sure Docker has the necessary permissions to pull and run containers.