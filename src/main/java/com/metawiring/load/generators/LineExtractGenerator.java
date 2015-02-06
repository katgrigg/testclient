package com.metawiring.load.generators;

import com.metawiring.load.generator.Generator;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineExtractGenerator implements Generator<String> {
    private final static Logger logger = LoggerFactory.getLogger(LineExtractGenerator.class);

    private List<String> lines = new ArrayList<>();

    MersenneTwister rng = new MersenneTwister(System.nanoTime());
    private IntegerDistribution itemDistribution;
    private String filename;

    public LineExtractGenerator(String filename) {
        this.filename = filename;
        loadLines(this.filename);
        itemDistribution= new UniformIntegerDistribution(rng, 0, lines.size()-1);
    }

    @Override
    public String get() {
        int itemIdx = itemDistribution.sample();
        String item = lines.get(itemIdx);
        return item;
    }

    public String toString() {
        return getClass().getSimpleName() + ":" + filename;
    }

    private void loadLines(String filename) {

        InputStream stream = LineExtractGenerator.class.getClassLoader().getResourceAsStream(filename);
        if (stream == null) {
            throw new RuntimeException(filename + " was missing.");
        }

        CharBuffer linesImage;
        try {
            InputStreamReader isr = new InputStreamReader(stream);
            linesImage = CharBuffer.allocate(1024 * 1024);
            isr.read(linesImage);
            isr.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        linesImage.flip();
        Collections.addAll(lines, linesImage.toString().split("\n"));
    }

}
