package de.variantsync.matching.experiments.common;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.List;

public class ExperimentConfiguration {
    private final Configuration config;
    private static final String EXPERIMENTS_DATASETS_FOLDER = "experiments.dir.datasets";
    private static final String EXPERIMENTS_RESULTS_FOLDER = "experiments.dir.results";
    private static final String EXPERIMENTS_MATCHERS_NWM = "experiments.matchers.nwm";
    private static final String EXPERIMENTS_MATCHERS_PAIRWISE_ASCENDING = "experiments.matchers.pairwise-ascending";
    private static final String EXPERIMENTS_MATCHERS_PAIRWISE_DESCENDING = "experiments.matchers.pairwise-descending";
    private static final String EXPERIMENTS_MATCHERS_RAQUN = "experiments.matchers.raqun";
    private static final String EXPERIMENTS_EXECUTION_VERBOSE = "experiments.execution.verbose";
    private static final String EXPERIMENTS_EXECUTION_REPEATS = "experiments.execution.repeats";
    private static final String EXPERIMENTS_RQ2_START_k = "experiments.rq2.start-k";
    private static final String EXPERIMENTS_RQ2_MAX_K = "experiments.rq2.max-k";
    private static final String EXPERIMENTS_RQ2_MAX_K_ARGOUML = "experiments.rq2.max-k-argouml";
    private static final String EXPERIMENTS_RQ1_DATASETS = "experiments.rq1.datasets";
    private static final String EXPERIMENTS_RQ2_DATASETS = "experiments.rq2.datasets";

    public ExperimentConfiguration(File propertiesFile) {
        try {
            this.config = new Configurations().properties(propertiesFile);
        } catch (ConfigurationException e) {
            System.err.println("Was not able to load properties file " + propertiesFile);
            throw new RuntimeException(e);
        }
    }

    public String datasetsFolder() {
        return config.getString(EXPERIMENTS_DATASETS_FOLDER);
    }

    public String resultsFolder() {
        return config.getString(EXPERIMENTS_RESULTS_FOLDER);
    }

    public boolean shouldRunNwM() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_NWM);
    }

    public boolean shouldRunPairwiseDesc() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_PAIRWISE_DESCENDING);
    }

    public boolean shouldRunPairwiseAsc() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_PAIRWISE_ASCENDING);
    }

    public boolean shouldRunRaQuN() {
        return config.getBoolean(EXPERIMENTS_MATCHERS_RAQUN);
    }

    public boolean verboseResults() {
        return config.getBoolean(EXPERIMENTS_EXECUTION_VERBOSE);
    }

    public int numberOfRepeats() {
        return config.getInt(EXPERIMENTS_EXECUTION_REPEATS);
    }

    public int startK() {
        return config.getInt(EXPERIMENTS_RQ2_START_k);
    }

    public int maxK() {
        return config.getInt(EXPERIMENTS_RQ2_MAX_K);
    }

    public int maxKArgoUML() {
        return config.getInt(EXPERIMENTS_RQ2_MAX_K_ARGOUML);
    }

    public List<String> datasetsRQ1() {
        return config.getList(String.class, EXPERIMENTS_RQ1_DATASETS);
    }

    public List<String> datasetsRQ2() {
        return config.getList(String.class, EXPERIMENTS_RQ2_DATASETS);
    }
}
