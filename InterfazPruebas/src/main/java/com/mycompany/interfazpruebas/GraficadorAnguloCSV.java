/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GraficadorAnguloCSV extends JFrame {
    private final XYSeries serie;
    private final ArrayList<Double> historialAngulo;
    private final ChartPanel chartPanel;
    private int tiempo = 0;

    public GraficadorAnguloCSV() {
        super("Gráfica del Ángulo del Codo");
        this.historialAngulo = new ArrayList<>();
        this.serie = new XYSeries("Angulo (deg)");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Ángulo del codo vs Tiempo",
                "Tiempo (ms)",
                "Angulo (°)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);
        chartPanel = new ChartPanel(chart); // 👈 Inicializar el campo
        
        ChartPanel Chart = new ChartPanel(chart);
        Chart.setPreferredSize(new Dimension(700, 400));
        setContentPane(Chart);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public void agregarDato(double angulo) {
        historialAngulo.add(angulo);
        serie.add(tiempo, angulo);
        tiempo += 100; // 100ms entre muestras
    }

    public void guardarCSV(String ruta) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ruta))) {
            writer.println("Tiempo(ms),Angulo(deg)");
            int t = 0;
            for (Double angulo : historialAngulo) {
                writer.printf("%d,%.4f%n", t, angulo);
                t += 100;
            }
        }
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
