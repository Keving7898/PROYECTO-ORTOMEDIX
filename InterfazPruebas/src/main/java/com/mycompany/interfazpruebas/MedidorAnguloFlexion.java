/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;

import org.apache.commons.math3.linear.*;
import java.io.*;
import java.util.*;


public class MedidorAnguloFlexion {

    private final RealMatrix matrizCalibracion;
    private final ArrayList<Double> historialAngulos;

    public MedidorAnguloFlexion() throws IOException {
        this.matrizCalibracion = cargarMatrizDesdeArchivo("matriz_calibracion.dat");
        this.historialAngulos = new ArrayList<>();
    }

    private RealMatrix cargarMatrizDesdeArchivo(String archivo) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (RealMatrix) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al leer la matriz de calibración", e);
        }
    }

    private RealVector alinearEnXY(double ax, double ay, double az) {
        RealVector crudo = new ArrayRealVector(new double[]{ax, ay, az});
        RealVector rotado = matrizCalibracion.operate(crudo);
        return new ArrayRealVector(new double[]{rotado.getEntry(0), rotado.getEntry(1)}); // XY
    }

    public double calcularAngulo(double ax1, double ay1, double az1,
                                 double ax2, double ay2, double az2) {
        RealVector v1 = alinearEnXY(ax1, ay1, az1);
        RealVector v2 = alinearEnXY(ax2, ay2, az2);

        double dot = v1.dotProduct(v2);
        double norms = v1.getNorm() * v2.getNorm();

        double angleRad = Math.acos(Math.max(-1.0, Math.min(1.0, dot / norms)));
        double angleDeg = Math.toDegrees(angleRad);

        historialAngulos.add(angleDeg);
        return angleDeg;
    }

    public ArrayList<Double> getHistorialAngulos() {
        return new ArrayList<>(historialAngulos);
    }

    public void guardarCSV(String rutaArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("Tiempo(ms),Angulo(deg)");
            int tiempo = 0;
            for (Double angulo : historialAngulos) {
                writer.printf("%d,%.4f%n", tiempo, angulo);
                tiempo += 100; // asumiendo 100 ms entre muestras
            }
        }
    }
}