package vassar.problem;

import com.evaluator.AggregationRuleQuery;
import com.evaluator.OrbitInformationQuery;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import vassar.database.template.TemplateRequest;
import vassar.database.template.request.CapabilityRuleTemplateRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// BASE PARAMS EQUIVALENT
public class Problem {

    public int MAX_TOTAL_INSTR;
    public int numInstr;
    public int numOrbits;
    public int numPanels;
    public String requestMode;
    public String orekitResourcesPath;
    public String[] orbitList;
    public String[] instrumentList;
    public ArrayList<Integer> numObjectivesPerPanel;
    public ArrayList<ArrayList<ArrayList<Double>>> subobjWeights;
    public ArrayList<ArrayList<ArrayList<String>>> subobjectives;
    protected HashMap<String, Integer> instrumentIndexes;
    protected HashMap<String, Integer> orbitIndexes;
    public HashMap<String, HashMap<String, Double>> revtimes;
    public HashMap<String, String>            subobjectivesToMeasurements;
    public HashMap<String, ArrayList<String>> measurementsToSubobjectives;
    public HashMap<String, ArrayList<String>> instrumentsToMeasurements;
    public HashMap<String, ArrayList<String>> measurementsToInstruments;

    public static class Builder<T extends Builder<T>>{

        public ArrayList<Integer> numObjectivesPerPanel;
        public ArrayList<ArrayList<ArrayList<Double>>> subobjWeights;
        public ArrayList<ArrayList<ArrayList<String>>> subobjectives;

        public String[] orbitList;
        public String[] instrumentList;
        public String   requestMode;
        public int      numPanels;

        public HashMap<String, String>            subobjectivesToMeasurements;
        public HashMap<String, ArrayList<String>> measurementsToSubobjectives;
        public HashMap<String, ArrayList<String>> instrumentsToMeasurements;
        public HashMap<String, ArrayList<String>> measurementsToInstruments;


        public Builder() {
            this.instrumentList = new String[] {};
            this.orbitList      = new String[] {};
            this.numObjectivesPerPanel = new ArrayList<>();
            this.subobjWeights = new ArrayList<>();
            this.subobjectives = new ArrayList<>();
            subobjectivesToMeasurements = new HashMap<>();
            measurementsToSubobjectives = new HashMap<>();
            instrumentsToMeasurements = new HashMap<>();
            measurementsToInstruments = new HashMap<>();
        }

        public T setInstrumentList(String[] instrumentList){
            this.instrumentList = instrumentList;
            return (T) this;
        }

        public T setOrbitList(List<OrbitInformationQuery.Item> items){
            // BUILD PROBLEM
            String[] orbitList = new String[items.size()];
            for(int x=0; x < items.size(); x++){
                orbitList[x] = items.get(x).name();
            }



            this.orbitList = new String[]{"LEO-600-polar-NA", "SSO-600-SSO-AM", "SSO-600-SSO-DD", "SSO-800-SSO-AM", "SSO-800-SSO-DD"};
            return (T) this;
        }

        public T setInstrumentMeasurementData(ArrayList<CapabilityRuleTemplateRequest.CapabilityRules.Instrument> instruments){
            for(CapabilityRuleTemplateRequest.CapabilityRules.Instrument instrument : instruments) {
                ArrayList<String> measurement_list = new ArrayList<>();
                for(CapabilityRuleTemplateRequest.CapabilityRules.Measurement measurement : instrument.measurements) {
                    measurement_list.add('"'+measurement.name+'"');
                }
                instrumentsToMeasurements.put(instrument.name, measurement_list);
            }
            measurementsToInstruments = this.getInverseHashMapSALToSAL(instrumentsToMeasurements);
            return (T) this;
        }

