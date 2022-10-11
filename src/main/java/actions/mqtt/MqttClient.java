package actions.mqtt;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import actions.routes.Route;
import actions.routes.Routes;
import actions.utils.Utils;

public class MqttClient implements MqttCallback {

	final static Logger logger = Logger.getLogger(MqttClient.class.getName());

	protected IMqttClient client;
	protected static String statusTopic;
	private static final String STATUS = "/status";
	private static final String WORKER_STATUS = "ecosystem/status";
	protected static String registrationTopic;
	private static final String REGISTER = "/register";
	protected static JsonObject statusPayload;

	public MqttClient(MqttConfiguration configuration) throws MqttException {
		configure(configuration);
	}

	public void configure(MqttConfiguration createDefault) throws MqttException {
		client = Mqtt.createConnectedClient(createDefault);
		String id = client.getClientId();
		statusTopic = Utils.concatenate(id, STATUS);
		registrationTopic = Utils.concatenate(id, REGISTER);
		client.subscribe("#"); // Incoming messages handling;
		client.setCallback(this);
		initPayload();
		publishStatus();
	}

	private static final String ID_TOKEN = "id";
	private static final String STATUS_TOKEN = "status_topic";
	private static final String REGISTRATION_TOKEN = "registration_topic";
	private static final String STATUS_REQUEST_TOKEN = "resquest_status_topic";
	private static final String ACTIONS_TOKEN = "actions";
	private static final String TIME_TOKEN = "time";

	private void initPayload() {
		statusPayload = new JsonObject();
		statusPayload.addProperty(ID_TOKEN, client.getClientId());
		statusPayload.addProperty(STATUS_TOKEN, statusTopic);
		statusPayload.addProperty(REGISTRATION_TOKEN, registrationTopic);
		statusPayload.addProperty(STATUS_REQUEST_TOKEN, WORKER_STATUS);
	}

	private static String statusPayload() {
		JsonObject status = statusPayload;
		status.add(ACTIONS_TOKEN, Utils.toJsonArray(Routes.toJson()));
		status.addProperty(TIME_TOKEN, (new Date()).toString());
		return status.toString();

	}

	public void publishStatus() {
		if (client != null) {
			try {
				client.publish(statusTopic, Mqtt.toMqttMessage(statusPayload()));
			} catch (MqttPersistenceException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//System.out.println("Message received on topic: " + topic);
		if (topic.equals(registrationTopic)) {
			registerNewAction(Mqtt.toString(message));
			publishStatus();
		}else if (topic.equals(WORKER_STATUS)) {
			publishStatus();
		} else {
			List<Route> routes = Routes.findByInput(topic);
			for (int index = 0; index < routes.size(); index++) {
				Route route = routes.get(index);
				Object actionResult = route.getAction().handle(Mqtt.toString(message),topic);
				if (actionResult != null) {
					MqttMessage outputMessage = Mqtt.toMqttMessage(actionResult.toString());
					route.getOutputs().parallelStream().forEach(outputTopic -> {
						try {
							System.out.println("\t\toutput at :" + outputTopic);
							client.publish(outputTopic, outputMessage);
						} catch (MqttPersistenceException e) {
							e.printStackTrace();
						} catch (MqttException e) {
							e.printStackTrace();
						}
					});
				}

			}
		}
	}

	private void registerNewAction(String payload) {
		try {
			Route route = Utils.GSON.fromJson(payload, Route.class);
			Routes.set(route.getName(), route.getInputs(), route.getOutputs());
		} catch (Exception e) {
			String error = e.toString();
			try {
				JsonArray routes = Utils.GSON.fromJson(payload, JsonArray.class);
				for (int index = 0; index < routes.size(); index++) {
					Route route = Utils.GSON.fromJson(routes.get(index), Route.class);
					Routes.set(route.getName(), route.getInputs(), route.getOutputs());
				}
			} catch (Exception e2) {
				logger.severe(error);
				logger.severe(e2.toString());
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}
}
