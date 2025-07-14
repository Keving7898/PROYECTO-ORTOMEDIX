/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;


import java.io.FileWriter;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PruebaPasivaInmovilRunner {

    public static void ejecutar(JFrame parentFrame) {
        String[] opciones = {"Flexión", "Extensión"};
        int seleccion = JOptionPane.showOptionDialog(
            parentFrame,
            "Selecciona la postura que vas a mantener durante 10 segundos:",
            "Prueba Pasiva Inmóvil",
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
            1. El paciente debe mantener flexionado el brazo a 90° sin moverse por 10 segundos.
            2. No se debe aplicar movimiento ni esfuerzo.""" :
            """
            1. El paciente debe mantenerse extendido sin moverse por 10 segundos.
            2. No se debe aplicar movimiento ni esfuerzo.""";

        int confirmar = JOptionPane.showConfirmDialog(
            parentFrame,
            mensaje + "\n\nPresiona Aceptar para iniciar la captura de ángulo mantenido.",
            "Protocolo Pasivo Inmóvil - " + tipo.toUpperCase(),
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

                lector.leerDurante(10000); // 10s de captura
                MedidorAnguloFlexion medidor = new MedidorAnguloFlexion();
                FiltroKalman1D kalman = new FiltroKalman1D(0.01, 2.0);
                long tPrev = System.nanoTime();

                var acc1 = lector.getAccelsIMU1();
                var acc2 = lector.getAccelsIMU2();

                java.util.List<Double> angulosFiltrados = new java.util.ArrayList<>();

                int n = Math.min(acc1.size(), acc2.size());
                for (int i = 0; i < n; i++) {
                    double[] a1 = {acc1.get(i).getX(), acc1.get(i).getY(), acc1.get(i).getZ()};
                    double[] a2 = {acc2.get(i).getX(), acc2.get(i).getY(), acc2.get(i).getZ()};
                    double angulo = medidor.calcularAngulo(a1[0], a1[1], a1[2], a2[0], a2[1], a2[2]);

                    long tNow = System.nanoTime();
                    double dt = (tNow - tPrev) / 1e9;
                    tPrev = tNow;

                    double thetaFiltrado = kalman.actualizar(0.0, angulo, dt); // w = 0 en prueba inmóvil
                    angulosFiltrados.add(thetaFiltrado);
                }

                // Calcular promedio
                double suma = 0;
                for (double a : angulosFiltrados) suma += a;
                double promedio = suma / angulosFiltrados.size();

                medidor.guardarCSV("angulo_pasivaInmovil_" + tipo + ".csv");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(parentFrame, "Promedio del ángulo mantenido: " + String.format("%.2f", promedio) + " grados");
                    EscalaDolorEVA escala = new EscalaDolorEVA(parentFrame, "Prueba Pasiva Inmóvil - " + tipo);
                    int dolor = escala.getValorDolor();
                    System.out.println("Nivel de dolor reportado: " + dolor + "/10");
                    
                    try (PrintWriter out = new PrintWriter(new FileWriter("eva_inmovil_" + tipo.toLowerCase() + ".txt"))) {
                        out.println("EVA Prueba Pasiva Inmóvil (" + tipo + "): " + dolor);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(parentFrame, "❌ No se pudo guardar EVA: " + e.getMessage());
                    }
                });
                

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame, "Error durante la prueba pasiva inmóvil: " + ex.getMessage()));
            }
        }).start();
    }
}