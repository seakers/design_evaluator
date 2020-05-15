package evaluator.sqs;

import evaluator.database.DatabaseClient;
import evaluator.database.template.TemplateRequest;
import evaluator.database.template.requests.*;
import evaluator.database.template.functions.JessExtension;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.ArrayList;

public class Consumer implements Runnable{

    private SqsClient      sqsClient;
    private DatabaseClient databaseClient;
    private boolean        debug;

    public static class Builder {

        private SqsClient   sqsClient;
        private DatabaseClient databaseClient;
        private boolean     debug;

        public Builder(SqsClient sqsClient){
            this.sqsClient = sqsClient;
        }

        public Builder setDatabaseClient(DatabaseClient databaseClient) {
            this.databaseClient = databaseClient;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Consumer build(){
            Consumer build    = new Consumer();
            build.sqsClient   = this.sqsClient;
            build.debug       = this.debug;
            build.databaseClient = this.databaseClient;
            return build;
        }

    }


    public void run() {

        // ---------- REQUESTS
        ArrayList<TemplateRequest> requests = new ArrayList<TemplateRequest>() {{

            add(
                    new OrbitTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/abstractDeffacts.clp")
                            .orbitHeader("DATABASE::Orbit")
                            .templateHeader("orbit-information-facts")
                            .build()
            );
            add(
                    new LaunchVehicleTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/abstractDeffacts.clp")
                            .launchVehicleHeader("DATABASE::Launch-vehicle")
                            .templateHeader("DATABASE::launch-vehicle-information-facts")
                            .build()
            );
            add(
                    new MeasurementTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/measurementTemplate.clp")
                            .templateHeader("REQUIREMENTS::Measurement")
                            .build()
            );
            add(
                    new InstrumentTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/measurementTemplate.clp")
                            .templateHeader("CAPABILITIES::Manifested-instrument")
                            .build()
            );
            add(
                    new OrbitAttributeTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/attributeTemplate.clp")
                            .templateHeader("DATABASE::Orbit")
                            .build()
            );
            add(
                    new LaunchVehicleAttributeTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/attributeTemplate.clp")
                            .templateHeader("DATABASE::Launch-vehicle")
                            .build()
            );
            add(
                    new MissionAttributeTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("app/src/main/java/evaluator/database/template/definitions/attributeTemplate.clp")
                            .templateHeader("MANIFEST::Mission")
                            .build()
            );
            add(
                    new AttributeInheritanceTemplateRequest.Builder()
                            .applyExtension(new JessExtension())
                            .templateFilePath("/app/src/main/java/evaluator/database/template/definitions/attributeTemplate.clp")
                            .templateHeader("MANIFEST::Mission")
                            .build()
            );

        }};



        requests.forEach(
                request -> System.out.println(this.databaseClient.processTemplateRequest(request))
        );


        this.databaseClient.writeDebugInfo();
    }
}
