package actions;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttException;

import actions.mqtt.MqttClient;
import actions.mqtt.MqttConfiguration;
import actions.routes.Action;
import actions.routes.Route;
import actions.routes.Routes;

public class Helio {

	private static MqttClient client;
	final static Logger logger = Logger.getLogger(Helio.class.getName());

	static {
		// Disable Hibernate logs, except for severe level
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
		Logger log = Logger.getLogger("org.hibernate");
	    log.setLevel(Level.OFF); 
	}
	
	private Helio() {
		super();
	}
	
	// Route methods

	public static void register(String name, Action action) {
		Routes.register(name, action);
		if(client!=null)
			client.publishStatus();
	}
	
	public static void register(String name, Action action, String input) {
		Routes.register(name, action, input, null);
		if(client!=null)
			client.publishStatus();
	}
	
	public static void register(String name, Action action, String input, String output) {
		Routes.register(name, action, input, output);
		if(client!=null)
			client.publishStatus();
	}

	// Mqtt methods

	public static void configure(MqttConfiguration createDefault) {
		try {
			// 1. Create client
			client = new MqttClient(createDefault);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	








}
