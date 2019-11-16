package Core;

import java.util.Random;

class Tool {
    /*
    Tool:
    Des outils générals pour manipuler les SetTool
     */

    // @param generator: On utilise des Random
    private static Random generator = new Random(1);

    static double[] createRandomArray(int size, double botLimit, double topLimit){
        // Créer un Array double[] aléatoire de longeur @param size et bornnée [@param botLimit, @param topLimit]
        if(size < 1){ return null; }
        double[] res = new double[size];
        for(int i = 0; i < size; i++){
            res[i] = randomValue(botLimit,topLimit);
        }
        return res;
    }

    static double[][] createRandomArray(int sizeX, int sizeY, double botLimit, double topLimit){
        // Créer un Array double[][] aléatoire de longeur @param sizeX(Y) et bornnée [@param botLimit, @param topLimit]
        if(sizeX < 1 || sizeY < 1){ return null; }
        double[][] res = new double[sizeX][sizeY];
        for(int i = 0; i < sizeX; i++){
            res[i] = createRandomArray(sizeY, botLimit, topLimit);
        }
        return res;
    }

    private static double randomValue(double botLimit, double topLimit){
        // Une double aléatoir
        return generator.nextFloat()*(topLimit - botLimit) + botLimit;
    }

    static Integer[] randomValues(int botLimite, int topLimite, int size) {
        // Créer des valeurs aléatoire de longeur @param size et bornnée [@param botLimit, @param topLimit]
        botLimite --;
        if(size > (topLimite-botLimite)){
            return null;
        }
        Integer[] values = new Integer[size];
        for(int i = 0; i< size; i++){
            int n = (int)(generator.nextFloat() * (topLimite-botLimite+1) + botLimite);
            while(containsValue(values, n)){
                n = (int)(generator.nextFloat() * (topLimite-botLimite+1) + botLimite);
            }
            values[i] = n;
        }
        return values;
    }

    private static <T extends Comparable<T>> boolean containsValue(T[] res, T value){
        // Vérifier si un donnée @param value est conpris dans un set @param res
        for (T re : res) {
            if (re != null) {
                if (value.compareTo(re) == 0) { return true; }
            }
        }
        return false;
    }

    static int MAX(double[] values){
        // Trouve la valeur maximal d'un set @param values
        int index = 0;
        for(int i = 1; i < values.length; i++){
            if(values[i] > values[index]){
                index = i;
            }
        }
        return index;
    }

    static int SEC(double[] values){
        // Trouve la valeur second maximal d'un set @param values
        int max_index = MAX(values);
        values[max_index] = 0;
        return MAX(values);
    }
}
