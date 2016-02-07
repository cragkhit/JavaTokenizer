import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java -jar JavaTokenizer.jar filename [options]");
			System.out.println("Options:\nhi = heavy normalisation (every word is converted to 'W')"
					+ "\nmid = medium normalisation (Java keywords and Java classes are converted to 'W')"
					+ "\nlo = low normalisation (only Java keywords are converted to 'W')");
			return;
		}
		
		JavaTokenizer tokenizer = new JavaTokenizer();
		nGramGenerator ngen = new nGramGenerator(4);
		try {
			ArrayList<String> tokens = tokenizer.getTokensFromFile(args[0]);
			ArrayList<String> ngrams = ngen.generateNGramsFromJavaTokens(tokens);
			for (int i=0; i<ngrams.size(); i++) {
				System.out.print(ngrams.get(i) + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
