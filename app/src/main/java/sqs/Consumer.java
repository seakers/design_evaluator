package sqs;

import vassar.GlobalScope;
import vassar.database.DatabaseClient;
import vassar.database.template.TemplateRequest;
import vassar.database.template.request.*;
import vassar.database.template.functions.JessExtension;
import vassar.jess.JessEngine;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.ArrayList;

public class Consumer implements Runnable{

    private SqsClient      sqsClient;
    private DatabaseClient databaseClient;
    private boolean        debug;
    private JessEngine     engine;

    public static class Builder {

        private SqsClient      sqsClient;
        private DatabaseClient databaseClient;
        private boolean        debug;
        private JessEngine     engine;

        public Builder(SqsClient sqsClient){
            this.sqsClient = sqsClient;
        }

        public Builder setDatabaseClient(DatabaseClient databaseClient) {
            this.databaseClient = databaseClient;
            return this;
        }

        public Builder setJessEngine(JessEngine engine) {
            this.engine = engine;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Consumer build(){
            Consumer build    = new Consumer();
            build.engine      = this.engine;
            build.sqsClient   = this.sqsClient;
            build.debug       = this.debug;
            build.databaseClient = this.databaseClient;
            return build;
        }

    }


    public void run() {


        // ---------- REQUESTS
        ArrayList<TemplateRequest> requests = new ArrayList<>() {{

            // TEMPLATES FIRST
//            add(
//                    new OrbitAttributeTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/AttributeTemplateRequest.clp")
//                            .templateHeader("DATABASE::Orbit")
//                            .build()
//            );
            add(
                    new MeasurementTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/MeasurementTemplate.clp")
                            .templateHeader("REQUIREMENTS::Measurement")
                            .build()
            );
//            add(
//                    new InstrumentTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/MeasurementTemplate.clp")
//                            .templateHeader("CAPABILITIES::Manifested-instrument")
//                            .build()
//            );
//            add(
//                    new LaunchVehicleAttributeTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/AttributeTemplateRequest.clp")
//                            .templateHeader("DATABASE::Launch-vehicle")
//                            .build()
//            );







//            add(
//                    new OrbitTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/abstractDeffacts.clp")
//                            .orbitHeader("DATABASE::Orbit")
//                            .templateHeader("orbit-information-facts")
//                            .build()
//            );
            // ---------------------------------------------------------------------------------------------------------
//            add(
//                    new LaunchVehicleTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/abstractDeffacts.clp")
//                            .launchVehicleHeader("DATABASE::Launch-vehicle")
//                            .templateHeader("DATABASE::launch-vehicle-information-facts")
//                            .build()
//            );
            // ---------------------------------------------------------------------------------------------------------
//
//
//            add(
//                    new MissionAttributeTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/AttributeTemplateRequest.clp")
//                            .templateHeader("MANIFEST::Mission")
//                            .build()
//            );
//            add(
//                    new FuzzyAttributeTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/FuzzyAttributeTemplate.clp")
//                            .build()
//            );
//            add(
//                    new AttributeInheritanceTemplateRequest.Builder()
//                            .applyExtension(new JessExtension())
//                            .templateFilePath("/home/gabe/repos/seakers/design_evaluator/app/src/main/java/evaluator/database/template/defs/AttributeInheritanceTemplate.clp")
//                            .build()
//            );

        }};

        requests.forEach(
                request -> System.out.println(this.databaseClient.processTemplateRequest(request).getTemplateString())
        );

        requests.forEach(
                request -> this.databaseClient.processTemplateRequest(request).evaluate(this.engine.getEngine())
        );


        this.databaseClient.writeDebugInfo();
    }
}
