package Core;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Illustration {
    /*
    Illustration：
    Pour illuster les charts.
        @param title: le titre d'image
        @param X_title： le titre d'axis X
        @param Y_title：le titre d'axis Y
     */
    private static String mtitle;
    private static String mX_title;
    private static String mY_title;
    private static XYSeriesCollection mCollection;
    private static JFreeChart mChart;

    public Illustration(String title, String X_title, String Y_title){
        mtitle = title;
        mX_title = X_title;
        mY_title = Y_title;
        mCollection = new XYSeriesCollection();
    }


    public void plot(){
        //Dessiner cet image
        mChart = ChartFactory.createXYLineChart(
                mtitle,
                mX_title,
                mY_title,
                mCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        XYPlot xyPlot = mChart.getXYPlot();
        ValueAxis rangeAxis = xyPlot.getRangeAxis();
        rangeAxis.setRange(0.0, 1.0);
        ChartFrame mChartFrame = new ChartFrame(mtitle, mChart);
        mChartFrame.pack();
        mChartFrame.setVisible(true);
    }

    public void Add_Collection(String key, ArrayList<Double> X, ArrayList<Double> Y){
        // Ajouter une trace
        XYSeries mSeries = new XYSeries(key);
        if (X.size() != Y.size()) {
            System.out.println("Error 1");
            return;
        }
        for (int i = 0; i < X.size(); i++) {
            mSeries.add(X.get(i), Y.get(i));
        }
        mCollection.addSeries(mSeries);
    }

    public void saveAsFile(String outputPath, int weight, int height) {
        // Sauvegarter cet image
        FileOutputStream out = null;
        try {
            File outFile = new File(outputPath);
            if (!outFile.getParentFile().exists()) {
                boolean mkdirs = outFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(outputPath);
            ChartUtilities.writeChartAsPNG(out, mChart, weight, height);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }



    public static void main(String[] args){
        // Test
        ArrayList<Double> X = new ArrayList<>();
        ArrayList<Double> Y = new ArrayList<>();
        for (int Na = 1000;  Na < 10001 ; Na += 1000) {
            int[] res = {Na, Na/100};
            X.add((double) res[0]);
            Y.add((double) res[1]);
        }
        Illustration image = new Illustration("Demo1","Na","Taux de Correct");
        image.Add_Collection("Taux d'Apprendisage - ", X, Y);
        image.plot();
        image.saveAsFile("./image/test2.png",600,300);
    }
}