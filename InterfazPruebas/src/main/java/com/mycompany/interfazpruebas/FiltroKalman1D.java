/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;
import org.apache.commons.math3.linear.*;

public class FiltroKalman1D {

    // Estado del sistema: ángulo [theta]
    private RealVector estado;         // x_k = [theta_k]
    private RealMatrix P;              // Covarianza de error
    private final double Q;            // Varianza del proceso (ruido del gyro)
    private final double R;            // Varianza de medición (ruido del ángulo observado)

    public FiltroKalman1D(double q, double r) {
        this.estado = new ArrayRealVector(new double[]{0.0}); // ángulo inicial
        this.P = MatrixUtils.createRealIdentityMatrix(1);     // P inicial
        this.Q = q;
        this.R = r;
    }

    /**
     * Actualiza el filtro con nueva velocidad angular y ángulo medido
     * @param velocidadAngular  velocidad relativa entre IMUs (rad/s)
     * @param anguloMedido      ángulo estimado desde PCA (grados o rad)
     * @param dt                tiempo entre muestras (segundos)
     * @return                  ángulo filtrado
     */
    public double actualizar(double velocidadAngular, double anguloMedido, double dt) { // === PREDICCIÓN ===
        // x_k|k-1 = x_k-1 + dt * omega
        // F = 1
        // H = 1
        RealVector prediccion = estado.add(new ArrayRealVector(new double[]{velocidadAngular * dt}));
        RealMatrix P_pred = P.add(MatrixUtils.createRealIdentityMatrix(1).scalarMultiply(Q));

        // === CORRECCIÓN ===
        RealVector z = new ArrayRealVector(new double[]{anguloMedido}); // Observación

        RealVector y = z.subtract(prediccion); // Innovación
        RealMatrix S = P_pred.add(MatrixUtils.createRealIdentityMatrix(1).scalarMultiply(R)); // S = P + R
        RealMatrix K = P_pred.multiply(MatrixUtils.inverse(S));  // Ganancia de Kalman

        estado = prediccion.add(K.operate(y)); // x_k = x_k|k-1 + K * (z - x_k|k-1)
        P = MatrixUtils.createRealIdentityMatrix(1).subtract(K).multiply(P_pred); // P_k

        return estado.getEntry(0); // ángulo estimado
    }

    public double getEstado() {
        return estado.getEntry(0);
    }

    public void reiniciar() {
        this.estado = new ArrayRealVector(new double[]{0.0});
        this.P = MatrixUtils.createRealIdentityMatrix(1);
    }
}