package actions.routes;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

import actions.utils.Utils;

public class Route {

	protected String name;
	protected Action action;
	protected Set<String> inputs;
	protected  Set<String> outputs;

	public Route(){
		super();
	}
	public Route(String name) {
		super();
		this.name = name;
		this.inputs = new HashSet<>();
		this.outputs = new HashSet<>();
	}
	public Route(String name,  Action action) {
		super();
		this.name = name;
		this.action = action;
		this.inputs = new HashSet<>();
		this.outputs = new HashSet<>();
	}

	public Route(String name, Action action, Set<String> inputs, Set<String> outputs) {
		super();
		this.name = name;
		this.action = action;
		this.inputs = inputs;
		this.outputs = outputs;
	}
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Set<String> getInputs() {
		return inputs;
	}

	public void setInputs(Set<String> inputs) {
		this.inputs = inputs;
	}

	public Set<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(Set<String> outputs) {
		this.outputs = outputs;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		try {
			if(inputs==null)
				inputs = new HashSet<>();
			if(outputs==null)
				outputs = new HashSet<>();
			return Utils.JACKSON_MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toJson() {
		try {
			return Utils.JACKSON_MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public int hashCode() {
		return Objects.hash(action, inputs, name, outputs);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		return Objects.equals(action, other.action) && Objects.equals(inputs, other.inputs)
				&& Objects.equals(name, other.name) && Objects.equals(outputs, other.outputs);
	}




	
}
