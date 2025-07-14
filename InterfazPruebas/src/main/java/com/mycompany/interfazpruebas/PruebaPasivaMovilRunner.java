/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;

import java.io.FileWriter;
import javax.swing.*;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;
import java.io.IOException;
import java.io.PrintWriter;

public class PruebaPasivaMovilRunner {

    public static void ejecutar(JFrame parentFrame) {
        String[] opciones = {"Flexión", "Extensión"};
        int seleccion = JOptionPane.showOptionDialog(
            parentFrame,
            "Selecciona el tipo de movimiento pasivo asistido que vas a realizar:",
            "Tipo de Movimiento Pasivo",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (seleccion == JOptionPane.CLOSED_OPTION) return;

        String tipo = seleccion == 0 ? "flexion" : "extension";
        String mensaje = tipo.equals("flexion") ?
              """
              1. El paciente debe iniciar con el brazo completamente extendido.
              2. El fisioterapeuta debe realizar una flexion completa por 10 segundos.
              3. El paciente no debe hacer esfuerzo."""
            :
            """
              1. El paciente debe iniciar con el brazo flexionado a 90°ba.
              2. El fisioterapeuta debe realizar una extension completa por 10 segundos.
              3. El paciente no debe hacer esfuerzo.""";

        int confirmar = JOptionPane.showConfirmDialog(
            parentFrame,
            mensaje + "\n\nPresiona Aceptar para comenzar la captura de datos.",
            "Protocolo Pasivo Asistido - " + tipo.toUpperCase(),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );

        if (confirmar != JOptionPane.OK_OPTION) return;

        new Thread(() -> {
            try {
                SerialIMUReader lector = new SerialIMUReader();
                if (!lector.conectar("COM8", 115200)) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame, "No se pudo abrir COM8."));
                    return;
                }
                lector.leerDurante(10000);

                MedidorAnguloFlexion medidor = new MedidorAnguloFlexion();
                RealMatrix R = CalibracionManager.cargarMatriz();
                Vector3D eje = CalibradorPCA.calcularEjePCA(lector.getGirosIMU2());
                MedidorVelocidadAngular medidorVel = new MedidorVelocidadAngular(R, eje);

                List<Vector3D> acc1 = lector.getAccelsIMU1();
                List<Vector3D> acc2 = lector.getAccelsIMU2();
                List<Vector3D> gyr1 = lector.getGirosIMU1();
                List<Vector3D> gyr2 = lector.getGirosIMU2();

                FiltroKalman1D kalman = new FiltroKalman1D(0.01, 2.0);
                long tPrev = System.nanoTime();

                java.util.List<Double> listaAngulos = new java.util.ArrayList<>();
                java.util.List<Double> listaVelocidades = new java.util.ArrayList<>();

                int n = Math.min(Math.min(acc1.size(), acc2.size()), gyr1.size());
                for (int i = 0; i < n; i++) {
                    double[] a1 = {acc1.get(i).getX(), acc1.get(i).getY(), acc1.get(i).getZ()};
                    double[] a2 = {acc2.get(i).getX(), acc2.get(i).getY(), acc2.get(i).getZ()};
                    double angulo = medidor.calcularAngulo(a1[0], a1[1], a1[2], a2[0], a2[1], a2[2]);
                    double vel = medidorVel.calcularVelocidad(gyr2.get(i), gyr1.get(i));

                    long tNow = System.nanoTime();
                    double dt = (tNow - tPrev) / 1e9;
                    tPrev = tNow;

                    double thetaFiltrado = kalman.actualizar(vel, angulo, dt);
                    listaAngulos.add(thetaFiltrado);
                    listaVelocidades.add(vel);
                }

                medidor.guardarCSV("angulo_pasivaMovil_" + tipo + ".csv");
                medidorVel.guardarCSV("velocidad_pasivaMovil_" + tipo + ".csv");

                SwingUtilities.invokeLater(() -> {
                    InterfazPruebas prueba = (InterfazPruebas) parentFrame;
                    prueba.mostrarGraficasCombinadas(listaAngulos, listaVelocidades, "graficas_pasivaMovil_" + tipo + ".png");
                    
                    EscalaDolorEVA escala = new EscalaDolorEVA(parentFrame, "Prueba Pasiva Móvil - " + tipo);
                    int dolor = escala.getValorDolor();
                    System.out.println("Nivel de dolor reportado: " + dolor + "/10");
                    
                    try (PrintWriter out = new PrintWriter(new FileWriter("eva_movil_" + tipo.toLowerCase() + ".txt"))) {
                        out.println("EVA Prueba Pasiva Móvil (" + tipo + "): " + dolor);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(parentFrame, "❌ No se pudo guardar EVA: " + e.getMessage());
                    }                    
                });

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame, "Error durante la prueba pasiva móvil: " + ex.getMessage()));
            }
        }).start();
    }
}
