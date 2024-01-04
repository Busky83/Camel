import paho.mqtt.client as mqtt
import uuid
import json
import random
import time


test_data = [
    {
        "uuid": "A",
        "temperature": 10,
    },
    {
        "uuid": "B",
        "temperature": 20,
    },
    {
        "uuid": "C",
        "temperature": 30,
    },
    {
        "uuid": "D",
        "temperature": 40,
    },
]

def generate_message(uuid, temperature):
    return json.dumps({
        # "uuid": str(uuid.uuid4()),
        # "temperature": random.uniform(20.0, 30.0),
        "uuid": uuid,
        "temperature": temperature,
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S")
    })

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

client = mqtt.Client()
client.on_connect = on_connect
# client.connect("MQTT_SERVER_ADDRESS", MQTT_PORT, 60)
client.connect("mosquitto", 1883, 60)
while not client.is_connected():
    print("Waiting for connection...")
    time.sleep(1)
    client.loop()

# time.sleep(10)
for i in range(4):
    message = generate_message(test_data[i]['uuid'], test_data[i]['temperature'])
    client.publish("Try/MQTT", message)
    time.sleep(3)
