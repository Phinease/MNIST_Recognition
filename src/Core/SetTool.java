package Core;

import java.util.ArrayList;
import java.util.Arrays;

import static Core.Tool.randomValues;

public class SetTool {
    /*
    SetTool:
    Les outils utilisé pour manipuler les sets de données
     */

    final int inputSize; // @param inputSize: Le longeur d'entrèe
    final int outputSize; // @param outputSize: Le longeur de sorties

    public int Na = 1000; // @param Na: Nombre de données à train
    public int Nv = 1000; // @param Nv: Nombre de données à valider
    public int Nt = 1000; // @param Nt: Nombre de données à tester

    private ArrayList<double[][]> data = new ArrayList<>();
    // @param data: donnée d'image de format double[][]
    private ArrayList<Integer> data_index = new ArrayList<>();
    // @param data_index: index d'image

    public SetTool(int inputSize, int OUTPUT_SIZE) {
        this.inputSize = inputSize;
        this.outputSize = OUTPUT_SIZE;
    }

    void addData(double[] in, double[] expected, int index) {
        // Ajouter un donnée @param in et son target @param expected
        if(in.length != inputSize || expected.length != outputSize) return;
        data.add(new double[][]{in, expected});
        data_index.add(index);
    }

    SetTool extractBatch(int size) {
        // Créer une batch de longeur @param size à faire le train par Random
        if(size > 0 && size <= this.size()) {
            SetTool set = new SetTool(inputSize, outputSize);
            Integer[] ids = randomValues(0,this.size() - 1, size);
            assert ids != null;
            for(Integer i:ids) {
                set.addData(this.getInput(i),this.getOutput(i),this.getIndex(i));
            }
            return set;
        }else return this;
    }

    public String toString() {
        // Créer un String pour préciser ce set
        StringBuilder s = new StringBuilder("Set [" + inputSize + " ; " + outputSize + "]\n");
        int index = 0;
        for(double[][] r:data) {
            s.append(index).append(":   ").append(Arrays.toString(r[0])).append("  ---  ").append(Arrays.toString(r[1])).append("\n");
            index++;
        }
        return s.toString();
    }

    int size() {
        // Longeur de donnée
        return data.size();
    }

    double[] getInput(int index) {
        // Obtenir certain @param index entrée de donnée
        if(index >= 0 && index < size())
            return data.get(index)[0];
        else return null;
    }

    double[] getOutput(int index) {
        // Obtenir certain @param index sortie de donnée
        if(index >= 0 && index < size())
            return data.get(index)[1];
        else return null;
    }

    private int getIndex(int index){
        // Obtenir certain @param index index de donnée quand il est ajouté
        if(index >= 0 && index < size())
            return data_index.get(index);
        else return Integer.parseInt(null);
    }
}
