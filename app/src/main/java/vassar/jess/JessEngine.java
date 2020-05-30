package vassar.jess;

import jess.Rete;
import jess.Userfunction;
import vassar.database.DatabaseClient;
import vassar.database.template.TemplateRequest;
import vassar.database.template.functions.JessExtension;

import java.util.ArrayList;

public class JessEngine {

    private Rete   engine;
    private String appPath;

    private DatabaseClient dbClient;


    public static class Builder {

        private Rete engine;
        private String appPath;

        private DatabaseClient dbClient;
        private ArrayList<TemplateRequest> requests;

        public Builder(DatabaseClient dbClient){
            this.engine = new Rete();
            this.dbClient = dbClient;
        }

        public Builder addUserFunctionBatch(ArrayList<Userfunction> funcs) {
            funcs.forEach( func -> this.engine.addUserfunction(func));
            return this;
        }

        public Builder evalTemplates(String appPath) {
            try {
                // APP PATH
                this.appPath = appPath;
                this.engine.eval("(defglobal ?*app_path* = \"" + this.appPath + "\")");
                this.engine.eval("(import seakers.vassar.*)");
                this.engine.eval("(import java.util.*)");
                this.engine.eval("(import jess.*)");
                this.engine.eval("(defglobal ?*rulesMap* = (new java.util.HashMap))");
                this.engine.eval("(set-reset-globals nil)");

                // MODULES
                System.out.println("Batching modules: " + this.appPath + "/modules.clp");
                this.engine.batch(this.appPath + "/modules.clp");

                // TEMPLATES
                System.out.println("Batching templates: " + this.appPath + "/templates.clp");
                this.engine.batch(this.appPath + "/templates.clp");

                return this;
            }
            catch (Exception e) {
                System.out.println("Error setting jess engine app path" + e.getMessage());
            }
            return this;
        }

        public Builder evalBatchFunctions(ArrayList<String> funcs) {
            try {
                for (String func: funcs) {
                    this.engine.batch(func);
                }
            }
            catch (Exception e) {
                System.out.println("Error setting jess engine app path" + e.getMessage());
            }
            return this;
        }