        private HashMap<String, ArrayList<String>> getInverseHashMapSALToSAL(HashMap<String, ArrayList<String>> hm) {
            HashMap<String, ArrayList<String>> inverse = new HashMap<>();
            for (Map.Entry<String, ArrayList<String>> entr: hm.entrySet()) {
                String key = entr.getKey();
                ArrayList<String> vals = entr.getValue();
                for (String val: vals) {
                    if (inverse.containsKey(val)) {
                        ArrayList<String> list = inverse.get(val);
                        if (!list.contains(key)) {
                            list.add(key);
                            inverse.put(val, list);
                        }
                    }
                    else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(key);
                        inverse.put(val, list);
                    }
                }
            }
            return inverse;
        }

        public T setAggregationRules(List<AggregationRuleQuery.Item> rules){
            this.numPanels = rules.size();
            for (AggregationRuleQuery.Item panel : rules) {
                this.numObjectivesPerPanel.add(panel.objectives().size());

                ArrayList<ArrayList<Double>> subobjWeights2 = new ArrayList<>();
                ArrayList<ArrayList<String>> subobjectives2 = new ArrayList<>();
                for (AggregationRuleQuery.Objective objective : panel.objectives()){

                    ArrayList<Double> subobjWeights3 = new ArrayList<>();
                    ArrayList<String> subobjectives3 = new ArrayList<>();
                    for (AggregationRuleQuery.Subobjective subobjective : objective.subobjectives()){
                        subobjWeights3.add(((BigDecimal) subobjective.weight()).doubleValue());
                        subobjectives3.add(subobjective.name());
                    }
                    subobjWeights2.add(subobjWeights3);
                    subobjectives2.add(subobjectives3);
                }
                subobjWeights.add(subobjWeights2);
                subobjectives.add(subobjectives2);
            }
            return (T) this;
        }

        public T setRequestMode(String requestMode){
            this.requestMode = requestMode;
            return (T) this;
        }

        public T template() {
            return (T) this;
        }

        public Problem build() { return new Problem(this);}
    }

    protected Problem(Builder<?> builder){

        this.requestMode           = builder.requestMode;
        this.numObjectivesPerPanel = builder.numObjectivesPerPanel;
        this.subobjWeights         = builder.subobjWeights;
        this.subobjectivesToMeasurements = builder.subobjectivesToMeasurements;
        this.measurementsToSubobjectives = builder.measurementsToSubobjectives;
        this.instrumentsToMeasurements = builder.instrumentsToMeasurements;
        this.measurementsToInstruments = builder.measurementsToInstruments;

        this.instrumentList  = builder.instrumentList;
        this.orbitList       = builder.orbitList;
        this.numInstr        = builder.instrumentList.length;
        this.numOrbits       = builder.orbitList.length;
        this.MAX_TOTAL_INSTR = this.numOrbits * this.numInstr;

        this.instrumentIndexes = new HashMap<>();
        this.orbitIndexes      = new HashMap<>();

        for (int i = 0; i < numInstr; i++) {
            instrumentIndexes.put(instrumentList[i], i);
        }
        for (int i = 0; i < numOrbits; i++) {
            orbitIndexes.put(orbitList[i], i);
        }

        orekitResourcesPath = "/home/gabe/repos/seakers/design_evaluator/app/src/main/java/vassar/coverage";

        try {
            FileInputStream fis = new FileInputStream("/home/gabe/repos/seakers/design_evaluator/app/problems/smap/dat/revtimes.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.revtimes = (HashMap<String, HashMap<String, Double>>) ois.readObject();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public String[] getOrbitList(){
        return this.orbitList;
    }
    public String[] getInstrumentList(){
        return this.instrumentList;
    }
    public int getNumOrbits(){
        return this.numOrbits;
    }
    public int getNumInstr(){
        return this.numInstr;
    }
    public HashMap<String, Integer> getOrbitIndexes() {
        return orbitIndexes;
    }
    public HashMap<String, Integer> getInstrumentIndexes() {
        return instrumentIndexes;
    }

    public String getRequestMode(){ return requestMode; }


}
