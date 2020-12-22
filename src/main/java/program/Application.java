package program;

import algorithm.ParentFunc;
import algorithm.PopulationFunc;
import algorithm.parent.RandomParent;
import algorithm.parent.SusParent;
import algorithm.population.CrowdTourPopulation;
import algorithm.population.FudsPopulation;
import algorithm.population.ModFudsPopulation;
import func.t.F1;
import func.t.F2;
import func.t.F3;
import func.t.F4;
import node.HealthFunc;
import node.Node;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;
import plot.Plot;
import util.Stats;
import util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Application {

    public static void laba(int n, int N, double P, int CHECK_TO_STOP_ITER, int TOTAL_ITERATION, int CHILD_NUM, HealthFunc func, ParentFunc parent, PopulationFunc population, int RUN_COUNTS) throws InterruptedException, IOException, InvalidFormatException {
        long start = System.currentTimeMillis();
        for (int run = 0; run < RUN_COUNTS; run++) {
            ArrayList<Node> x = new ArrayList<>();
            for (int k = 0; k < N; k++) {
                ArrayList<Double> gens = new ArrayList<>(n);
                for (int i = 0; i < n; i++)
                    gens.add(Math.random());
                x.add(new Node(gens, func));
            }
            XYChart chart = new XYChartBuilder().width(800).height(700).title("Plot").xAxisTitle("X").yAxisTitle("Y").build();
            final SwingWrapper<XYChart> sw = Plot.scatter(chart, x, func);
            sw.displayChart();
            String label = "HealthFunc: " + func + " Parent: " + parent + " Popul: " + population + " P: " + P + " RUN_NUMBER: " + run;

            ArrayList<Node> prev = new ArrayList<>();
            double sigma = 0;
            AtomicReference<Double> min = new AtomicReference<>((double) 100);
            AtomicReference<Double> max = new AtomicReference<>((double) 0);
            x.stream().forEach(node -> {
                if (node.getHealth() > max.get())
                    max.set(node.getHealth());
                if (node.getHealth() < min.get())
                    min.set(node.getHealth());
            });
            double[] healthArr = new double[CHECK_TO_STOP_ITER];
            for (int i = 0; i < TOTAL_ITERATION; i++) {

                List<Node> parentList = parent.chooseParentForNChild(x, CHILD_NUM);

                for (Node parentToCopy : parentList) {
                    Node child = new Node(parentToCopy);
                    if (Utils.isNeedMutate(P))
                        child.mutate(Utils.getMutation(3 * sigma));
                    parentToCopy.addChild(child);
                    x.add(child);
                }
                x = population.getPopulation(x, N, min.get(), max.get());

                //if (i % CHECK_TO_STOP_ITER == 0)
                    updatePlot(chart, x, i, sw, P, label, parent.toString(), population.toString(), func.toString(), run);
                Thread.sleep(100);
                if (i % CHECK_TO_STOP_ITER == 0) {
                    for (Node node : x)
                        healthArr[i % CHECK_TO_STOP_ITER] += node.getHealth();
                    healthArr[i % CHECK_TO_STOP_ITER] /= N;
                    boolean found = false;
                    for (int di = 0; di < healthArr.length - 1; di++) {
                        if (Math.abs(healthArr[di] - healthArr[di + 1]) > 0.0001) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        updatePlot(chart, x, i, sw, P, "STOPPED " + label, parent.toString(), population.toString(), func.toString(), run);
                        break;
                    }
                }
                if (i % 60 == 0 && !prev.isEmpty())
                    sigma = Utils.sigma(prev, x);

                prev = copyNodes(x);
                x.forEach(node -> {
                    if (node.getHealth() > max.get())
                        max.set(node.getHealth());
                    if (node.getHealth() < min.get())
                        min.set(node.getHealth());
                });

                for (Node node : x)
                    healthArr[i % CHECK_TO_STOP_ITER] += node.getHealth();
                healthArr[i % CHECK_TO_STOP_ITER] /= N;
            }
            func.makeStats(x);
            ArrayList<double[]> GP = markTheGlobalPeaks(func, chart, sw);
            ArrayList<double[]> NP = markTheLocalPeaks(func, chart, sw);
            int nseeds = markPopulationPeaks(x, chart, GP, sw);
            saveEndResOfPlot(chart, P, "STOPPED " + label, parent.toString(), population.toString(), func.toString(), run, sw);
            Stats stats = new Stats(NP.size(), GP.size(), NP.size() - GP.size(), nseeds, (nseeds - NP.size()) / (double) nseeds, Node.NFE(), P);
            System.out.println(stats);
            statsToExcel(stats, run, func.toString(), parent.toString(), population.toString());
        }
        System.out.println("Time(seconds): " + (System.currentTimeMillis() - start) / 1000);
    }

    private static ArrayList<double[]> markTheGlobalPeaks(HealthFunc func, XYChart chart, SwingWrapper<XYChart> sw) {
        ArrayList<double[]> coordinates = new ArrayList<>();

        double step = 0.1 / 500;
        for (double x = 0.0; x < 1.0; x += step) {
            double y = func.health(x);
            coordinates.add(new double[]{x, y});
        }
        coordinates.sort(Comparator.comparingDouble(o -> o[1]));
        Collections.reverse(coordinates);
        ArrayList<double[]> res = new ArrayList<>();
        res.add(coordinates.get(0));
        if (coordinates.get(0)[1] == coordinates.get(1)[1]) {
            int k = 1;
            while (coordinates.get(0)[1] == coordinates.get(k)[1]) {
                res.add(coordinates.get(k++));
            }
        }
        List<Double> x = res.stream().map((doubles -> doubles[0])).collect(Collectors.toList());
        List<Double> y = res.stream().map((doubles -> doubles[1])).collect(Collectors.toList());

        //sw.repaintChart();
        SwingUtilities.invokeLater(() -> {
            XYSeries funcS = chart.addSeries("FuncPeaks", x, y);
            funcS.setMarker(new Marker() {
                final BasicStroke stroke = new BasicStroke(1.0F, 0, 2);

                @Override
                public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                    g.setStroke(this.stroke);
                    markerSize *= 2;
                    double halfSize = (double) markerSize / 2.0D;
                    Path2D.Double path = new Path2D.Double();
                    path.moveTo(xOffset - halfSize, yOffset - halfSize);
                    path.lineTo(xOffset + halfSize, yOffset + halfSize);
                    path.moveTo(xOffset - halfSize, yOffset + halfSize);
                    path.lineTo(xOffset + halfSize, yOffset - halfSize);
                    g.draw(path);
                }
            });
            funcS.setMarkerColor(Color.RED);
            sw.repaintChart();
        });

        return res;
    }

    private static ArrayList<double[]> markTheLocalPeaks(HealthFunc func, XYChart chart, SwingWrapper<XYChart> sw) {
        ArrayList<double[]> coordinates = new ArrayList<>();

        double step = 0.1 / 500;
        for (double x = 0.0; x < 1.0; x += step) {
            double y = func.health(x);
            coordinates.add(new double[]{x, y});
        }
        coordinates.sort(Comparator.comparingDouble(o -> o[0]));
        Collections.reverse(coordinates);
        ArrayList<double[]> res = new ArrayList<>();
        res.add(coordinates.get(0));
        boolean added = false;
        for (int k = 0; k < coordinates.size() - 1; k++) {
            if (coordinates.get(k)[1] > coordinates.get(k + 1)[1] && !added) {
                res.add(coordinates.get(k));
                added = true;
            } else if (coordinates.get(k)[1] < coordinates.get(k + 1)[1]) {
                added = false;
            }
        }
        List<Double> x = res.stream().map((doubles -> doubles[0])).collect(Collectors.toList());
        List<Double> y = res.stream().map((doubles -> doubles[1])).collect(Collectors.toList());

        //sw.repaintChart();
        SwingUtilities.invokeLater(() -> {
            XYSeries funcS = chart.addSeries("LocalPeaks", x, y);
            funcS.setMarker(new Marker() {
                final BasicStroke stroke = new BasicStroke(1.0F, 0, 2);

                @Override
                public void paint(Graphics2D g, double xOffset, double yOffset, int markerSize) {
                    g.setStroke(this.stroke);
                    markerSize *= 3;
                    double halfSize = (double) markerSize / 2.0D;
                    Path2D.Double path = new Path2D.Double();
                    path.moveTo(xOffset - halfSize, yOffset - halfSize);
                    path.lineTo(xOffset + halfSize, yOffset + halfSize);
                    path.moveTo(xOffset - halfSize, yOffset + halfSize);
                    path.lineTo(xOffset + halfSize, yOffset - halfSize);
                    g.draw(path);
                }
            });
            funcS.setMarkerColor(Color.RED);
            sw.repaintChart();
        });

        return res;
    }

    private static int markPopulationPeaks(ArrayList<Node> nodes, XYChart chart, ArrayList<double[]> peaks, SwingWrapper<XYChart> sw) {
        nodes.sort(Comparator.comparingDouble(n -> n.getGenList().get(0)));
        Collections.reverse(nodes);

        double eps = 0.03;
        ArrayList<ArrayList<Node>> grouped = new ArrayList<>();
        grouped.add(new ArrayList<>());
        int i = 0;
        for (int k = 0; k < nodes.size() - 1; k++) {
            Node curr = nodes.get(k);
            Node next = nodes.get(k + 1);
            if (Utils.distance(curr, next) < eps) {
                grouped.get(i).add(curr);
            } else {
                grouped.get(i).add(curr);
                grouped.add(new ArrayList<>());
                i++;
            }
        }

        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();
        grouped.forEach(list -> {
            if (!list.isEmpty()) {
                x.add(list.get(0).getGenList().get(0));
                y.add(list.get(0).getHealth());
            }
        });

        SwingUtilities.invokeLater(() -> {
            XYSeries funcS = chart.addSeries("NodesPeaks", x, y);
            funcS.setMarker(SeriesMarkers.OVAL);
            funcS.setMarkerColor(Color.magenta);
            sw.repaintChart();
        });
        return x.size();
    }

    private static void statsToExcel(Stats stats, int run, String func, String parent, String population) throws IOException {
        XSSFWorkbook workbook;
        String path = parent + "_" + population + "_" + String.valueOf(stats.getP()).replace(".", "") + ".xlsx";
        if (run == 0)
            workbook = new XSSFWorkbook();
        else
            workbook = new XSSFWorkbook(new FileInputStream(path));
        XSSFSheet sheet = workbook.getSheet(func);
        if (sheet == null)
            sheet = workbook.createSheet(func);

        if (run == 0) {
            Row row = sheet.createRow(0);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue("P");

            Cell cell1 = row.createCell(1);
            cell1.setCellValue("Parent");

            Cell cell2 = row.createCell(2);
            cell2.setCellValue("Population");

            Cell cell3 = row.createCell(3);
            cell3.setCellValue("NP");

            Cell cell4 = row.createCell(4);
            cell4.setCellValue("GP");

            Cell cell5 = row.createCell(5);
            cell5.setCellValue("NSEED");

            Cell cell6 = row.createCell(6);
            cell6.setCellValue("FPR");

            Cell cell7 = row.createCell(7);
            cell7.setCellValue("NFE");
        }
        {
            Row row = sheet.createRow(run + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue("" + stats.getP());

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(parent);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(population);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(stats.getNP());

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(stats.getGP());

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(stats.getNSEEDS());

            Cell cell6 = row.createCell(6);
            cell6.setCellValue(stats.getFPR());

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(stats.getNFE());
        }

        // if (run == 0) {
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // }
        //else workbook.close();
    }

    public static void main(String[] args) throws InterruptedException, IOException, InvalidFormatException {


        int N = 500;
        double[] P = {0.75, 0.15};
        int CHECK_TO_STOP_ITER = N * 10;
        int TOTAL_ITERATION = 20_000_000;
        int CHILD_NUM = 3;
        ParentFunc[] parentFuncArr = new ParentFunc[]{new RandomParent(), new SusParent()};
        PopulationFunc[] populationFuncArr = new PopulationFunc[]{new ModFudsPopulation(), new FudsPopulation(), new CrowdTourPopulation()};
        HealthFunc[] healthFuncArr = new HealthFunc[]{F1.getInstance(), F2.getInstance(), F3.getInstance(), F4.getInstance()};
        for (Double p : P) {
            for (ParentFunc parent : parentFuncArr) {
                for (PopulationFunc population : populationFuncArr) {
                    for (HealthFunc healthF : healthFuncArr) {
//                        new Thread(() -> {
//                            try {
                                laba(1, N, p, CHECK_TO_STOP_ITER, TOTAL_ITERATION, CHILD_NUM, healthF, parent, population, 10);
//                            } catch (InterruptedException | IOException | InvalidFormatException e) {
//                                e.printStackTrace();
//                            }
//                        }).start();
                    }
                }
            }
        }
    }

    private static void updatePlot(XYChart chart, ArrayList<Node> x, int iter, SwingWrapper<XYChart> sw, String additionalText) {
        if (x.get(0).getGenList().size() != 1)
            return;
        List<Double> xData = new ArrayList<>();
        List<Double> yData = x.stream().map(Node::getHealth).collect(Collectors.toList());
        for (Node node : x) {
            xData.add(node.getGenList().get(0));
        }
        SwingUtilities.invokeLater(() -> {
            chart.updateXYSeries("Nodes", xData, yData, null);
            if (additionalText != null)
                chart.setXAxisTitle("Iteration: " + iter + " " + additionalText);
            else chart.setXAxisTitle("Iteration: " + iter);
            sw.repaintChart();
        });
    }

    private static void updatePlot(XYChart chart, ArrayList<Node> x, int iter, SwingWrapper<XYChart> sw, double P, String additionalText, String parent, String child, String func, int iteration) {
        File parentFolder = new File(parent);
        if (!parentFolder.exists() || !parentFolder.isDirectory())
            parentFolder.mkdirs();

        File childFolder = new File(parent, child);
        if (!childFolder.exists() || !childFolder.isDirectory())
            childFolder.mkdirs();

        File runCountFolder = new File(childFolder, "" + iteration);
        if (!runCountFolder.exists() || !runCountFolder.isDirectory())
            runCountFolder.mkdirs();

        File funcFolder = new File(runCountFolder, func);
        if (!funcFolder.exists() || !funcFolder.isDirectory())
            funcFolder.mkdirs();

        File pFolder = new File(funcFolder, String.valueOf(P));
        if (!pFolder.exists() || !pFolder.isDirectory())
            pFolder.mkdirs();

        if (x.get(0).getGenList().size() != 1)
            return;
        List<Double> xData = new ArrayList<>();
        List<Double> yData = x.stream().map(Node::getHealth).collect(Collectors.toList());
        for (Node node : x) {
            xData.add(node.getGenList().get(0));
        }
        try {
            SwingUtilities.invokeLater(() -> {
                chart.updateXYSeries("Nodes", xData, yData, null);
                if (additionalText != null)
                    chart.setXAxisTitle("Iteration: " + iter + " " + additionalText);
                else chart.setXAxisTitle("Iteration: " + iter);
                sw.repaintChart();
                // Save it
                /*try {
                    BitmapEncoder.saveBitmap(chart, "./" + parent + "/" + child + "/" + iteration + "/" + func + "/" + P + "/" + "Iteration: " + iter + " " + additionalText, BitmapEncoder.BitmapFormat.PNG);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveEndResOfPlot(XYChart chart, double P, String additionalText, String parent, String child, String func, int run, SwingWrapper<XYChart> sw) {
        SwingUtilities.invokeLater(() -> {
            sw.repaintChart();
            try {
                BitmapEncoder.saveBitmap(chart, "./" + parent + "/" + child + "/" + run + "/" + func + "/" + P + "/" + "END: " + additionalText, BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static ArrayList<Node> copyNodes(ArrayList<Node> x) {
        ArrayList<Node> res = new ArrayList<>();
        for (Node node : x)
            res.add(new Node(node));
        return res;
    }
}
