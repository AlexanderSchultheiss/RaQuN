package de.variantsync.matching.raqun.tree;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Random;

public class RVectorTest {
    private Random random = new SecureRandom();

    @Test
    public void dimensionsInitializedCorrectly() {
        RVector simpleVector = new RVector(1);
        assert simpleVector.getDimensions() == 1;

        int numberOfDims = random.nextInt(100_000) + 1;
        RVector anotherVector = new RVector(numberOfDims);
        assert anotherVector.getDimensions() == numberOfDims;
        for (int i = 0; i < numberOfDims; i++) {
            assert Double.compare(anotherVector.getCoord(i), 0) == 0;
        }
    }

    @Test
    public void dimensionsAreSetCorrectly() {
        RVector simpleVector = new RVector(3);

        for (int i = 0; i < simpleVector.getDimensions(); i++) {
            assert Double.compare(simpleVector.getCoord(i), 0) == 0;
        }

        simpleVector.setCoord(0, 1);
        simpleVector.setCoord(1, 0.5d);
        simpleVector.setCoord(2, 20);

        assert Double.compare(simpleVector.getCoord(0), 1) == 0;
        assert Double.compare(simpleVector.getCoord(1), 0.5) == 0;
        assert Double.compare(simpleVector.getCoord(2), 20) == 0;
    }
}