        public Builder evalStaticFunctions() {
            try {
                this.engine.eval("(deffunction numerical-to-fuzzy (?num ?values ?mins ?maxs)"  +
                        "(bind ?ind 1)"  +
                        "(bind ?n (length$ ?values))"  +
                        "(while (<= ?ind ?n)"  +
                        "(if (and (< ?num (nth$ ?ind ?maxs)) (>= ?num (nth$ ?ind ?mins))) then (return (nth$ ?ind ?values))"  +
                        "else (++ ?ind))))");
                this.engine.eval("(deffunction revisit-time-to-temporal-resolution (?region ?values)"  +
                        "(if (eq ?region Global) then "  +
                        "(return (nth$ 1 ?values))"  +
                        " elif (eq ?region Tropical-regions) then"  +
                        "(return (nth$ 2 ?values))"  +
                        " elif (eq ?region Northern-hemisphere) then"  +
                        "(return (nth$ 3 ?values))"  +
                        " elif (eq ?region Southern-hemisphere) then"  +
                        "(return (nth$ 4 ?values))"  +
                        " elif (eq ?region Cold-regions) then"  +
                        "(return (nth$ 5 ?values))"  +
                        " elif (eq ?region US) then"  +
                        "(return (nth$ 6 ?values))"  +
                        " else (throw new JessException \"revisit-time-to-temporal-resolution: The region of interest is invalid\")" +
                        "))");

                this.engine.eval("(deffunction fuzzy-max (?att ?v1 ?v2) "  +
                        "(if (>= (SameOrBetter ?att ?v1 ?v2) 0) then "  +
                        "?v1 else ?v2))");

                this.engine.eval("(deffunction fuzzy-min (?att ?v1 ?v2) "  +
                        "(if (<= (SameOrBetter ?att ?v1 ?v2) 0) then "  +
                        "?v1 else ?v2))");


                this.engine.eval("(deffunction fuzzy-avg (?v1 ?v2) "  +
                        "(if (or (and (eq ?v1 High) (eq ?v2 Low)) (and (eq ?v1 Low) (eq ?v2 High))) then "  +
                        " \"Medium\" "  +
                        " else (fuzzy-min Accuracy ?v1 ?v2)))");

                this.engine.eval("(deffunction member (?elem ?list) "  +
                        "(if (listp ?list) then "  +
                        " (neq (member$ ?elem ?list) FALSE) "  +
                        " else (?list contains ?elem)))");

                this.engine.eval("(deffunction valid-orbit (?typ ?h ?i ?raan) "  +
                        "(bind ?valid TRUE)"  +
                        "(if (and (eq ?typ GEO) (or (neq ?h GEO) (neq ?i 0))) then (bind ?valid FALSE))"  +
                        "(if (and (neq ?typ GEO) (eq ?h GEO)) then (bind ?valid FALSE))"  +
                        "(if (and (eq ?typ SSO) (neq ?i SSO)) then (bind ?valid FALSE))"  +
                        "(if (and (neq ?typ SSO) (eq ?i SSO)) then (bind ?valid FALSE))"  +
                        "(if (and (neq ?typ SSO) (neq ?raan NA)) then (bind ?valid FALSE))"  +
                        "(if (and (eq ?typ SSO) (eq ?raan NA)) then (bind ?valid FALSE))"  +
                        "(if (and (or (eq ?h 1000) (eq ?h 1300)) (neq ?i near-polar)) then (bind ?valid FALSE))"  +
                        "(if (and (< ?h 400) (or (neq ?typ LEO) (eq ?i SSO) (eq ?i near-polar))) then (bind ?valid FALSE))"  +
                        " (return ?valid))");

                this.engine.eval("(deffunction worth-improving-measurement (?meas) "  +
                        "(bind ?worth TRUE)"  +
                        "(bind ?arr (matlabf get_related_suboj ?meas))"  +
                        "(if (eq ?arr nil) then (return FALSE))"  +
                        "(bind ?iter (?arr iterator))"  +
                        "(while (?iter hasNext) "  +
                        "(bind ?subobj (?iter next)) "  +
                        "(if (eq (eval ?subobj) 1) then (bind ?worth FALSE))) "  +
                        "(return ?worth))");

                this.engine.eval("(deffunction meas-group (?p ?gr)"  +
                        "(if (eq (str-compare (sub-string 1 1 ?p) A) 0) then (return FALSE))"  +
                        "(bind ?pos (str-index \" \" ?p)) " +
                        "(bind ?str (sub-string 1 (- ?pos 1) ?p)) " +
                        "(bind ?meas-1 (nth$ 1 (get-meas-group ?str))) " +
                        "(bind ?meas-2 (nth$ 2 (get-meas-group ?str)))"  +
                        "(bind ?meas-3 (nth$ 3 (get-meas-group ?str))) " +
                        "(bind ?gr-1 (nth$ 1 (get-meas-group ?gr))) " +
                        "(bind ?gr-2 (nth$ 2 (get-meas-group ?gr))) " +
                        "(bind ?gr-3 (nth$ 3 (get-meas-group ?gr)))"  +
                        "(if (and (neq (str-compare ?gr-1 ?meas-1) 0) (neq (str-compare ?gr-1 0) 0)) then (return FALSE)) " +
                        "(if (and (neq (str-compare ?gr-2 ?meas-2) 0) (neq (str-compare ?gr-2 0) 0)) then (return FALSE))"  +
                        "(if (and (neq (str-compare ?gr-3 ?meas-3) 0) (neq (str-compare ?gr-3 0) 0)) then (return FALSE)) " +
                        " (return TRUE))");

                this.engine.eval("(deffunction get-meas-group (?str)"  +
                        "(bind ?pos (str-index . ?str)) " +
                        "(bind ?gr1 (sub-string 1 (- ?pos 1) ?str)) " +
                        "(bind ?new-str (sub-string (+ ?pos 1) (str-length ?str) ?str)) " +
                        "(bind ?pos2 (str-index . ?new-str)) " +
                        "(bind ?gr2 (sub-string 1 (- ?pos2 1) ?new-str)) " +
                        "(bind ?gr3 (sub-string (+ ?pos2 1) (str-length ?new-str) ?new-str)) " +
                        "(return (create$ ?gr1 ?gr2 ?gr3)))");

            }
            catch (Exception e) {
                System.out.println("Error setting jess engine app path" + e.getMessage());
            }
            return this;
        }

        public Builder evalRequests(ArrayList<TemplateRequest> requests) {
            this.requests = requests;

            // PRINT TEMPLATES FOR DEBUG
            System.out.println("----> PRINTING TEMPLATES");
            this.requests.forEach(
                    request -> System.out.println(this.dbClient.processTemplateRequest(request).getTemplateString())
            );

            // EVALUATE TEMPLATE REQUESTS
            System.out.println("----> EVALUATING TEMPLATES");
            this.requests.forEach(
                    request -> this.dbClient.processTemplateRequest(request).evaluate(this.engine)
            );

            return this;
        }

        public JessEngine build(){

            JessEngine build = new JessEngine();
            build.engine     = this.engine;
            build.appPath    = this.appPath;
            return build;
        }

    }

    public Rete getEngine() {
        return this.engine;
    }












}
