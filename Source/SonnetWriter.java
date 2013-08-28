import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


//Analyzes a sample of writing, generates a probabilistic model of which words frequently occur together
//	and writes it's own text based on that model. Hopefully, hilarity will insue
public class SonnetWriter {

	//Fine tuning stuff
	private static int rankCutoff = 40; //When choosing probabilistic next word, select randomly out of the top rankCutoff options
	private static int desiredLength = 120; //Desired length of sonnet, in words
	
	//File stuff
	private static String sonnetFile = "sonnets.txt";
	private static BufferedReader theirSonnetReader;
	private static String theirSonnet;
	
	//Model Stuff
	private static ArrayList<Word> words; //List of all the words found
	private static TreeMap<String, String[]> model; //Model for generating new sonnet. Keys are a word, and the value is an array of possible followers (length rankCutoff)
	
	//Output stuff
	private static String mySonnet;

	
	public static void main(String[] args) throws Exception{
		System.out.println("Initializing...");
		initialize();
		System.out.println("Initialized");
		System.out.println();
		System.out.println("Analyzing...");
		analyzeText();
		System.out.println("Analyzed");
		System.out.println();
		System.out.println("Generating...");
		writeSonnet();
		System.out.println("Generated");
		System.out.println();
		System.out.println("My magnum opus: ");
		System.out.println();
		System.out.println(mySonnet);
	}
	
	private static void initialize() throws Exception {
		theirSonnetReader = new BufferedReader(new FileReader(sonnetFile));
		theirSonnet = "";
		String line = theirSonnetReader.readLine();
		while (line != null) {
			if (line.length() > 0) {theirSonnet += line.toLowerCase() + " @ ";} // @ is a special character used to indicate line break.
			else {theirSonnet += " @ ";} // It's probably safe to use, assuming Shakespeare wasn't trendy enough to use "@"s in his writing
			line = theirSonnetReader.readLine();
		}
		
		words = new ArrayList<Word>();
		model = new TreeMap<String, String[]>();
		mySonnet = "";
	}
	
	@SuppressWarnings("unchecked")
	private static void analyzeText(){
		//Split the sonnet into separate words
		theirSonnet = theirSonnet.substring(1);
		String wordsInSonnet[] = theirSonnet.split(" ");
		
		//Iterate over the array, and add the words to words ArrayList, including followers. Ignore the last word, because a word without followers is useless to us
		for(int i = 0; i < wordsInSonnet.length - 1; i++) {
			addWordWithFollower(wordsInSonnet[i], wordsInSonnet[i+1]);
		}
				
		//Now get all the top followers for each word and put them into the model
		for (Word w : words) {
			ArrayList<Map.Entry<String, Integer>> topFollowers = w.getSortedFollowers(rankCutoff);
			Map.Entry<String, Integer> followers[] = topFollowers.toArray((Map.Entry<String, Integer>[])new Map.Entry[0]);
			String followerStrings[] = new String[rankCutoff];
			for (int i = 0; i < rankCutoff && i < followers.length; i++) {
				followerStrings[i] = followers[i].getKey();
			}
			model.put(w.getWord(), followerStrings);
		}
	}
	
	private static void addWordWithFollower(String word, String follower) {
		Word thisWord = new Word(word);
		if (words.contains(thisWord)) {
			thisWord = words.get(words.indexOf(thisWord)); //I know this might be slightly hack-ish, but frankly who cares
			thisWord.addFollower(follower);
			words.set(words.indexOf(thisWord), thisWord);
		} else {
			thisWord.addFollower(follower);
			words.add(thisWord);
		}
	}
	
	private static void writeSonnet() {
		String previousWord = "@";
		for (int i = 0; i < desiredLength; i++) {
			String nextWord = getRandomFollower(previousWord);
			if (nextWord.equals("@")) { mySonnet += "\n"; }
			else { mySonnet += nextWord; }
			previousWord = nextWord;
			if (!previousWord.equals("@")) { mySonnet += " "; }
		}
		
	}
	
	private static String getRandomFollower(String word) {
		String followers[] = model.get(word);
		String randomFollower = null;
		while (randomFollower == null) {
			int randIndex = (int) (Math.random()*followers.length);
			randomFollower = followers[randIndex];
		}
		return randomFollower;
	}

}
