# Network Cell Analyzer

## Introduction

The "Network Cell Analyzer" is an Android application designed to analyze cell-specific data received from the serving base station of the cellular network to which your Android device is connected. It provides insights into the history of network operators, cellular network types, and offers statistics on the experienced link quality and measurements in a distributed manner.

## Proposed System

The project aims to utilize smartphone APIs to collect network measurements in various generations of mobile networks. The Android application collects cell information from the actively connected base station, supporting GSM/GPRS/EDGE (2G/2.5G), UMTS (3G), and LTE (4G) networks. Through Android APIs, the device acquires cell-related information and regularly sends this data to a server.

## Key Features

- Collects cell information from the actively connected base station.
- Supports GSM/GPRS/EDGE (2G/2.5G), UMTS (3G), and LTE (4G) networks.
- Regularly sends cell information to the server with a timestamp.
- Server saves received data in a database and provides statistics upon user request.
- Android application displays real-time cell information and statistics based on user-defined time periods.
- Provides statistics such as average connectivity time per operator and network type, average signal power per network type, average signal power per device, and average SNR or SINR per network type.
- Server interface displays centralized statistics, including the number of connected mobile devices and IP/MAC addresses of connected devices.

