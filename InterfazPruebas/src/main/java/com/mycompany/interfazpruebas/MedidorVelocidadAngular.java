/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MedidorVelocidadAngular {

    private final List<Double> historialVelocidades = new ArrayList<>();
    private final Vector3D ejePCA;
    private final RealMatrix R;

    public MedidorVelocidadAngular(RealMatrix matrizRotacion, Vector3D ejePrincipal) {
        this.R = matrizRotacion;
        this.ejePCA = ejePrincipal.normalize();
    }

    private Vector3D rotar(Vector3D original) {
        double[] arr = {original.getX(), original.getY(), original.getZ()};
        double[] rotado = R.operate(arr);
        return new Vector3D(rotado);
    }

    public double calcularVelocidad(Vector3D gyroAntebrazo, Vector3D gyroBrazo) {
        Vector3D g1 = rotar(gyroBrazo);     // mpu2 (brazo)
        Vector3D g2 = rotar(gyroAntebrazo); // mpu1 (muñeca)

        double w1 = g1.dotProduct(ejePCA);  // componente en eje funcional
        double w2 = g2.dotProduct(ejePCA);

        double velocidadRelativa = w2 - w1; // rad/s
        historialVelocidades.add(velocidadRelativa);

        return velocidadRelativa;
    }

    public List<Double> getHistorial() {
        return new ArrayList<>(historialVelocidades);
    }

    public void guardarCSV(String ruta) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ruta))) {
            writer.println("Tiempo(ms),VelocidadAngular(rad/s)");
            int tiempo = 0;
            for (Double w : historialVelocidades) {
                writer.printf("%d,%.6f%n", tiempo, w);
                tiempo += 100; // 100 ms por muestra
            }
        }
    }
}