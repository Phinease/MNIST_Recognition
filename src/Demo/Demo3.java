package Demo;

import Core.SetTool;
import Core.NeuralNetwork;

public class Demo3 {
    public static void main(String[] Args) {
        String imageDB_Train = "Data/emnist-byclass-train-images-idx3-ubyte";
        String labelDB_Train = "Data/emnist-byclass-train-labels-idx1-ubyte";

        int[][] matrix_confusion;
        int input_size = 784;
        int output_size = 12;
        
        // Initialiser les données de Train
        SetTool train_set = new SetTool(input_size, output_size);
        train_set.Na = 5000;
        NeuralNetwork.Creat_train_set_letter(train_set, imageDB_Train, labelDB_Train,output_size);

        SetTool valid_set = new SetTool(input_size, output_size);
        NeuralNetwork.Creat_valid_set_letter(valid_set,imageDB_Train,labelDB_Train,output_size);

        // Initialiser le Neuron Network
        NeuralNetwork net = new NeuralNetwork(input_size,100,50,output_size);

        // Train
        net.eta = 0.6;
        for (int i = 0; i < 10 ; i++) {
            net.train(train_set, 5, 1000);
        }
        // COST
        System.out.println("COST: " + net.COST(train_set));

        //Validation
        matrix_confusion = NeuralNetwork.Matrix_Confusion(net, valid_set);

        //Matrice de Confusion
        int max = 0;
        int[] res = new int[3];
        String alphabet = "ABCDEFGHIJKL";
        String[] list_alphabet = alphabet.split("");
        System.out.print("\t");
        for (int i = 0; i < 12 ; i++) {
            System.out.print(list_alphabet[i] + "\t");
        }
        System.out.println();
        for (int i = 0; i < 12 ; i++) {
            System.out.print(list_alphabet[i] + "\t");
            for (int j = 0; j < 12 ; j++) {
                System.out.print(matrix_confusion[i][j] + "\t");
                if(i != j && matrix_confusion[i][j] > max){
                    max = matrix_confusion[i][j];
                    res[0] = max;
                    res[1] = i;
                    res[2] = j;
                }
            }
            System.out.println();
        }

        System.out.println("les classes les plus problématiques est de classer "
                + list_alphabet[res[1]] + " à " + list_alphabet[res[2]] + " "+ res[0] + " fois.");
    }
}
