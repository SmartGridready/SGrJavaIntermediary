# -----------------------------------------------------------------------------
# Script Name:    sgr-intermediary-python-example.py
# Author:         Gino Agbomemewa
# Date Created:   October 2024
# Description:    Example to use SGr Intermediary to retrive dynamic tariffs
# -----------------------------------------------------------------------------

# Version:        1.1
# Python Version: 3.x
# License:        refer to https://github.com/SmartGridready

# -----------------------------------------------------------------------------
# Change Log:
# Date        Author              Version     Description
# ----------  ------------------  ---------   ---------------------------------
# 2024-10-xx  Gino Agbomemewa     1.0         Initial creation.
# 2025-09-xx  Matthias Krebs      1.1         Correction, improved doc.
# -----------------------------------------------------------------------------

import requests


def create_url(*args):
    # Join the strings with '/' and remove any duplicate slashes
    return '/'.join(arg.strip('/') for arg in args)

################################################################################
# Configuration
################################################################################

# Docker container RestAPI address
sgr_intermediary_url = "http://localhost:8080"

# Tariff configuration parameters
selection = input('Do you want to test Groupe-E (1) or ESIT/Swisspower (2) Tariff?[1] ')
if selection == '2':
    # ESIT / Swisspower Tariff
    xml_file_name = "SGr_05_mmmm_dddd_Dynamic_Tariffs_Swisspower_V1.0.xml"  # Name of EID XML file describing the dynamic tariff
    device_name = "Swisspower-Tariff"  # Name of the device which will be created in the SGr intermediary
    device_configuration_parameters = [  # Parameters necessary to initialize the device
            {
                "name": "metering_code",
                "val": "CH1018601234500000000000000011642"
            },
            {
                "name": "token",
                "val": "19d6ca0bb9bf4d8b6525440eead80da6"
            },
            {
                "name": "tariff_type",
                "val": "electricity"
            }
        ]
elif selection in ['1', '']:
    # Groupe-E Tariff
    xml_file_name = "SGr_05_mmmm_dddd_Dynamic_Tariffs_GroupeE_V1.0.xml"  # Name of EID XML file describing the dynamic tariff
    device_name = "GroupeE-Tariff"  # Name of the device which will be created in the SGr Intermediary
    device_configuration_parameters = [  # Parameters necessary to initialize the device
            {
                "name": "tariff_name",
                "val": "vario_grid"
            }
        ]

# To receive dynamic tariff informations from a device (= Rest API server of a
# Tariff supplier) I need to define which data point of the DynamicTariff
# functional profile I want to read
sgr_functional_profile = "DynamicTariff"
sgr_data_point = "TariffSupply"

# Create some useful URL to GET/POST data on the SGr Intermediary
# eiXml/sgr-library retrieves the EI-XML file from the official declaration library https://library.smartgridready.ch/
url_to_load_xml_in_the_sgr_intermediary = create_url(*[
    sgr_intermediary_url, "eiXml/sgr-library"
])

url_to_initialize_device = create_url(*[
    sgr_intermediary_url, "device"
])

# http://localhost:8080/value/Swisspower-Tariff/DynamicTariff/TariffSupply?\
# start_timestamp=2025-01-01T00%3A00%3A00%2B01%3A00&end_timestamp=\
# 2025-01-02T00%3A00%3A00%2B01%3A00
url_to_request_tariff_data = create_url(*[
    sgr_intermediary_url, "value", device_name, sgr_functional_profile,
    sgr_data_point])

query_to_request_tariff_data = {
    "start_timestamp": "2025-01-01T00:00:00+01:00",
    "end_timestamp": "2025-01-02T00:00:00+01:00"
}

################################################################################
# Execution
################################################################################

print("# Step 1: Make the EID XML available to the SGr Intermediary by loading it")
request_body = {
    "eiXmlName": xml_file_name
}
post_response = requests.post(url_to_load_xml_in_the_sgr_intermediary, data=request_body)

# Check if the POST request was successful
if post_response.status_code == 200:
    print("EID XML file loaded successfully!")
else:
    print(f"Failed to load the EID XML file. Status code: {post_response.status_code}")

print("# Step 2: Establish a connection to the dynamic tariff API server (the server is called a 'device')")

# Configuration parameters for device initialization
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

print("# Step 3: Request tariff data")

# Perform the GET request to retrieve tariff data
tariff_response = requests.get(url_to_request_tariff_data, params=query_to_request_tariff_data)

# Check if the GET request was successful
if tariff_response.status_code == 200:
    print("GET request successful!")
    print("Response content:")
    print(tariff_response.text)  # Print the response content
else:
    print(f"Failed to retrieve the tariff data. Status code: {tariff_response.status_code}")
