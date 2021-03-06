package com.stanford.profilematch;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class ResumeDemo {

	public static void main(String[] args) throws Exception {

		String serializedClassifier = "ner-train-model.ser.gz";

		if (args.length > 0) {
			serializedClassifier = args[0];
		}

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

		/* For either a file to annotate or for the hardcoded text example, this
	       demo file shows several ways to process the input, for teaching purposes.
		 */

		boolean check = true;

		if (check) {

			/* For the file, it shows (1) how to run NER on a String, (2) how
	         to get the entities in the String with character offsets, and
	         (3) how to run NER on a whole file (without loading it into a String).
			 */

			String fileContents = IOUtils.slurpFile("resume.txt");
			List<List<CoreLabel>> out = classifier.classify(fileContents);

			for (List<CoreLabel> sentence : out) {
				for (CoreLabel word : sentence) {
					System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
					if (word.get(CoreAnnotations.AnswerAnnotation.class).equalsIgnoreCase("java")) {

					}
				}
				//System.out.println();
			}

			System.out.println("---");
			StringBuffer data = new StringBuffer("");
			Set<String> dataSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
			for (Triple<String, Integer, Integer> item : list) {
				System.out.println(item.first() + ": " + fileContents.substring(item.second(), item.third()));
				data.append(fileContents.substring(item.second(), item.third())).append(",");
				dataSet.add(fileContents.substring(item.second(), item.third()));
			}
			String technologiesFound = data.toString();
			if (technologiesFound.endsWith(",")) {
				technologiesFound = technologiesFound.substring(0, technologiesFound.length());
			}
			System.out.println("Java related technologies found in Resume " + dataSet);

			/*System.out.println("---");
	      out = classifier.classifyFile("C:\\projects\\profilematch-stanford\\src\\main\\resources\\JD.txt");
	      for (List<CoreLabel> sentence : out) {
	        for (CoreLabel word : sentence) {
	          System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
	        }
	        System.out.println();
	      }

	      System.out.println("---");
	      List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
	      for (Triple<String, Integer, Integer> item : list) {
	        System.out.println(item.first() + ": " + fileContents.substring(item.second(), item.third()));
	      }
	      System.out.println("---");
	      DocumentReaderAndWriter<CoreLabel> readerAndWriter = classifier.makePlainTextReaderAndWriter();
	      classifier.classifyAndWriteAnswersKBest("C:\\projects\\profilematch-stanford\\src\\main\\resources\\JD.txt", 10, readerAndWriter);

	      System.out.println("---");
	      System.out.println("Per-token marginalized probabilities");
	      classifier.printProbs("C:\\projects\\profilematch-stanford\\src\\main\\resources\\JD.txt", readerAndWriter);*/

			// -- This code prints out the first order (token pair) clique probabilities.
			// -- But that output is a bit overwhelming, so we leave it commented out by default.
			// System.out.println("---");
			// System.out.println("First Order Clique Probabilities");
			// ((CRFClassifier) classifier).printFirstOrderProbs("JD.txt", readerAndWriter);

		} else {

			/* For the hard-coded String, it shows how to run it on a single
	         sentence, and how to do this and produce several formats, including
	         slash tags and an inline XML output format. It also shows the full
	         contents of the {@code CoreLabel}s that are constructed by the
	         classifier. And it shows getting out the probabilities of different
	         assignments and an n-best list of classifications with probabilities.
			 */

			String[] example = {"Good afternoon Rajat Raina, how are you today?",
			"I go to school at Stanford University, which is located in California." };
			for (String str : example) {
				System.out.println(classifier.classifyToString(str));
			}
			System.out.println("---");

			for (String str : example) {
				// This one puts in spaces and newlines between tokens, so just print not println.
				System.out.print(classifier.classifyToString(str, "slashTags", false));
			}
			System.out.println("---");

			for (String str : example) {
				// This one is best for dealing with the output as a TSV (tab-separated column) file.
				// The first column gives entities, the second their classes, and the third the remaining text in a document
				System.out.print(classifier.classifyToString(str, "tabbedEntities", false));
			}
			System.out.println("---");

			for (String str : example) {
				System.out.println(classifier.classifyWithInlineXML(str));
			}
			System.out.println("---");

			for (String str : example) {
				System.out.println(classifier.classifyToString(str, "xml", true));
			}
			System.out.println("---");

			for (String str : example) {
				System.out.print(classifier.classifyToString(str, "tsv", false));
			}
			System.out.println("---");

			// This gets out entities with character offsets
			int j = 0;
			for (String str : example) {
				j++;
				List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(str);
				for (Triple<String,Integer,Integer> trip : triples) {
					System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n",
							trip.first(), trip.second(), trip.third, j);
				}
			}
			System.out.println("---");

			// This prints out all the details of what is stored for each token
			int i=0;
			for (String str : example) {
				for (List<CoreLabel> lcl : classifier.classify(str)) {
					for (CoreLabel cl : lcl) {
						System.out.print(i++ + ": ");
						System.out.println(cl.toShorterString());
					}
				}
			}

			System.out.println("---");
		}
	}
}
