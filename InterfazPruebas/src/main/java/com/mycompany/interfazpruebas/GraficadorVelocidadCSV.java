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

public class GraficadorVelocidadCSV extends JFrame {
    private final XYSeries serie;
    private final ArrayList<Double> historial;
    private final ChartPanel chartPanel;
    private int tiempo = 0;

    public GraficadorVelocidadCSV() {
        super("Gráfica de Velocidad Angular del Codo");
        this.historial = new ArrayList<>();
        this.serie = new XYSeries("Velocidad Angular (rad/s)");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Velocidad Angular del Codo vs Tiempo",
                "Tiempo (ms)",
                "Velocidad (rad/s)",
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

     public ChartPanel getChartPanel(ChartPanel chartPanel) { return chartPanel; }
    
    public void agregarDato(double valor) {
        historial.add(valor);
        serie.add(tiempo, valor);
        tiempo += 100; // 100 ms entre muestras
    }

    public void guardarCSV(String ruta) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ruta))) {
            writer.println("Tiempo(ms),Velocidad(rad/s)");
            int t = 0;
            for (Double v : historial) {
                writer.printf("%d,%.6f%n", t, v);
                t += 100;
            }
        }
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
