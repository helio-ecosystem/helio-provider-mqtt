package actions.routes;



@FunctionalInterface
public interface Action {

	Object handle(Object data, String topic);

}
