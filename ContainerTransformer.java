package com.ONTRAPORT;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Contains multiToOne function which turns multi-dimension contain (map) into
 * one dimension (map) oneToMulti function which turns one dimension (map) to
 * multi-dimension main function that is used to test the function. Other
 * private helper functions Use map as container since generic container can
 * lead to problems but still may contain unchecked casting
 * 
 * @author megan.tsai
 *
 */
public class ContainerTransformer {

	/**
	 * Main creates a multi-dimension map, pass it through multiToOne, print
	 * result Then pass the result through oneToMulti then print result. While
	 * the example shows number only value. question did not specify. Use String
	 * to cover more basis.
	 */
	public static void main(String[] args) {

		// Creates a multi-dimension map for testing purpose
		Map<String, Object> testMap = new TreeMap<String, Object>();// root

		// a-one/2-1/0:Andrew, a-one/2-1/1:Brain, a-one/2-1/2:Charles,
		// a-one/2-1/3:David
		// a-one/2-2/:testMap2-2
		// b-two/3-1:testMap3-1
		// b-two/3-2:testMap3-2
		// b-two/3-3/4-1:testMap4-1
		// b-two/3-3/4-2:testMap4-2
		// b-two/3-3/4-3:testMap4-3
		// b-two/3-3/4-4:testMap4-4
		// c-four:level1-3
		// d-six: level1-4
		Map<String, Object> testMap2 = new TreeMap<String, Object>();
		List<String> data = Arrays.asList("Andrew", "Brain", "Charles", "David");
		testMap2.put("2-1", data);
		testMap2.put("2-2", "testMap2-2");
		Map<String, Object> testMap3 = new TreeMap<String, Object>();
		testMap3.put("3-1", "testMap3-1");
		testMap3.put("3-2", "testMap3-2");
		Map<String, Object> testMap4 = new TreeMap<String, Object>();
		testMap4.put("4-1", "testMap4-1");
		testMap4.put("4-2", "testMap4-2");
		testMap4.put("4-3", "testMap4-3");
		testMap4.put("4-4", "testMap4-4");

		testMap3.put("3-3", testMap4);
		testMap.put("a-one", testMap2);
		testMap.put("b-two", testMap3);
		testMap.put("c-four", "level1-3");
		testMap.put("d-fix", "level1-4");

		System.out.println("Print Original:");
		multiDimensionReader(testMap, null);

		System.out.println("");
		Map<String, String> oneDimension = multiToOne(testMap, null);

		System.out.println("Print multiToOne part:");
		for (Map.Entry<String, String> entry : oneDimension.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		System.out.println("");
		Map<String, Object> multiDimension = oneToMulti(oneDimension);

		System.out.println("Print onetoMulti part:");
		multiDimensionReader(multiDimension, null);
	}

	/**
	 * Convert a multi-dimension map to one dimension
	 * 
	 * @param original
	 *            multi-dimension map
	 * @param parentkey
	 *            parent key for recursive function to go keep track of parent
	 *            key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> multiToOne(Map<String, Object> original, String parentkey) {
		// Instantiate returning container
		Map<String, String> returnMap = new TreeMap<String, String>();
		// Sanity check to make sure original is not broken
		if (original != null) {
			// Iterate through map
			for (Map.Entry<String, Object> entry : original.entrySet()) {
				String nextKey = entry.getKey();
				String newKey = nextKey;
				// Prepend parentKey if parentKey exist
				if (parentkey != null) {
					newKey = parentkey + "/" + nextKey;
				}
				Object nextObject = entry.getValue();

				// If map, recurse through
				if (nextObject instanceof Map) {
					Map<String, Object> nextCollection = (Map<String, Object>) nextObject;
					returnMap.putAll(multiToOne(nextCollection, newKey));
				}
				// If string, add to map
				else if (nextObject instanceof String) {
					String nextValue = (String) nextObject;
					returnMap.put(newKey, nextValue);
				}
				// If list, iterate and add to map
				else if (nextObject instanceof List) {

					List<String> finalArray = (List<String>) nextObject;
					int count = 0;
					for (String nextValue : finalArray) {
						String finalKey = newKey + "/" + count;
						returnMap.put(finalKey, nextValue);
						count++;
					}
				}
				// Something bad happened. Should not get ehre
				else {
					System.err.println("Something horrible happened. Should not have anything but string, list, map");
				}
			}

		}
		return returnMap;
	}

	/**
	 * Convert single dimension map to multi-dimension Assumes when number is
	 * given as key, the data structure to create is list instead of map. Does
	 * not check to make sure that sequential index exist to fill list
	 * 
	 * @param original
	 *            one dimension map
	 * @return multi-dimension map
	 */
	public static Map<String, Object> oneToMulti(Map<String, String> original) {
		// Intialize returning map
		Map<String, Object> returnMap = new TreeMap<String, Object>();
		// Sanity check, in case given map is broken
		if (original != null) {
			// Iterate through and split key into list
			for (Map.Entry<String, String> entry : original.entrySet()) {
				String nextKey = entry.getKey();
				String nextValue = entry.getValue();
				String[] levels = nextKey.split("/");
				LinkedList<String> levelsQueue = new LinkedList<String>(Arrays.asList(levels));
				returnMap = levelCreator(returnMap, levelsQueue, nextValue);
			}
		}
		return returnMap;
	}

	/**
	 * Help creates the maps that is the container for each level
	 * 
	 * @param parentLevel
	 *            map that is the parent of the current level
	 * @param levelsQueue
	 *            list of levels to process inculding current level
	 * @param value
	 *            final value to put in map/list
	 * @return map of current level
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> levelCreator(Map<String, Object> parentLevel, LinkedList<String> levelsQueue,
			String value) {
		// Retrieve current level key
		String currentLevel = levelsQueue.get(0);
		levelsQueue.remove(0); // dequeue

		// If another level exist, go down to next level unless next level is a
		// number which indicate array
		if (levelsQueue.size() >= 1) {
			// If nextLevel is a number, process as list
			try {
				int nextLevel = Integer.parseInt(levelsQueue.get(0));
				List<String> currentLevelList = null;
				// If currentLevel exist, get the list
				if (parentLevel.containsKey(currentLevel)) {
					currentLevelList = (List<String>) parentLevel.get(currentLevel);
				}
				// currentLevel does not exist, create list
				else {
					currentLevelList = new LinkedList<String>();
				}
				currentLevelList.add(nextLevel, value);
				parentLevel.put(currentLevel, currentLevelList);
			}
			// Not a number, proceed as map
			catch (NumberFormatException e) {
				Map<String, Object> currentLevelMap = null;
				// If current level exist, get the map
				if (parentLevel.containsKey(currentLevel)) {
					currentLevelMap = (Map<String, Object>) parentLevel.get(currentLevel);
				}
				// currentLevel does not exist, create map
				else {
					currentLevelMap = new TreeMap<String, Object>();
				}
				parentLevel.put(currentLevel, levelCreator(currentLevelMap, levelsQueue, value));
			}
		}
		// Reached end level, add value to map
		else {
			parentLevel.put(currentLevel, value);

		}
		return parentLevel;
	}

	/**
	 * Helper function that reads multi-dimension map
	 * 
	 * @param multiDimension
	 *            map to read
	 * @param parentKey
	 *            parent key for helping recursive function print all level
	 */
	@SuppressWarnings("unchecked")
	private static void multiDimensionReader(Map<String, Object> multiDimension, String parentKey) {
		// Iterate through all keys
		for (Map.Entry<String, Object> entry : multiDimension.entrySet()) {
			Object value = entry.getValue();
			String key = entry.getKey();
			// Prepend key if parent key exist
			if (parentKey != null) {
				key = parentKey + "|" + key;
			}

			// If value is just string, print
			if (value instanceof String) {
				System.out.println(key + ": " + entry.getValue());
			}
			// If value is a list, iterate through and print
			else if (value instanceof List) {
				List<String> valueList = (List<String>) value;
				for (int i = 0; i < valueList.size(); i++) {
					System.out.println(key + "|" + i + ": " + valueList.get(i));
				}
			}
			// If value is a map, recursive through next levels
			else if (value instanceof Map) {
				multiDimensionReader((Map<String, Object>) value, key);
			}
			// Value cannot be anything else, something bad happened
			else {
				System.err.println("Something horrible happened. Should not have anything but string, list, map");
			}
		}

	}
}
