package helio.providers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import actions.Helio;
import actions.mqtt.MqttConfiguration;
import actions.routes.Action;
import actions.routes.Routes;
import actions.utils.Tokens;
import helio.blueprints.AsyncDataProvider;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

public class MqttProvider implements AsyncDataProvider{

	private static FlowableEmitter<@NonNull String> temporalEmitter;
	private static boolean include_topic = false;
	private static final Gson GSON = new Gson();
	private String url;
	private String id = UUID.randomUUID().toString();

	@Override
	public void configure(JsonObject configuration) {
		// TODO Auto-generated method stub
		if(!configuration.has(Tokens.URL) || !configuration.has(Tokens.REGISTERTOPIC)) {
			throw new IllegalArgumentException("The MqttProvider needs to receive non empty value for 'url' or 'registerTopic'");
		}
		url = configuration.get(Tokens.URL).getAsString();
		if(!configuration.get(Tokens.REGISTERTOPIC).isJsonArray()) {
			throw new IllegalArgumentException("MqttProvider - 'registerTopic' must be an array with 'name' and 'topic'");
		}
		if(configuration.has(Tokens.ID)) {
			id = configuration.get(Tokens.ID).getAsString();
		}
		if(configuration.has(Tokens.USERNAME) && configuration.has(Tokens.PASSWORD)) {
			Helio.configure(MqttConfiguration.createDefault(url, id, 
					configuration.get(Tokens.USERNAME).getAsString(),configuration.get(Tokens.PASSWORD).getAsString()));
		}else {
			Helio.configure(MqttConfiguration.createDefault(url, id));
		}
		if(configuration.has(Tokens.INCLUDETOPIC)){
			include_topic = configuration.get(Tokens.INCLUDETOPIC).getAsBoolean();
		}
		JsonArray topic = configuration.get(Tokens.REGISTERTOPIC).getAsJsonArray();
		for(int i = 0; i<topic.size();i++) {
			Helio.register(topic.get(i).getAsJsonObject().get(Tokens.TOPICNAME).getAsString(), MqttProvider.messages, 
					topic.get(i).getAsJsonObject().get(Tokens.TOPIC).getAsString());
		}
		
//		//Flag: include_topic
//		Helio.configure(MqttConfiguration.createDefault("tcp://mqtt.alentejo.auroral.eu:1883", "upmclient", "upm", "auroral#upm"));
////		Helio.register("Imprimir", MqttProvider.messages, topic);
//
//		Helio.register("Imprimir", MqttProvider.messages, "shellies/shellymotionsensor-60A423BEC274/status");
//		Helio.register("all", MqttProvider.messages, "shellies/");
//		Helio.register("sensor", MqttProvider.messages, "shellies/shellymotionsensor-60A423BEC274/sensor");

	}

	@Override
	public void subscribe(@NonNull FlowableEmitter<@NonNull String> emitter) throws Throwable {
		temporalEmitter = emitter;
		//		emitter.onError(null); //Indicate error (i.e. is not a JSON or CSV or whatever)

	}

	public static boolean isValid(String json) {
		try {
			JsonParser.parseString(json);
		} catch (JsonSyntaxException e) {
			return false;
		}
		return true;
	}

	/**
	 * Pass the data to subscribe
	 * Create a JSON Array encapsulating the topic and data (optional)
	 */
	public static final Action messages = (Object data, String topic) -> {
		if(temporalEmitter!=null) {
			if(include_topic) {
				JsonObject jsonData = new JsonObject();
				jsonData.addProperty(Tokens.TOPIC, topic);
				if(isValid(data.toString())) {
					JsonElement aaa = JsonParser.parseString(data.toString());
					if(aaa instanceof JsonObject) {
						jsonData.add(Tokens.DATA, JsonParser.parseString(data.toString()).getAsJsonObject());
					}else {
						jsonData.add(Tokens.DATA, JsonParser.parseString(data.toString()).getAsJsonArray());
					}
				}else {
					jsonData.addProperty(Tokens.DATA, data.toString());
				}
				temporalEmitter.onNext(jsonData.toString());

			}else {
				temporalEmitter.onNext(data.toString());
			}
		}
		return null;
	};

//	public static void main(String[] args) throws Throwable {
//		MqttProvider t = new MqttProvider();
//		t.configure(null);		
//		String blocked = Flowable.create(t, BackpressureStrategy.BUFFER).blockingFirst();
//		System.out.println(blocked);
//	}

}
