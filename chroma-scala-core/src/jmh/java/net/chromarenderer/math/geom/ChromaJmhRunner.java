package net.chromarenderer.math.geom;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


public class ChromaJmhRunner {
    public static void main(String... args) throws Exception {
        Options opts = new OptionsBuilder()
                .include(".*")
                .warmupIterations(2)
                .operationsPerInvocation(1)
                .measurementIterations(100)
                .measurementBatchSize(1000000)
                .jvmArgs("-server"
                        //"-XX:+UseSuperWord",
                        //"-XX:+UnlockDiagnosticVMOptions",
                        //"-XX:+PrintCompilation",
                        //"-XX:+PrintAssembly",
                        //"-XX:PrintAssemblyOptions=intel"
                        //"-XX:CompileCommand=print,*Triangle.*"
                        )
                .forks(1)
                .build();

        new Runner(opts).run();
    }
}
