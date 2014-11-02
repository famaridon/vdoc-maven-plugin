/**
 *
 */
package com.vdoc.maven.plugin.jaxb.beans.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentAdapter extends XmlAdapter<EnvironmentAdapter.Environment, Map<String, String>> {

	@Override
	public Map<String, String> unmarshal(Environment adaptedMap) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		for (Entry entry : adaptedMap.entry) {
			map.put(entry.key, entry.value);
		}
		return map;
	}

	@Override
	public Environment marshal(Map<String, String> map) throws Exception {
		Environment adaptedMap = new Environment();
		for (Map.Entry<String, String> mapEntry : map.entrySet()) {
			Entry entry = new Entry();
			entry.key = mapEntry.getKey();
			entry.value = mapEntry.getValue();
			adaptedMap.entry.add(entry);
		}
		return adaptedMap;
	}

	public static class Environment {
		@XmlElement(name = "environment")
		public List<Entry> entry = new ArrayList<Entry>();

	}

	public static class Entry {
		@XmlAttribute(name = "scope")
		public String key;

		@XmlElement
		public String value;

	}

}
