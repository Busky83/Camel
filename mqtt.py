import paho.mqtt.client as mqtt
import uuid
import json
import random
import time

def generate_message():
    return json.dumps({
        "uuid": str(uuid.uuid4()),
        "temperature": random.uniform(20.0, 30.0),
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S")
    })

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

client = mqtt.Client()
client.on_connect = on_connect

# client.connect("MQTT_SERVER_ADDRESS", MQTT_PORT, 60)
client.connect("mosquitto", 1883, 60)

while True:
    message = generate_message()
    client.publish("Try/MQTT", message)
    time.sleep(10)
