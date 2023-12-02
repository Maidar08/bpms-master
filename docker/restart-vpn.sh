#!/bin/bash

set -euo pipefail

sudo screen -S openfortivpn -X quit || echo "No active VPN session found"
sudo pkill openfortivpn || echo "No running openfortivpn process found"
sudo screen -dmS openfortivpn openfortivpn
