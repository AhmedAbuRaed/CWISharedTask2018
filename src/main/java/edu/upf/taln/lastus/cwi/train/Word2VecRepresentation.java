package edu.upf.taln.lastus.cwi.train;

import edu.upf.taln.lastus.cwi.reader.TestingDataReader;
import edu.upf.taln.lastus.cwi.reader.TrainingDataReader;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;
import org.apache.commons.lang3.tuple.Pair;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Word2VecRepresentation {

    public static void main(String args[]) {
        String workingDir = args[1];

        Set<String> stopWords = getStopWordsSet(workingDir);

        Word2Vec gvec = null;
        //Word2Vec bnvec = null;

        //Load Word2Vec models in case it is in the pipeline
        System.out.println("Loading Word2vec Models ...");
        //Get file from resources folder
        File gModel = new File(workingDir + File.separator + "GoogleNews-vectors-negative300.bin.gz");
        gvec = WordVectorSerializer.readWord2VecModel(gModel);

        /*File bnModel = new File(workingDir + File.separator + "sw2v_synsets_cbow_wikipedia_vectors.bin");
        bnvec = WordVectorSerializer.readWord2VecModel(bnModel);
        System.out.println("Word2vec Models Loaded ...");*/

        File output = new File(workingDir + File.separator + "News_Train.txt");

        List<TrainingExample> trainingExamples = TrainingDataReader.readTrainingSet(workingDir + "/src/main/resources/data/CWI 2018 Training Set/english/News_Train.tsv", false);
        for (int i = 0; i < trainingExamples.size(); i++) {
            String sentence = trainingExamples.get(i).getSentence().toLowerCase();
            Pair<Integer, Integer> wordOffsets = trainingExamples.get(i).getWordOffsets();
            Integer isComplex = trainingExamples.get(i).getIsComplex();
            Double isComplexScore = trainingExamples.get(i).getIsComplexScore();

            String before = sentence.substring(0, wordOffsets.getLeft());
            String word = trainingExamples.get(i).getWord().toLowerCase();
            String after = sentence.substring(wordOffsets.getRight());

            String[] beforetokens = before.split(" ");
            String[] aftertokens = after.split(" ");

            INDArray beforeDim = Nd4j.zeros(300);
            INDArray wordDim = Nd4j.zeros(300);
            INDArray afterDim = Nd4j.zeros(300);

            int beforeCounter = 0;
            int afterCounter = 0;

            for(String token: beforetokens)
            {
                if((!stopWords.contains(token) && (token.length() > 1) && gvec.hasWord(token)))
                {
                    beforeDim = beforeDim.addRowVector(gvec.getWordVectorMatrix(token));
                    beforeCounter++;
                }
            }
            beforeDim = beforeDim.div(beforeCounter);

            if(gvec.hasWord(word))
            {
                wordDim = gvec.getWordVectorMatrix(word);
            }

            for(String token: aftertokens)
            {
                if((!stopWords.contains(token) && (token.length() > 1) && gvec.hasWord(token)))
                {
                    afterDim = afterDim.addRowVector(gvec.getWordVectorMatrix(token));
                    afterCounter++;
                }
            }
            afterDim = afterDim.div(afterCounter);

            append2CSVFile(Arrays.toString(beforeDim.data().asDouble()).replaceAll("[\\[\\] ]", "").replaceAll("NaN", "0") +
                    "," +
                    Arrays.toString(wordDim.data().asDouble()).replaceAll("[\\[\\] ]", "").replaceAll("NaN", "0")  +
                    "," +
                    Arrays.toString(afterDim.data().asDouble()).replaceAll("[\\[\\] ]", "").replaceAll("NaN", "0")  +
                    "," +
                    Double.toString(Transforms.cosineSim(wordDim, beforeDim)).replaceAll("NaN", "0") +
                    "," +
                    Double.toString(Transforms.cosineSim(wordDim, afterDim)).replaceAll("NaN", "0") +
                    "," +
                    isComplex, output);
        }
    }

    public static Set<String> getStopWordsSet(String workingDir) {
        Set<String> stopWords = new HashSet<String>();
        BufferedReader readerStopWords = null;
        try {
            String line;
            readerStopWords = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(workingDir + File.separator + "full-stop-words.lst"), "UTF-8"));
            while ((line = readerStopWords.readLine()) != null) {
                if (!line.equals("")) {
                    stopWords.add(line.toLowerCase().trim());
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    public static void append2CSVFile(String lineCSV, File file) {
        PrintWriter out = null;
        BufferedWriter bufWriter;

        try{
            bufWriter =
                    Files.newBufferedWriter(
                            Paths.get(file.toURI()),
                            Charset.forName("UTF8"),
                            StandardOpenOption.WRITE,
                            StandardOpenOption.APPEND,
                            StandardOpenOption.CREATE);
            out = new PrintWriter(bufWriter, true);
        }catch(IOException e){
            //Oh, no! Failed to create PrintWriter
            e.printStackTrace();
        }

        //After successful creation of PrintWriter
        out.println(lineCSV);


        //After done writing, remember to close!
        out.close();
    }

}
