package Core;

import mnisttools.MnistReader;

import java.util.Objects;

import static Core.Tool.*;


public class NeuralNetwork {
    /*
    NeuralNetwork:
    Core Algorithms de Backprobagation et Réseau de Neurone
     */

    private double[][] output; // @param output: output de chaque neurone inclue les sorties
    private double[][][] weights; // @param weights: donnée stocké dans le neurone - les pois
    private double[][] bias; // @param bias: une autre partie de pois à additioner

    // @param activiation: réaliser par le sigmoid, décider si cette neurone est important ou pas pour cette donnée
    private double[][] activiation;
    // @param derivative: derivée de fonction d'activiation
    private double[][] derivative;

    private final int[] Nb_Neurons; // @param Nb_Neurons: nombre de neurones de chaque couches
    private final int inputSize; // @param inputSize: nombre de neurones de premier couche
    private final int outputSize; // @param outputSize: nombre de neurones de dernière couche
    private final int Nb_Layers; // @param Nb_Layers: nombre de couches de network

    public double[] mauvai_scores = {99,99,99,99,99};
    // @param mauvai_scores: Les score de bien classé mais mal en logique
    public double[][] malva_justifies = new double[5][28*28];
    // @param malva_justifies: Les image de bien classé mais mal en logique
    public double[] pire_scores = {99,99,99,99,99};
    // @param pire_scores: Les score de mal classé et le pire en logique
    public double[][] pier_justifies = new double[5][28*28];
    // @param pier_justifies: Les images de mal classé et le pire en logique
    public double eta = 0.3; // @param eta: Taux d'apprendisage


    public NeuralNetwork(int... Nb_Neurons) {
        // initialiser le network de couches @param Nb_Neurons
        this.Nb_Neurons = Nb_Neurons;
        this.inputSize = Nb_Neurons[0];
        this.Nb_Layers = Nb_Neurons.length;
        this.outputSize = Nb_Neurons[Nb_Layers -1];

        this.output = new double[Nb_Layers][];
        this.weights = new double[Nb_Layers][][];
        this.bias = new double[Nb_Layers][];

        this.activiation = new double[Nb_Layers][];
        this.derivative = new double[Nb_Layers][];

        for(int i = 0; i < Nb_Layers; i++) {
            this.output[i] = new double[Nb_Neurons[i]];
            this.activiation[i] = new double[Nb_Neurons[i]];
            this.derivative[i] = new double[Nb_Neurons[i]];

            this.bias[i] = createRandomArray(Nb_Neurons[i], -0.5,0.7);

            if(i > 0) {
                weights[i] = createRandomArray(Nb_Neurons[i], Nb_Neurons[i-1], -1,1);
            }
        }
    }

    private double[] InfPerceptron(double... output) {
        // calcul la probabilté de les sortis @param output
        if(output.length != this.inputSize) return null;
        this.output[0] = output;
        for(int layer = 1; layer < Nb_Layers; layer ++) {
            for(int neuron = 0; neuron < Nb_Neurons[layer]; neuron ++) {
                double sum = bias[layer][neuron];
                // un neurone est influencé par tout neurones precédants
                for(int last = 0; last < Nb_Neurons[layer-1]; last ++) {
                    sum += this.output[layer-1][last] * weights[layer][neuron][last];
                }
                // décider l'importance de cette neurone
                // Wiki: https://en.wikipedia.org/wiki/Logistic_function
                this.output[layer][neuron] = sigmoid(sum);
                derivative[layer][neuron] = this.output[layer][neuron] * (1 - this.output[layer][neuron]);
            }
        }
        return this.output[Nb_Layers -1];
    }

    private void detector(double topProb, double secondProb, double[] data){
        // Détecter les image de bien classé mais mal en logique dans Demo 5
        if (topProb < secondProb) System.out.println("WARNING1");
        double sub = topProb - secondProb;
        for (int i = 0; i < 5 ; i++) {
            if(sub < this.mauvai_scores[i]) {
                this.mauvai_scores[i] = sub;
                this.malva_justifies[i] = data;
                return;
            }
        }
    }

    private void detector(double botProb, double[] data){
        // Détecter Les images de mal classé et le pire en logique dans Demo 6
        if(botProb > 99) System.out.println("WARNING2");
        for (int i = 0; i < 5 ; i++) {
            if(botProb < this.pire_scores[i]) {
                this.pire_scores[i] = botProb;
                this.pier_justifies[i] = data;
                return;
            }
        }
    }

    public void train(SetTool set, int loops, int batch_size) {
        // @param loops époque de train donnée de nombre @param batch_size dans @param set
        if(set.inputSize != inputSize || set.outputSize != outputSize) return;
        for(int i = 0; i < loops; i++) {
            SetTool batch = set.extractBatch(batch_size);
            for(int b = 0; b < batch_size; b++) {
                this.train(batch.getInput(b), batch.getOutput(b), eta);
            }
            System.out.println("COST-Loop " + (i+1) + ": " + COST(batch));
        }
    }

