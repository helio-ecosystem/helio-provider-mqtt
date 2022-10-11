# helio-provider-mqtt

This provider allows to recover data from a MQTT device


### MqttProvider

````json


{
    "source" : "https://github.com/helio-ecosystem/helio-providers-mqtt/releases/download/v0.1.1/helio-providers-mqtt-0.1.1.jar",
    "clazz": "helio.providers.MqttProvider",
    "type": "PROVIDER"
  }
````

The configuration information that can be passed to this provider are the following fields:

 * `url` the MQTT device URL.
 * `id` the id to be identified by the MQTT device. By default, is a random string.
 * `include_topic` True if the response is a JSON with the topic. By default is set in false.
 * `username` The username to be identified by the MQTT device.
 * `password` The password to be identified by the MQTT device.
 * `registerTopic` A JSON Array that contains the following:
 ** `name` The name of the action to be register.
 ** `topic` The topic to be susbribe.

