package actions.routes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import actions.utils.Utils;

public class Routes {

	final static Logger logger = Logger.getLogger(Routes.class.getName());
	private static Map<String, Route> routes = new HashMap<>();
	
	public static void register(String name, Action action) {
		Route route = new Route(name, action);
		if(route!=null) {
			route.setAction(action);
		}
		if(!routes.containsKey(name)) {
			routes.put(name, route);
		}else {
			routes.replace(name, route);
		}
		logger.info("Action registered: "+route.getName());
	}
	
	public static void register(String name, Action action, String input, String output) {
		Route route = new Route(name, action);
		if(route!=null) {
			route.setAction(action);
			route.getInputs().add(input); //check input null
			route.getOutputs().add(output); // check output null
		}
		if(!routes.containsKey(name)) {
			routes.put(name, route);
		}else {
			routes.replace(name, route);
		}
		logger.info("Action registered: "+route.getName());
	}

	public static void set(String name, Set<String> inputs, Set<String> outputs) {
		Route route = new Route(name);
		if(route!=null && routes.containsKey(name)) {
			route.setInputs(inputs);
			route.setOutputs(outputs);
			routes.replace(name, route);
			logger.info("Routes assigned to action "+route.getName()+", inputs: "+inputs+" outputs:"+outputs);
		}else {
			logger.fine("Specified name ("+name+") does not corresponds with an existing action");

		}
		System.out.println(routes);
	}

	public static List<Route> findByInput(String inputTopic) {
		return routes.entrySet().parallelStream()
						.map(Entry::getValue)
						.filter(route -> route.getInputs().contains(inputTopic))
						.collect(Collectors.toList());
	}
	
	
	public static String toJson() {
		try {
			return Utils.JACKSON_MAPPER.writeValueAsString(routes.values());
		} catch (JsonProcessingException e) {
			logger.severe(e.toString());
			e.printStackTrace();
			return "";
		}
	}
}
