package vassar;



//          ____   ____                                     ______  __    _                  _
//         |_  _| |_  _|                                  .' ___  |[  |  (_)                / |_
//           \ \   / /,--.   .--.   .--.   ,--.   _ .--. / .'   \_| | |  __  .---.  _ .--. `| |-'
//            \ \ / /`'_\ : ( (`\] ( (`\] `'_\ : [ `/'`\]| |        | | [  |/ /__\\[ `.-. | | |
//             \ ' / // | |, `'.'.  `'.'. // | |, | |    \ `.___.'\ | |  | || \__., | | | | | |,
//              \_/  \'-;__/[\__) )[\__) )\'-;__/[___]    `.____ .'[___][___]'.__.'[___||__]\__/


import jess.Rete;
import vassar.architecture.AbstractArchitecture;
import vassar.architecture.Architecture;
import vassar.evaluator.AbstractArchitectureEvaluator;
import vassar.evaluator.ArchitectureEvaluator;
import vassar.jess.Resource;
import vassar.result.Result;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VassarClient {

    private Resource engine;

    public static class Builder {

        private Resource engine;

        public Builder() {

        }

        public Builder setEngine(Resource engine) {
            this.engine = engine;
            return this;
        }

        public VassarClient build() {
            VassarClient build = new VassarClient();
            build.engine = this.engine;
            return build;
        }

    }


    public Result evaluateArchitecture(String bitString){

        AbstractArchitecture arch = new Architecture(bitString, 1, this.engine.getProblem());

        System.out.println(arch.isFeasibleAssignment());

        AbstractArchitectureEvaluator t = new ArchitectureEvaluator(this.engine, arch, "Slow");

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<Result> future = executorService.submit(t);

        Result result = null;
        try {
            result = future.get();
        }
        catch (ExecutionException e) {
            System.out.println("Exception when evaluating an architecture");
            e.printStackTrace();
            System.exit(-1);
        }
        catch (InterruptedException e) {
            System.out.println("Execution got interrupted while evaluating an architecture");
            e.printStackTrace();
            System.exit(-1);
        }

        return result;
    }






}
