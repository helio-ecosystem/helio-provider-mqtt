package helio.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class Test {

	public static void main(String[] args) {
		JsonArray j = new JsonArray();
		j.add("/test");
		j.add("shellies/shellyht-CA6DB4/sensor/temperature");
		j.add("shellies/shellyht-CA6DB4/sensor/humidity");
		j.add("shellies/shellyht-CA6DB4/sensor/battery");
		
		JsonObject config = new JsonObject();
		config.addProperty("url", "tcp://mqtt.alentejo.auroral.eu:1883");
		config.add("topics", j);
		config.addProperty("username", "upm");
		config.addProperty("password", "auroral#upm");
		config.addProperty("include_topic", true);
		MqttProvider prov = new MqttProvider();
		prov.configure(config);
		
		Flowable.create(prov, BackpressureStrategy.BUFFER).subscribe(elem -> System.out.println("*"+elem));
		//System.out.println(">"+blocked);
		
	}

}
