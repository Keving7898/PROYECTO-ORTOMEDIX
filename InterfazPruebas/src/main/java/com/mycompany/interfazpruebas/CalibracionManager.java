package com.mycompany.interfazpruebas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;

public class CalibracionManager {
    private static RealMatrix matrizCalibracion = MatrixUtils.createRealIdentityMatrix(3);

public static void guardarMatriz(RealMatrix R) {
    matrizCalibracion = R.copy();

    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("matriz_calibracion.dat"))) {
        out.writeObject(matrizCalibracion);
        System.out.println("✅ Matriz guardada correctamente en archivo.");
    } catch (IOException e) {
        System.err.println("❌ Error al guardar la matriz: " + e.getMessage());
    }
}

    public static RealMatrix getMatrizCalibracion() {
        return matrizCalibracion.copy();
    }

    public static RealMatrix aplicarCalibracion(RealMatrix R_sensor) {
        return matrizCalibracion.multiply(R_sensor);
    }
    
    public static RealMatrix cargarMatriz() throws IOException {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("matriz_calibracion.dat"))) {
        return (RealMatrix) in.readObject();
    } catch (ClassNotFoundException e) {
        throw new IOException("Error al leer la matriz", e);
    }
}
}
