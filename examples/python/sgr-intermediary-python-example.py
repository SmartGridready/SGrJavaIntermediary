# -----------------------------------------------------------------------------
# Script Name:    sgr-intermediary-python-example.py
# Author:         Gino Agbomemewa
# Date Created:   October 2024
# Description:    Example to use SGr Intermediary to retrive dynamic tariffs
# -----------------------------------------------------------------------------

# Version:        1.0
# Python Version: 3.x
# License:        refer to https://github.com/SmartGridready 

# -----------------------------------------------------------------------------
# Change Log:
# Date        Author              Version     Description
# ----------  ------------------  ---------   ---------------------------------
# 2024-10-xx  Gino Agbomemewa     1.0         Initial creation.
# -----------------------------------------------------------------------------

import requests
import sys

def create_url(*args):
    # Join the strings with '/' and remove any duplicate slashes
    return '/'.join(arg.strip('/') for arg in args)

################################################################################
## Configuration
################################################################################

# docker container RestAPI address
sgr_intermediary_url = "http://localhost:8080"
# GitHub URL where the XML files are hosted
github_url = "https://github.com/SmartGridready/SGrSpecifications/blob/master/XMLInstances/ExtInterfaces"

# Tariff Configuration parameters
xml_file_name = "SGr_05_mmmm_dddd_Dynamic_Tariffs_Swisspower_V0.0.1.xml"  # Name of XML File describing the dynamic tariff
device_name = "Swisspower-Tariff"  # Name of the device which will be created in the SGr intermediary
device_configuration_parameters = [  # Parameters necessary to initialize the device
        {
            "name": "metering_code",
            "val": "CH1018601234500000000000000011642"
        },
        {
            "name": "token",
            "val": "19d6ca0bb9bf4d8b6525440eead80da6"        
        }
    ]

# To receive dynamic tariff informations from a device (= Rest API server of a 
# Tariff supplier) i need to define which datapoint of the DynamicTariff 
# functional profile i want to read
sgr_functional_profile = "DynamicTariff"
sgr_datapoint = "TariffSupply?start_timestamp=2024-10-01T00:00:00%2B02:00&\
end_timestamp=2024-10-02T01:00:00%2B02:00"

# Create some userful url to GET/Post data with the SGr Intermediary
url_to_load_xml_in_the_sgr_intermediary = create_url(*[
    sgr_intermediary_url, "eiXml", xml_file_name
])

url_to_initialize_device = create_url(*[
    sgr_intermediary_url, "device"
])

# http://localhost:8080/value/Swisspower-Tariff/DynamicTariff/TariffSupply?\
# start_timestamp=2024-01-01T00:00:00+02:00&end_timestamp=\
# 2024-01-01T01:00:00+02:00
url_to_request_tariff_data = create_url(*[
    sgr_intermediary_url, "value", device_name, sgr_functional_profile, 
    sgr_datapoint])

################################################################################
## Execution
################################################################################

print("# Step 1: Download the XML file from GitHub (alternatively a local saved XML File can be used)")
github_xml_file_url = create_url(*[github_url, xml_file_name])
github_response = requests.get(github_xml_file_url)

# Check if the request was successful
if github_response.status_code == 200:
    print("Downloaded XML from GitHub successfully!")

    print("# Step 2: make the XML available to the SGr Intermediary by loading it")
    files = {'file': github_response.content}
    data = {'fileName': xml_file_name}
    post_response = requests.post(url_to_load_xml_in_the_sgr_intermediary, files=files, data=data)
else:
    print("Response status code {}".format(github_response.status_code))
    sys.exit()

# Check if the POST request was successful
if post_response.status_code == 200:
    print("XML file posted successfully!")
else:
    print(f"Failed to post the XML file. Status code: {post_response.status_code}")

print("# Step 3: establish a connection to the dynamic tariff server (the server is called a 'device')")

# configuration paramters for device initialisation
device_payload = {
    "name": device_name,
    "eiXmlName": xml_file_name,
    "configurationValues": device_configuration_parameters
}

# Perform the POST request to add the device
device_response = requests.post(url_to_initialize_device, json=device_payload)

# Check if the POST request for the device was successful
if device_response.status_code == 200:
    print("Device added successfully!")
    print(device_response.text)
else:
    print(f"Failed to add the device. Status code: {device_response.status_code}")

print("# Step 4: Request tariff data")

tariff_response = requests.get(url_to_request_tariff_data)

# Check if the GET request was successful
if tariff_response.status_code == 200:
    print("GET request successful!")
    print("Response content:")
    print(tariff_response.text)  # Print the response content
else:
    print(f"Failed to retrieve the tariff data. Status code: {tariff_response.status_code}")