    private double COST(double[] input, double[] target) {
        // Fonction COST de entrées @param input et ses distance de @param target
        if(input.length != inputSize || target.length != outputSize) return -1;
        InfPerceptron(input);
        double v = 0;
        for(int i = 0; i < target.length; i++) {
            v += Math.pow((target[i] - output[Nb_Layers -1][i]), 2);
            // Wiki: https://en.wikipedia.org/wiki/Loss_function
        }
        return v / (2d * target.length);
    }

    public double COST(SetTool set) {
        // Fonction COST de ce set entier
        double v = 0;
        for(int i = 0; i< set.size(); i++) {
            v += COST(set.getInput(i), set.getOutput(i));
        }
        return v / set.size();
    }

    private void train(double[] input, double[] target, double eta) {
        // Une procédure de train entier de entrèes @param input et @param target en taux d'apprendisage de @param eta
        if(input.length != inputSize || target.length != outputSize) return;
        InfPerceptron(input);
        BP_Algorithms(target);
        MAJ(eta);
    }

    private void BP_Algorithms(double[] target) {
        /*
        Backprobagation Algorithms:
        Corriger les données de chaque neurones, et commence par le dernière couche au premier couche
        en utilisant ses derivées et ses sigmoid pour voir si cette neurone est important pour cette donnée ou pas.
         */
        for(int neuron = 0; neuron < Nb_Neurons[Nb_Layers -1]; neuron ++) {
            activiation[Nb_Layers -1][neuron] = (output[Nb_Layers -1][neuron] - target[neuron])
                    * derivative[Nb_Layers -1][neuron];
            // Wiki: https://en.wikipedia.org/wiki/Backpropagation
            // output_derivative -> activation function
        }
        for(int layer = Nb_Layers -2; layer > 0; layer --) {
            for(int neuron = 0; neuron < Nb_Neurons[layer]; neuron ++){
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < Nb_Neurons[layer+1]; nextNeuron ++) {
                    sum += weights[layer + 1][nextNeuron][neuron] * activiation[layer + 1][nextNeuron];
                }
                this.activiation[layer][neuron] = sum * derivative[layer][neuron];
            }
        }
    }

    private void MAJ(double eta) {
        // Même chose pour mttre à jour les pois dans TP, en utilisant les resultat de BP_Algorithms
        for(int layer = 1; layer < Nb_Layers; layer++) {
            for(int neuron = 0; neuron < Nb_Neurons[layer]; neuron++) {
                double delta = - eta * activiation[layer][neuron];
                bias[layer][neuron] += delta;
                for(int prevNeuron = 0; prevNeuron < Nb_Neurons[layer-1]; prevNeuron ++) {
                    weights[layer][neuron][prevNeuron] += delta * output[layer-1][prevNeuron];
                }
            }
        }
    }

    private double sigmoid( double x) {
        // Fonction sigmoid @param x - Fonction d'activiation
        return 1d / ( 1 + Math.exp(-x));
    }

    private static double[] BinariserImage(int[][] image) {
        // Binariser les images @param image
        double[] res = new double[28*28];
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                if (image[i][j] > 100) {
                    res[i * 28 + j] = 1;
                } else {
                    res[i * 28 + j] = 0;
                }
            }
        }
        return res;
    }

    private static double[] OneHot(int etiquette) {
        // Créer un vecteur de longeur de 10 {0,...,1,...,0} que 1 est à index @param etiquette
        double[] res = new double[10];
        for (int i = 0; i < 10; i++) {
            res[i] = 0;
        }
        res[etiquette] = 1;
        return res;
    }

    private static double[] OneHot_letter(int etiquette, int size) {
        // Créer un vecteur de longeur de @param size {0,...,1,...,0} que 1 est à index @param etiquette
        double[] res = new double[size];
        res[etiquette-10] = 1d;
        return res;
    }
    public static double Validation(NeuralNetwork net, SetTool set) {
        // Valiser le taux de correct pour le réseau @param net pour les données de @param set
        int correct = 0;
        for(int i = 0; i < set.size(); i++) {
            double[] x = net.InfPerceptron(set.getInput(i));
            assert x != null;
            double truth = MAX(x);
            double actual = MAX(set.getOutput(i));
            if(truth == actual) {
                correct ++ ;
                net.detector(x[(int)truth], x[SEC(x)], set.getInput(i));
            } else {
                double[] k = set.getOutput(i);
                int temp = 0;
                for (int j = 0; j < k.length ; j++) {
                    if(k[j] == 1){
                        temp = j;
                        break;
                    }
                }
                net.detector(x[temp], set.getInput(i));
            }
        }
        System.out.println("Testing finished, RESULT: " + correct + " / " + set.size()+ "  -> " + 100 * (double)correct / (double)set.size() +" %");
        return 1 - (double)correct / (double)set.size();
    }

    public static int[][] Matrix_Confusion(NeuralNetwork net, SetTool set) {
        // Créer la matrice de confusion pour le réseau @param net pour les données de @param set
        int[][] res =new int[22][22];
        for(int i = 0; i < set.size(); i++) {
            int highest = MAX(Objects.requireNonNull(net.InfPerceptron(set.getInput(i))));
            int actualHighest = MAX(set.getOutput(i));
            res[highest][actualHighest] ++;
        }
        return res;
    }

    // Pour les données de digits
    public static void Creat_train_set_digit(SetTool set, String imageDB_Train, String labelDB_Train){
        // Créer un set de donnée de digit à train pour @param set auprès de base de donnée @param imageDB_Train
        MnistReader db = new MnistReader(labelDB_Train, imageDB_Train);
        for (int i = 1; i < set.Na+1 ; i++) {
            int temp = db.getLabel(i);
            set.addData(BinariserImage(db.getImage(i)), OneHot(temp),i);
        }
    }

    public static void Creat_valid_set_digit(SetTool set, String imageDB_Train, String labelDB_Train){
        // Créer un set de donnée de digit à valider pour @param set auprès de base de donnée @param imageDB_Train
        MnistReader db = new MnistReader(labelDB_Train, imageDB_Train);
        for (int i = 1+set.Na ; i < set.Na+1+set.Nv ; i++) {
            int temp = db.getLabel(i);
            set.addData(BinariserImage(db.getImage(i)), OneHot(temp),i);
        }
    }

    public static void Creat_test_set_digit(SetTool set, String imageDB_Train, String labelDB_Train){
        // Créer un set de donnée de digit à tester pour @param set auprès de base de donnée @param imageDB_Train
        MnistReader db = new MnistReader(labelDB_Train, imageDB_Train);
        for (int i = set.Na+1+set.Nv ; i < set.Na+1+set.Nv+set.Nt ; i++) {
            int temp = db.getLabel(i);
            set.addData(BinariserImage(db.getImage(i)), OneHot(temp),i);
        }
    }

    public static void Creat_train_set_letter(SetTool set, String imageDB_Train, String labelDB_Train, int size){
        // Créer un set de donnée de alphabets à train pour @param set auprès de base de donnée @param imageDB_Train
        MnistReader db = new MnistReader(labelDB_Train, imageDB_Train);
        System.out.println(db.getTotalImages());
        System.out.println("Loading train set...");
        int index = 1;
        for (int i = 1; i < set.Na+1 ; i++) {
            int temp = db.getLabel(index);
            while(temp > 9+size || temp < 10) {
                index ++;
                temp = db.getLabel(index);
            }
            set.addData(BinariserImage(db.getImage(index)), OneHot_letter(temp, size),index);
            index++;
        }
    }

    public static void Creat_valid_set_letter(SetTool set, String imageDB_Train, String labelDB_Train, int size){
        // Créer un set de donnée de alphabets à valider pour @param set auprès de base de donnée @param imageDB_Train
        MnistReader db = new MnistReader(labelDB_Train, imageDB_Train);
        System.out.println(db.getTotalImages());
        System.out.println("Loading valid set...");
        int index = 1+set.Na;
        for (int i = 1+set.Na ; i < set.Na+1+set.Nv ; i++) {
            int temp = db.getLabel(index);
            while(temp > 9+size || temp < 10) {
                index ++;
                temp = db.getLabel(index);
            }
            set.addData(BinariserImage(db.getImage(index)), OneHot_letter(temp,size),index);
            index++;
        }
    }

    public static void Creat_test_set_letter(SetTool set, String imageDB_Train, String labelDB_Train, int size){
        // Créer un set de donnée de alphabets à tester pour @param set auprès de base de donnée @param imageDB_Train
        MnistReader db = new MnistReader(labelDB_Train, imageDB_Train);
        int index = set.Na+1+set.Nv;
        for (int i = set.Na+1+set.Nv ; i < set.Na+1+set.Nv+set.Nt ; i++) {
            int temp = db.getLabel(index);
            while(temp > 9+size || temp < 10) {
                index ++;
                temp = db.getLabel(index);
            }
            set.addData(BinariserImage(db.getImage(index)), OneHot_letter(temp, size),index);
            index++;
        }
    }


    public static void main(String[] args){
        // Tester
        String imageDB_Train = "Data/emnist-byclass-train-images-idx3-ubyte";
        String labelDB_Train = "Data/emnist-byclass-train-labels-idx1-ubyte";

        // Initialiser le Neuron Network
        NeuralNetwork net = new NeuralNetwork(784,100,50,12);

        // Initialiser les données de Train
        SetTool train_set = new SetTool(784, 12);
        train_set.Na = 5000;
        net.eta = 0.3;
        NeuralNetwork.Creat_train_set_letter(train_set, imageDB_Train, labelDB_Train, 12);

        // Train
        for (int i = 0; i < 100 ; i++) {
            System.out.println(i);
            net.train(train_set, 50, 100);
        }

        // COST
        System.out.println("COST: " + net.COST(train_set));

        //Validation
        SetTool test_set = new SetTool(784, 12);
        NeuralNetwork.Creat_valid_set_letter(test_set,imageDB_Train,labelDB_Train , 12);
        NeuralNetwork.Validation(net, test_set);
    }
}
