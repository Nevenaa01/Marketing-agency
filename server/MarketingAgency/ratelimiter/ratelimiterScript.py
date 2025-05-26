import requests
import json
import time
import urllib3

client_cert = ('client.crt', 'client.key')

url = "https://localhost:8443/api/ad/testRateLimiting"
data = {
    "id": 1,
    "role": "BASIC"
}

json_data = json.dumps(data)

headers = {
    "Content-Type": "application/json"
}

def send_post_request(request_id):
    try:
        response = requests.post(url, data=json_data, headers=headers, cert=client_cert, verify=False)
        print(f"Request {request_id} \n - Status Code:", response.status_code)
        print(f" - Response Body:", response.text,"\n")
    except requests.exceptions.RequestException as e:
        print(f"Request {request_id} - Error:", e,"\n")

print('\nStarting rate limiting test...\n\n')
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

for i in range(15):
    print(f"Sending request {i + 1}...")
    send_post_request(i + 1)
    time.sleep(1)
