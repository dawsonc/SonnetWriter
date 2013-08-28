import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class Word {

	//This is a class for storing word-sequencing data
	
	//Instance variables
	private String thisWord;
	private TreeMap<String, Integer> followingWords; //A map of all the words found immediately after this word, mapped to their frequencies
	
	public Word(String word){
		thisWord = word;
		followingWords = new TreeMap<String, Integer>();
	}
	
	public void addFollower(String follower){
		if (followingWords.containsKey(follower)) {
			int currentCount = followingWords.get(follower);
			currentCount ++;
			followingWords.put(follower, currentCount);
		} else {
			followingWords.put(follower, 1);
		}
	}
	
	/**
	 * 
	 * @param n The number of top entries to return
	 * @return A SortedSet of the top n entries
	 */
	public ArrayList<Map.Entry<String, Integer>> getSortedFollowers(int n) {
		ArrayList<Map.Entry<String, Integer>> toReturn = new ArrayList<Map.Entry<String, Integer>>();
		@SuppressWarnings("unchecked")
		Map.Entry<String, Integer> sortedFollowers[] = entriesSortedByValue(followingWords).toArray((Map.Entry<String, Integer>[])new Map.Entry[0]); 
		
		// Because the list of followers gets sorted in ascending order (because reasons), you have to start from the back
		for(int i = 0; i < n && i < sortedFollowers.length; i++) {
			toReturn.add(sortedFollowers[sortedFollowers.length - 1 - i]);
		}
		
		return toReturn;
	}
	
	public String getWord(){
		return thisWord;
	}
	
	//Override equals() so that it matches based on the word, not the object
	@Override
	public boolean equals(Object o){
		if (o != null && o.getClass() == this.getClass()) {
			if (this.getWord().equals(((Word)o).getWord())) {return true;}
		}
		return false;
	}
	
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValue(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
	
	public String toString() {
		return thisWord + " (" + followingWords.size() + ")";
	}
}