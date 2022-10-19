package helio.providers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import helio.blueprints.DataHandler;

/**
* This class handles the call backs from the queue topic where the {@link MqttProvider} has been subscribed. It provides a JSON containing the topics and the values retrieved
* @author Andrea Cimmino
*/
public class SubscriberCallback implements MqttCallback {
	private static final Gson GSON = new Gson();
	protected static List<JsonObject> queue = Lists.newCopyOnWriteArrayList();
	private Set<String> topics = new HashSet<>();
	public static Logger logger = LoggerFactory.getLogger(DataHandler.class);
	private boolean includeTopics = true;
	private boolean castJson = false;
	
	

	/**
	 * Constructor for the class {@link SubscriberCallback} 
	 */
	public SubscriberCallback() {
	
	}
	
	/**
	 * This method provides log information in case of losing the MQTT connection 
	 */
	@Override
	public void connectionLost(Throwable cause) {
		logger.warn(cause.toString());
		
	}

	/**
	 * This method updates the JSON object that contains the values retrieved from the topics in the queue
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if(topics.contains(topic)) {
			JsonObject data = new JsonObject();
			data.addProperty("topic", topic);
			// TODO: if castJson is true  message.toString() should be transformed into JSON using GSON
			data.addProperty("data", message.toString());
			queue.add(data);
			
		}
	}

	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// empty
	}

	// Getters & Setters
	
	public Set<String> getTopics() {
		return topics;
	}

	public void setTopics(Set<String> topics) {
		this.topics = topics;
	}

	public boolean isCastJson() {
		return castJson;
	}

	public void setCastJson(boolean castJson) {
		this.castJson = castJson;
	}

	public boolean isIncludeTopics() {
		return includeTopics;
	}

	public void setIncludeTopics(boolean includeTopics) {
		this.includeTopics = includeTopics;
	}
	

}