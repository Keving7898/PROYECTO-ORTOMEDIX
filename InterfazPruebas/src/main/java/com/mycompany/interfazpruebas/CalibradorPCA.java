package com.mycompany.interfazpruebas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.*;

import java.util.List;

public class CalibradorPCA {

    public static Vector3D calcularEjePCA(List<Vector3D> datos) {
        int n = datos.size();
        RealMatrix matriz = MatrixUtils.createRealMatrix(n, 3);
        for (int i = 0; i < n; i++) {
            Vector3D v = datos.get(i);
            matriz.setRow(i, new double[]{v.getX(), v.getY(), v.getZ()});
        }

        // Centrar los datos
        RealVector media = mediaColumnas(matriz);
        for (int i = 0; i < n; i++) {
            matriz.setRowVector(i, matriz.getRowVector(i).subtract(media));
        }

        // PCA: valores propios de la matriz de covarianza
        RealMatrix cov = matriz.transpose().multiply(matriz).scalarMultiply(1.0 / (n - 1));
        EigenDecomposition ed = new EigenDecomposition(cov);
        RealVector eje = ed.getEigenvector(0);
        return new Vector3D(eje.toArray()).normalize();
    }

    public static RealMatrix rotacionEntre(Vector3D u1, Vector3D u2) {
        u1 = u1.normalize();
        u2 = u2.normalize();
        Vector3D v = u1.crossProduct(u2);
        double s = v.getNorm();
        double c = u1.dotProduct(u2);
        double[][] vx = {
            {0, -v.getZ(), v.getY()},
            {v.getZ(), 0, -v.getX()},
            {-v.getY(), v.getX(), 0}
        };
        RealMatrix vxM = MatrixUtils.createRealMatrix(vx);
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(3);
        RealMatrix R = I.add(vxM).add(vxM.multiply(vxM).scalarMultiply((1 - c) / (s * s)));
        return R;
    }

    private static RealVector mediaColumnas(RealMatrix M) {
        double[] mean = new double[3];
        for (int j = 0; j < 3; j++) {
            double sum = 0;
            for (int i = 0; i < M.getRowDimension(); i++) {
                sum += M.getEntry(i, j);
            }
            mean[j] = sum / M.getRowDimension();
        }
        return new ArrayRealVector(mean);
    }
}
