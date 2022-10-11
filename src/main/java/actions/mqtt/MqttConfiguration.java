package actions.mqtt;

import java.util.UUID;


public class MqttConfiguration  {


	private String id;

	private String host;

	private boolean autoReconnect = true;

	private boolean cleanSession = true;

	private Integer connectionTimeout = 10;

	private String username;
	
	private String password;
	
	public MqttConfiguration(String host){
		this.host = host;
		this.id = UUID.randomUUID().toString();
	}

	public MqttConfiguration(String host, String id){
		this.host = host;
		this.id = id;
	}
	
	public MqttConfiguration(String host, String id, String username, String password){
		this.host = host;
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public boolean getAutoReconnect() {
		return autoReconnect;
	}

	public void setAutoReconnect(boolean autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	public boolean getCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static MqttConfiguration createDefault(String host, String id) {
		return new MqttConfiguration(host, id);
	}


	public static MqttConfiguration createDefault(String host, String id, String username, String password) {
		return new MqttConfiguration(host, id, username, password);
	}

}
