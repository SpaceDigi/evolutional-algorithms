package plot;

import node.HealthFunc;
import node.Node;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Theme;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Plot {

    public static SwingWrapper<XYChart> scatter(XYChart chart, ArrayList<Node> nodeList, HealthFunc func) {
        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(10);
        // Series
        List<Double> xData = new LinkedList<>();
        List<Double> yData = new LinkedList<>();
        for (Node node : nodeList) {
            xData.add(node.getGenList().get(0));
            yData.add(node.getHealth());
        }
        List<Double> x = new LinkedList<>();
        List<Double> y = new LinkedList<>();
        double step = 0.1 / nodeList.size();
        for (double k = 0.0; k < 1.0; k += step) {
            x.add(k);
            y.add(func.health(k));
        }

        // Series
        XYSeries funcS = chart.addSeries("Func", x, y);
        funcS.setMarker(SeriesMarkers.CIRCLE);

        XYSeries s = chart.addSeries("Nodes", xData, yData);
        s.setMarker(new Marker() {

            final BasicStroke stroke = new BasicStroke(1.0F, 0, 2);

            public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                g.setStroke(this.stroke);
                markerSize *= 1.3;
                double halfSize = (double) markerSize / 2.0D;
                Shape circle = new Ellipse2D.Double(xOffset - halfSize, yOffset - halfSize, (double) markerSize, (double) markerSize);
                g.fill(circle);
            }
        });


        return new SwingWrapper<>(chart);
    }

    public static void scatter(HealthFunc func, int N) throws IOException {
        double step = 1.0 / N;
        List<Double> xData = new LinkedList<>();
        List<Double> yData = new LinkedList<>();

        List<Double> xData1 = new LinkedList<>();
        List<Double> yData1 = new LinkedList<>();
        for (double k = 0.0; k < 1.0; k += step) {
            xData.add(k);
            yData.add(func.health(k));
            double q = Math.random();
            xData1.add(q);
            yData1.add(func.health(q));
        }
        XYChart chart = new XYChartBuilder().width(600).height(500).title("Plot").xAxisTitle("X").yAxisTitle("Y").build();
        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(8);
        chart.addSeries("Gaussian Blob 1", xData, yData);
        XYSeries s = chart.addSeries("Gaussian Blob ", xData1, yData1);
        s.setMarker(SeriesMarkers.SQUARE);
        // Show it
        new SwingWrapper(chart).displayChart();
        // Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

        // BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);
    }

}
