// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.HashMap;
import java.util.Map;

import marmot.core.Options;



public class MorphOptions extends Options {
	private static final long serialVersionUID = 1L;
	public static final String TRAIN_FILE = "train-file";
	public static final String TEST_FILE = "test-file";
	public static final String RARE_WORD_MAX_FREQ = "rare-word-max-freq";
	public static final String SHAPE_TRIE_PATH = "shape-trie-path";
	public static final String MODEL_FILE = "model-file";
	public static final String RESTRICT_TRANSITIONS = "restrict-transitions";
	//public static final String SUBPOS_TO_POS = "subpos-to-pos";
	public static final String SHAPE = "shape";
	public static final String TAG_MORPH = "tag-morph";
	public static final String PRED_FILE = "pred-file";
	public static final String OBSERVED_FEATURE = "observed-feature";
	public static final String SPLIT_MORPHS = "split-morphs";
	public static final String SUBTAG_SEPERATOR = "subtag-seperator";
	public static final String TYPE_DICT = "type-dict";
	public static final String SPLIT_POS = "split-pos";
	public static final String FLOAT_TYPE_DICT = "type-embeddings";
	public static final String NORMALIZE_FORMS = "normalize-forms";
	
	private static final Map<String, String> DEFALUT_VALUES_ = new HashMap<String, String>();
	private static final Map<String, String> COMMENTS_ = new HashMap<String, String>();
	

	static {
		DEFALUT_VALUES_.put(TRAIN_FILE, "");
		COMMENTS_.put(TRAIN_FILE, "Input training file");
		DEFALUT_VALUES_.put(TEST_FILE, "");
		COMMENTS_.put(TEST_FILE, "Input test file. (optional for training)");
		DEFALUT_VALUES_.put(PRED_FILE, "");
		COMMENTS_.put(PRED_FILE, "Output prediction file in CoNLL09. (optional for training)");
		DEFALUT_VALUES_.put(RARE_WORD_MAX_FREQ, "10");
		COMMENTS_.put(RARE_WORD_MAX_FREQ, "Maximal frequency of a rare word.");
		DEFALUT_VALUES_.put(SHAPE_TRIE_PATH, "");
		COMMENTS_.put(SHAPE_TRIE_PATH, "Path to the shape trie. Will be created if non-existent.");
		DEFALUT_VALUES_.put(MODEL_FILE, "");
		COMMENTS_.put(MODEL_FILE, "Output model file.");
		DEFALUT_VALUES_.put(RESTRICT_TRANSITIONS, "true");
		COMMENTS_.put(RESTRICT_TRANSITIONS, "Whether to only allow POS -> MORPH transitions that have been seen during training.");
		DEFALUT_VALUES_.put(SHAPE, "false");
		COMMENTS_.put(SHAPE, "Whether to use shape features.");
		DEFALUT_VALUES_.put(TAG_MORPH, "true");
		COMMENTS_.put(TAG_MORPH, "Whether to train a morphological tagger or a POS tagger.");
		DEFALUT_VALUES_.put(OBSERVED_FEATURE, "true");
		COMMENTS_.put(OBSERVED_FEATURE, "Whether to use the observed feature. Have a look at the paper!");
		DEFALUT_VALUES_.put(SPLIT_POS, "false");
		COMMENTS_.put(SPLIT_POS, "Whether to split POS tags. See subtag-seperator. Have a look at the paper!");
		DEFALUT_VALUES_.put(SPLIT_MORPHS, "true");
		COMMENTS_.put(SPLIT_MORPHS, "Whether to split MORPG tags. See subtag-seperator. Have a look at the paper!");
		DEFALUT_VALUES_.put(SUBTAG_SEPERATOR, "\\|");
		COMMENTS_.put(SUBTAG_SEPERATOR, "Regular expression to use for splitting tags. (Has to work with Java's String.split)");
		DEFALUT_VALUES_.put(TYPE_DICT, "");
		COMMENTS_.put(TYPE_DICT, "Word type dictionary file (optional)");
		DEFALUT_VALUES_.put(FLOAT_TYPE_DICT, "");
		COMMENTS_.put(FLOAT_TYPE_DICT, "Word type embeddings file (optional)");
		DEFALUT_VALUES_.put(NORMALIZE_FORMS, "false");
		COMMENTS_.put(NORMALIZE_FORMS, "Whether to normalize word forms before tagging.");
	}

	public MorphOptions() {
		super();
		putAll(DEFALUT_VALUES_);
	}
	
	public String getTrainFile() {
		return getProperty(TRAIN_FILE);
	}

	public String getTestFile() {
		return getProperty(TEST_FILE);
	}
	
	public int getRareWordMaxFreq() {
		return Integer.parseInt(getProperty(RARE_WORD_MAX_FREQ));
	}
	
	public boolean getRestricTransitions() {
		return Boolean.parseBoolean(getProperty(RESTRICT_TRANSITIONS));
	}
	
	public boolean getTagMorph() {
		return Boolean.valueOf(getProperty(TAG_MORPH));
	}
	
	public String getPredFile() {
		return getProperty(PRED_FILE);
	}
	
	public boolean getShape() {
		return Boolean.parseBoolean(getProperty(SHAPE));
	}
	
	public String getShapeTriePath() {
		return getProperty(SHAPE_TRIE_PATH);
	}

	public String getModelFile() {
		return getProperty(MODEL_FILE);
	}

	public boolean getObservedFeature() {
		return Boolean.parseBoolean(getProperty(OBSERVED_FEATURE));
	}

	public boolean getSplitMorphs() {
		return Boolean.parseBoolean(getProperty(SPLIT_MORPHS));
	}
	
	public String getSubTagSeperator() {
		return getProperty(SUBTAG_SEPERATOR);
	}

	public String getMorphDict() {
		return getProperty(TYPE_DICT);
	}
	
	public String getFloatTypeDict() {
		return getProperty(FLOAT_TYPE_DICT);
	}

	public boolean getSplitPos() {
		return Boolean.parseBoolean(getProperty(SPLIT_POS));
	}
	
	protected void usage() {
		super.usage();
		System.err.println("Morph Options:");
		usage(DEFALUT_VALUES_, COMMENTS_);
		System.err.println();
	}

	public boolean getNormalizeForms() {
		return Boolean.parseBoolean(getProperty(NORMALIZE_FORMS));
	}

}
