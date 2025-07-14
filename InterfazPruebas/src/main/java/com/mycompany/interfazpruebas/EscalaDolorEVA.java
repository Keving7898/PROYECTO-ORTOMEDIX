/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfazpruebas;

import javax.swing.*;
import java.awt.*;

public class EscalaDolorEVA extends JDialog {

    private int valorDolor = -1; // -1 significa que no se seleccionó

    public EscalaDolorEVA(JFrame parentFrame, String tituloPrueba) {
        super(parentFrame, "Escala Visual Análoga del Dolor", true);

        setLayout(new BorderLayout(10, 10));
        setSize(500, 200);
        setLocationRelativeTo(parentFrame);

        JLabel lblInstruccion = new JLabel("Indica tu nivel de dolor durante la prueba de " + tituloPrueba, SwingConstants.CENTER);
        lblInstruccion.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(lblInstruccion, BorderLayout.NORTH);

        // Escala de 0 a 10
        JSlider slider = new JSlider(0, 10, 0);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Colores visuales (opcional para customización posterior)

        add(slider, BorderLayout.CENTER);

        JPanel etiquetas = new JPanel(new GridLayout(1, 2));
        etiquetas.add(new JLabel("Sin dolor", SwingConstants.LEFT));
        etiquetas.add(new JLabel("Dolor máximo", SwingConstants.RIGHT));
        add(etiquetas, BorderLayout.SOUTH);

        JButton btnAceptar = new JButton("Confirmar");
        btnAceptar.addActionListener(e -> {
            valorDolor = slider.getValue();
            JOptionPane.showMessageDialog(this, "Valor registrado: " + valorDolor + "/10");
            dispose();
        });
        add(btnAceptar, BorderLayout.EAST);

        setVisible(true);
    }

    public int getValorDolor() {
        return valorDolor;
    }
}