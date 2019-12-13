package net.contra.jmd.transformers.generic;

import net.contra.jmd.util.GenericMethods;
import net.contra.jmd.util.LogHandler;
import org.apache.bcel.generic.ClassGen;

import java.util.HashMap;
import java.util.Map;

public class TransformerTemplate {
    private static LogHandler logger = new LogHandler("StringFixer");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    String JAR_NAME;

    public void transform() {
        logger.log("Generic Transformer");

        logger.log("Deobfuscation finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");

    }
}
