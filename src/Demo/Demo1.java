package Demo;

import Core.SetTool;
import Core.NeuralNetwork;
import Core.Illustration;

import java.util.ArrayList;

public class Demo1 {

    public static void main(String[] Args) {
        String imageDB_Train = "Data/emnist-byclass-train-images-idx3-ubyte";
        String labelDB_Train = "Data/emnist-byclass-train-labels-idx1-ubyte";

        int sample = 20;
        double[] res = new double[sample];
        ArrayList<Double> X = new ArrayList<>();
        ArrayList<Double> Y = new ArrayList<>();
        int input_size = 784;
        int output_size = 12;

        // Initialiser les données
        SetTool train_set = new SetTool(input_size, output_size);
        train_set.Na = 10000;
        NeuralNetwork.Creat_train_set_letter(train_set, imageDB_Train, labelDB_Train,output_size);
        SetTool test_set = new SetTool(input_size, output_size);
        test_set.Nv = 1000;
        NeuralNetwork.Creat_valid_set_letter(test_set, imageDB_Train, labelDB_Train,output_size);

        // Initialiser le Neuron Network
        NeuralNetwork net = new NeuralNetwork(input_size,100,50,output_size);
        for (int i = 1; i < sample ; i++) {
            // Train
            net.eta = 0.3;
            net.train(train_set, i+1, 500);

            // COST
            System.out.println("COST: " + net.COST(train_set));

            //Validation
            res[i-1] = NeuralNetwork.Validation(net, test_set);
            X.add((double) ((i)*500));
            Y.add(res[i-1]);
        }
        Illustration image = new Illustration("Demo1: taux d'erreur en fonction de Na","Na","Taux d'erreur");
        image.Add_Collection("Taux d'Apprentisage - " + net.eta, X, Y);
        image.plot();
        image.saveAsFile("./Image/demo1.png",600,600);
    }

}
