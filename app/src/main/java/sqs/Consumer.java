package sqs;

import vassar.VassarClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import vassar.result.Result;

public class Consumer implements Runnable{

    private boolean      debug;
    private VassarClient client;
    private SqsClient    sqsClient;

    public static class Builder {

        private SqsClient      sqsClient;
        private boolean        debug;
        private VassarClient client;

        public Builder(SqsClient sqsClient){
            this.sqsClient = sqsClient;
        }

        public Builder setVassarClient(VassarClient client) {
            this.client = client;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Consumer build(){
            Consumer build  = new Consumer();
            build.sqsClient = this.sqsClient;
            build.debug     = this.debug;
            build.client    = this.client;
            return build;
        }

    }


    public void run() {
        System.out.println("running consumer");

        // From SQS message
        // Design ID: D499; Science: 0.6900; Cost ($M): 3298.04
        String bitString = "1000000010010011001000110";

        Result result = this.client.evaluateArchitecture(bitString);
        System.out.println("----------- COST");
        System.out.println(result.getCost());
        System.out.println(result.getScience());





















        // this.databaseClient.writeDebugInfo();
    }
}
