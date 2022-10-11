package actions.mqtt;

import java.nio.charset.Charset;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.JsonObject;

import actions.utils.Utils;



public class Mqtt {

	public static Integer deafultQOS = 1;

	public static Integer getDefaultQOS() {
		return deafultQOS;
	}

	public static void setDefaultQOS(Integer newQOS) {
		deafultQOS = newQOS;
	}

	public static MqttMessage toMqttMessage(String payload, Integer qos) {
		MqttMessage message = new MqttMessage(payload.getBytes());
		message.setQos(qos);
		return message;
	}

	public static MqttMessage toMqttMessage(String payload) {
		MqttMessage message = new MqttMessage(payload.getBytes());
		message.setQos(deafultQOS);
		return message;
	}

	public static String toString(MqttMessage payload) {
		return new String(payload.getPayload(), Charset.defaultCharset());
	}

	public static JsonObject toJsonObject(MqttMessage payload) {
		return Utils.GSON.fromJson(toString(payload), JsonObject.class);
	}

	public static IMqttClient createConnectedClient(MqttConfiguration configuration) throws MqttException {
		IMqttClient client = new MqttClient(configuration.getHost(), configuration.getId());
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(configuration.getAutoReconnect());
		options.setCleanSession(configuration.getCleanSession());
		options.setConnectionTimeout(configuration.getConnectionTimeout());
		options.setUserName(configuration.getUsername());
		options.setPassword(configuration.getPassword().toCharArray());
		IMqttToken token = client.connectWithResult(options);
	    token.waitForCompletion();
		return client;
	}

}
