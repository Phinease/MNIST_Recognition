package Demo;

import Core.SetTool;
import Core.NeuralNetwork;
import Core.Image;

public class Demo6 {
    public static void main(String[] Args) {
        String imageDB_Train = "Data/emnist-byclass-train-images-idx3-ubyte";
        String labelDB_Train = "Data/emnist-byclass-train-labels-idx1-ubyte";

        int input_size = 784;
        int output_size = 12;

        // Initialiser les données
        SetTool train_set = new SetTool(input_size, output_size);
        train_set.Na = 10000;
        NeuralNetwork.Creat_train_set_letter(train_set, imageDB_Train, labelDB_Train,output_size);
        SetTool valid_set = new SetTool(input_size, output_size);
        valid_set.Nv = 1000;
        NeuralNetwork.Creat_valid_set_letter(valid_set,imageDB_Train,labelDB_Train,output_size);
        NeuralNetwork net = new NeuralNetwork(input_size,100,50,output_size);
        // Train
        net.eta = 0.6;
        net.train(train_set, 10, 1000);

        // COST
        System.out.println("COST: " + net.COST(train_set));

        //Validation
        NeuralNetwork.Validation(net, valid_set);

        System.out.println("Probabilité de mauvais classé ou mal classé: ");
        System.out.println("(Le moin le pire)");

        for (int i = 0; i < 5 ; i++) {
            System.out.println("Image " + (i+1) + ": " + net.pire_scores[i]);
            Image.displayImage("Demo6", Image.creat_image(net.pier_justifies[i]));
        }
    }
}
