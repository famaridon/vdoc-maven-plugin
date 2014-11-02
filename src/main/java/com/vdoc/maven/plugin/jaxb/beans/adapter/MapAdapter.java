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

public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, String>> {

	@Override
	public Map<String, String> unmarshal(AdaptedMap adaptedMap) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		for (Entry entry : adaptedMap.entry) {
			map.put(entry.key, entry.value);
		}
		return map;
	}

	@Override
	public AdaptedMap marshal(Map<String, String> map) throws Exception {
		AdaptedMap adaptedMap = new AdaptedMap();
		for (Map.Entry<String, String> mapEntry : map.entrySet()) {
			Entry entry = new Entry();
			entry.key = mapEntry.getKey();
			entry.value = mapEntry.getValue();
			adaptedMap.entry.add(entry);
		}
		return adaptedMap;
	}

	public static class AdaptedMap {
		@XmlElement(name = "param")
		public List<Entry> entry = new ArrayList<Entry>();

	}

	public static class Entry {
		@XmlAttribute(name = "name")
		public String key;

		@XmlAttribute
		public String value;

	}

}
