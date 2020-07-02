package edu.upf.taln.lastus.cwi.feature.calculator;

import edu.upf.taln.lastus.cwi.Utilities;
import edu.upf.taln.ml.feat.base.FeatCalculator;
import edu.upf.taln.ml.feat.base.MyDouble;
import edu.upf.taln.lastus.cwi.feature.context.TrainingCtx;
import edu.upf.taln.lastus.cwi.reader.model.TrainingExample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ahmed on 11/24/15.
 */
public class Dale_Chall implements FeatCalculator<Double, TrainingExample, TrainingCtx> {
    ArrayList<String> ewords = new ArrayList<>();
    public Dale_Chall() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/simplewords.txt"));
        try {
            String line = br.readLine();

            while (line != null) {
                ewords.add(line);

                line = br.readLine();
            }
        } finally {
            br.close();
        }
    }

    @Override
    public MyDouble calculateFeature(TrainingExample obj, TrainingCtx doc) {
        MyDouble value = new MyDouble(0d);

        for (Integer index : Utilities.getIndex(obj.getSentence(), obj.getWordOffsets())) {
            if (ewords.contains(doc.getParsedSent().getLemmaAtIndex(index))) {
                value.setValue(1d);
                return value;
            }
        }

        return value;
    }

}
