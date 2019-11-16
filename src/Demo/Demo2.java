package Demo;

import Core.SetTool;
import Core.NeuralNetwork;
import Core.Illustration;

import java.util.ArrayList;

public class Demo2 {
    public static void main(String[] Args) {
        String imageDB_Train = "Data/emnist-byclass-train-images-idx3-ubyte";
        String labelDB_Train = "Data/emnist-byclass-train-labels-idx1-ubyte";

        int sample = 100;
        double[] res = new double[sample];
        ArrayList<Double> X = new ArrayList<>();
        ArrayList<Double> Y = new ArrayList<>();
        NeuralNetwork best = null;

        int input_size = 784;
        int output_size = 12;
        
        // Initialiser les données
        SetTool train_set = new SetTool(input_size, output_size);
        train_set.Na = 10000;
        NeuralNetwork.Creat_train_set_letter(train_set, imageDB_Train, labelDB_Train,output_size);
        SetTool valid_set = new SetTool(input_size, output_size);
        valid_set.Nv = 1000;
        NeuralNetwork.Creat_valid_set_letter(valid_set,imageDB_Train,labelDB_Train,output_size);
        SetTool test_set = new SetTool(input_size, output_size);
        test_set.Nt = 1000;
        NeuralNetwork.Creat_test_set_letter(test_set,imageDB_Train,labelDB_Train,output_size);
        double max = 0;
        for (int i = 1; i < sample  ; i++) {
            // Initialiser le Neuron Network
            NeuralNetwork net = new NeuralNetwork(input_size,100,50,output_size);

            // Train
            net.eta = 0.05*i;
            net.train(train_set, 10, 1000);
            // COST
            System.out.println("COST " + i +": " + net.COST(train_set));

            //Validation
            res[i-1] = NeuralNetwork.Validation(net, valid_set);
            if(res[i-1]>max) {
                best = net;
                max = res[i-1];
                System.out.println("BEST FOUND");
            }
            X.add(net.eta);
            Y.add(res[i-1]);
        }
        double res_test = NeuralNetwork.Validation(best, test_set);
        System.out.println("Test result: " + res_test + " %");
        assert best != null;
        System.out.println("Best taux d'Apprentisage: " + best.eta);
        Illustration image = new Illustration("Demo2","Eta","Taux d'erreur");
        image.Add_Collection("Na - 10000", X, Y);
        image.plot();
        // image.saveAsFile("./Image/demo2.png",600,600);
    }

}
