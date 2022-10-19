package helio.providers;

import java.util.Arrays;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import helio.blueprints.AsyncDataProvider;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableEmitter;

public class MqttProvider implements AsyncDataProvider {

	// -- Attributes

	public static Logger logger = LoggerFactory.getLogger(MqttProvider.class);
	// ---- MQTT objects
	private SubscriberCallback subscriber;
	private MqttClient client;
	// ---- Connection attributes
	private String brokerAddress; // configurable
	
	private String clientId; // Auto-generated

	// -- Constructor

	/**
	 * Constructor
	 */
	public MqttProvider() {
		super();
		UUID uuid = UUID.randomUUID();
		clientId = "HelioMqttProvider-" + uuid.toString();
		subscriber = new SubscriberCallback();
	}	

	// -- Configuration methods

	private static final String ADDRESS_TOKEN = "url";
	private static final String ID_TOKEN = "id";
	private static final String USERNAME_TOKEN = "username";
	private static final String PASSWORD_TOKEN = "password";
	private static final String TOPICS_TOKEN = "topics";
	private static final String TOPICS_INCLUDE_TOKEN = "include_topic";
	private static final String CAST_TOKEN = "cast_json";
	@Override
	public void configure(JsonObject configuration) {
		if (configuration.has(ADDRESS_TOKEN)) {
			this.brokerAddress = configuration.get(ADDRESS_TOKEN).getAsString();
			if (configuration.has(TOPICS_TOKEN)) {
				JsonArray topicsArray = configuration.get(TOPICS_TOKEN).getAsJsonArray();
				Gson gson = new Gson();
				this.subscriber.getTopics().addAll(Arrays.asList(gson.fromJson(topicsArray, String[].class)));
				if (configuration.has(ID_TOKEN))
					this.clientId = configuration.get(ID_TOKEN).getAsString();
				if (configuration.has(TOPICS_INCLUDE_TOKEN))
					this.subscriber.setIncludeTopics(configuration.get(TOPICS_INCLUDE_TOKEN).getAsBoolean());
				if (configuration.has(CAST_TOKEN))
					this.subscriber.setCastJson(configuration.get(CAST_TOKEN).getAsBoolean());
				
				String username = null;
				String password = null;
				if (configuration.has(USERNAME_TOKEN))
					username = configuration.get(USERNAME_TOKEN).getAsString();
				if (configuration.has(PASSWORD_TOKEN))
					password = configuration.get(PASSWORD_TOKEN).getAsString();
				if( (username==null && password!=null) || (username!=null && password==null))
					throw new IllegalArgumentException("Provide a JSON configration with both 'username' and 'password'");
				connect(username, password); // connect to the mqtt
			} else {
				throw new IllegalArgumentException("Provide a JSON configuration file with the key 'topics' which value is an array of strings specifying topics to subscribe");
			}
		} else {
			throw new IllegalArgumentException("Provide a JSON configuration file with the key 'url' which value is a string specifying an MQTT broker address");
		}

	}

	private void connect(String username, String password) {
		try {
			this.client = new MqttClient(brokerAddress, clientId);
			MqttConnectOptions opts = new MqttConnectOptions();
			opts.setAutomaticReconnect(true);
			opts.setCleanSession(true);
			if(username!=null)
				opts.setUserName(username);
			if(password!=null)
				opts.setPassword(password.toCharArray());
			
			client.setCallback(subscriber);
			client.connect(opts);
			this.subscriber.getTopics().forEach(topic -> {
				try {
					client.subscribe(topic);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			});
			logger.info("MQTT connected");
		} catch (MqttException e) {
			logger.error("No broker to connect was found");
			throw new IllegalArgumentException(e.toString());
		}
	}

	public void disconnect() {
		try {
			subscriber = null;
			client.close();
		} catch (MqttException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void subscribe(@NonNull FlowableEmitter<@NonNull String> emitter) throws Throwable {
		
			while (true) {
				try {
					while (!SubscriberCallback.queue.isEmpty()) {
						
						JsonObject data = SubscriberCallback.queue.remove(0);
						emitter.onNext(data.toString());
						logger.info("Emitting, queue: "+ SubscriberCallback.queue.size());
					}
				} catch (Exception e) {
					e.printStackTrace();
					emitter.onError(e);
				}
				// TODO: PUT THIS REFRESH AS A CONFIGURABLE PARAMETER
				Thread.sleep(500);
			}
		
	}
}
