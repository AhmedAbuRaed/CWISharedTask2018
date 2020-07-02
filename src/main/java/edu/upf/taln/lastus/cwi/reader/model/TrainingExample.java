package edu.upf.taln.lastus.cwi.reader.model;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Class that models a training example
 *
 * @author Ahmed AbuRa'ed
 *
 */
public class TrainingExample {
    private String HITID;
	private String sentence;
    private Pair<Integer, Integer> wordOffsets;
	private String word;
    private Pair<Integer, Integer> nativeNonNativeCount;
    private Pair<Integer, Integer> nativeNonNativeComplexCount;
	private Integer isComplex;
	private Double isComplexScore;

    // Getters and setters
    public String getHITID() {
        return HITID;
    }

    public void setHITID(String HITID) {
        this.HITID = HITID;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Pair<Integer, Integer> getWordOffsets() {
        return wordOffsets;
    }

    public void setWordOffsets(Pair<Integer, Integer> wordOffsets) {
        this.wordOffsets = wordOffsets;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Pair<Integer, Integer> getNativeNonNativeCount() {
        return nativeNonNativeCount;
    }

    public void setNativeNonNativeCount(Pair<Integer, Integer> nativeNonNativeCount) {
        this.nativeNonNativeCount = nativeNonNativeCount;
    }

    public Pair<Integer, Integer> getNativeNonNativeComplexCount() {
        return nativeNonNativeComplexCount;
    }

    public void setNativeNonNativeComplexCount(Pair<Integer, Integer> nativeNonNativeComplexCount) {
        this.nativeNonNativeComplexCount = nativeNonNativeComplexCount;
    }

    public Integer getIsComplex() {
        return isComplex;
    }

    public void setIsComplex(Integer isComplex) {
        this.isComplex = isComplex;
    }

    public Double getIsComplexScore() {
        return isComplexScore;
    }

    public void setIsComplexScore(Double isComplexScore) {
        this.isComplexScore = isComplexScore;
    }
}
