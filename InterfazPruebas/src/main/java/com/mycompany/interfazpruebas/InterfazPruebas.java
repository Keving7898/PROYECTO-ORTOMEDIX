/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.interfazpruebas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.math3.linear.RealMatrix;
import org.jfree.chart.ChartPanel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Sine;
import java.util.List;

public class InterfazPruebas extends JFrame {
    private final JButton btnPasivaMovil;
    private final JButton btnPasivaInmovil;
    private final JButton btnCalibracion;

    public InterfazPruebas() {
        setTitle("Interfaz de Pruebas");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panelBase = new JPanel(new BorderLayout());
        panelBase.setBackground(Color.WHITE);
        setContentPane(panelBase);

        // 🔷 Encabezado
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(new Color(10, 251, 152));
        encabezado.setPreferredSize(new Dimension(800, 130));

        JLabel lblTitulo = new JLabel("Bienvenido a Ortomedix", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 24));
        lblTitulo.setForeground(Color.BLACK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        encabezado.add(lblTitulo, BorderLayout.CENTER);
        panelBase.add(encabezado, BorderLayout.NORTH);

        // ✨ Animación
        Timeline timeline = new Timeline(encabezado);
        timeline.addPropertyToInterpolate("background", new Color(10, 251, 152), new Color(100, 255, 200));
        timeline.setDuration(1500);
        timeline.setEase(new Sine());
        timeline.playLoop(Timeline.RepeatBehavior.REVERSE);

        // 🎛️ Panel central con botones
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 60, 20, 60); // Espaciado mayor entre botones

        Color colorBoton = new Color(10, 251, 167);

        // 🟦 Botón prueba pasiva movil
        btnPasivaMovil = new JButton("<html><center>Prueba<br>Pasiva Móvil</center></html>", getScaledIcon("/imagenes/pasiva_movil.png", 96, 96));
        configurarBoton(btnPasivaMovil, colorBoton);
        btnPasivaMovil.addActionListener(e -> ejecutarPruebaPasivaMovil());

        // 🟨 Botón prueba pasiva inmovil
        btnPasivaInmovil = new JButton("<html><center>Prueba<br>Pasiva Inmóvil</center></html>", getScaledIcon("/imagenes/pasiva_inmovil.png", 96, 96));
        configurarBoton(btnPasivaInmovil, colorBoton);
        btnPasivaInmovil.addActionListener(e -> ejecutarPruebaPasivaInmovil());

        // 🟩 Botón calibración
        btnCalibracion = new JButton("<html><center>Calibración</center></html>", getScaledIcon("/imagenes/calibracion.png", 96, 96));
        configurarBoton(btnCalibracion, colorBoton);
        btnCalibracion.addActionListener(e -> mostrarVentanaCalibracion());

        // ➕ Agregar botones al panel
        gbc.gridx = 0;
        panelCentral.add(btnPasivaMovil, gbc);
        gbc.gridx = 1;
        panelCentral.add(btnPasivaInmovil, gbc);
        gbc.gridx = 2;
        panelCentral.add(btnCalibracion, gbc);

        panelBase.add(panelCentral, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configurarBoton(JButton boton, Color colorFondo) {
        boton.setPreferredSize(new Dimension(240, 240)); // Más ancho y alto
        boton.setVerticalTextPosition(SwingConstants.BOTTOM);
        boton.setHorizontalTextPosition(SwingConstants.CENTER);
        boton.setBackground(colorFondo);
        boton.setFont(new Font("SansSerif", Font.BOLD, 19)); // Fuente más grande
        boton.setOpaque(true);
        boton.setBorderPainted(false);
    }

    private ImageIcon getScaledIcon(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("❌ No se encontró el recurso: " + path);
            return null;
        }
    }

    // Métodos vacíos aún
    public void ejecutarPruebaPasivaMovil() {
        String mensaje = """
            PROTOCOLO: PRUEBA PASIVA CON MOVIMIENTO

            🧑‍⚕️ El fisioterapeuta debe mover el brazo del paciente:

            - Fase de Flexión:
              Iniciar con el brazo completamente extendido. Flexionar suavemente por 10 segundos.

            - Fase de Extensión:
              Iniciar con el brazo en 90°. Extender completamente por 10 segundos.

            ⚠️ El paciente no debe realizar ningún esfuerzo.
            """;

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Prueba Pasiva Móvil", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            new Thread(() -> PruebaPasivaMovilRunner.ejecutar(this)).start();
        }
    }

    public void ejecutarPruebaPasivaInmovil() {
        String mensaje = """
            PROTOCOLO: PRUEBA PASIVA INMÓVIL

            🧍 Mantener la postura durante 10 segundos mientras el sistema estima el ángulo promedio.

            - Fase de Flexión: mantener el brazo flexionado en 90°
            - Fase de Extensión: mantener el brazo en extensión.

            ⚠️ No mover el brazo ni los sensores.
            """;

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Prueba Pasiva Inmóvil", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            new Thread(() -> PruebaPasivaInmovilRunner.ejecutar(this)).start();
        }
    }

private void mostrarVentanaCalibracion() {
    String mensaje = """
        INSTRUCCIONES PARA LA CALIBRACIÓN:

        1. Colócate sentado, derecho, con el brazo derecho relajado y apoyado.
        2. Asegúrate de que los sensores IMU estén bien sujetos:
           - Uno en el brazo (arriba del codo).
           - Otro en el antebrazo (debajo del codo).

        3. NO muevas el tronco ni el hombro durante la prueba.

        4. Al presionar 'Continuar', el sistema empezará a leer datos durante 6 segundos.
           DURANTE ese tiempo:
           → Realiza entre 3 a 5 movimientos lentos y completos de flexión y extensión del codo.
           → Haz los movimientos de forma continua, sin detenerte.

        ⚠️ Si estás listo, presiona 'Continuar'. Si no, presiona 'Cancelar'.
        """;

    int opcion = JOptionPane.showConfirmDialog(
        this,
        mensaje,
        "Protocolo de Calibración",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.INFORMATION_MESSAGE
    );

    if (opcion == JOptionPane.OK_OPTION) {
        // Crear la ventana emergente
        JDialog dialogProgreso = new JDialog(this, "Capturando movimientos...", true);
        dialogProgreso.setSize(400, 120);
        dialogProgreso.setLayout(new BorderLayout());

        JLabel lbl = new JLabel("Realiza los movimientos ahora", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        dialogProgreso.add(lbl, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        dialogProgreso.add(progressBar, BorderLayout.CENTER);

        dialogProgreso.setLocationRelativeTo(this);

        // Hilo de ejecución para no bloquear interfaz
        new Thread(() -> {
            SerialIMUReader reader = new SerialIMUReader();
            if (reader.conectar("COM8", 115200)) {
                // SOLO una lectura real (sin sleep ni segunda lectura)
                reader.leerDurante(6000);
                var datos1 = reader.getGirosIMU1();
                var datos2 = reader.getGirosIMU2();
                var eje1 = CalibradorPCA.calcularEjePCA(datos1);
                var eje2 = CalibradorPCA.calcularEjePCA(datos2);
                var R = CalibradorPCA.rotacionEntre(eje2, eje1);
                CalibracionManager.guardarMatriz(R);
                SwingUtilities.invokeLater(() -> {
                    dialogProgreso.dispose();
                    JOptionPane.showMessageDialog(InterfazPruebas.this, "✅ Calibración completada con éxito.");
                    mostrarMatrizCalibracion();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    dialogProgreso.dispose();
                    JOptionPane.showMessageDialog(InterfazPruebas.this, "❌ No se pudo abrir el puerto COM8.", "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        dialogProgreso.setVisible(true);
    }
}

private void mostrarMatrizCalibracion() {
    try {
        RealMatrix matriz = CalibracionManager.cargarMatriz();

        StringBuilder sb = new StringBuilder("Matriz de Calibración (Rotación 3x3):\n\n");

        for (int i = 0; i < matriz.getRowDimension(); i++) {
            for (int j = 0; j < matriz.getColumnDimension(); j++) {
                sb.append(String.format("%.4f", matriz.getEntry(i, j))).append("\t");
            }
            sb.append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this, scroll, "Matriz de Calibración", JOptionPane.INFORMATION_MESSAGE);

    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "❌ No se pudo cargar la matriz de calibración.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

   public void mostrarGraficasCombinadas(List<Double> angulos, List<Double> velocidades,String nombreArchivoPNG) {
        GraficadorAnguloCSV graficoAngulo = new GraficadorAnguloCSV();
        GraficadorVelocidadCSV graficoVelocidad = new GraficadorVelocidadCSV();

        ChartPanel panelAngulo = graficoAngulo.getChartPanel();
        ChartPanel panelVelocidad = graficoVelocidad.getChartPanel();

        panelAngulo.setPreferredSize(new Dimension(800, 300));
        panelVelocidad.setPreferredSize(new Dimension(800, 300));

        JPanel panelCombinado = new JPanel();
        panelCombinado.setLayout(new BoxLayout(panelCombinado, BoxLayout.Y_AXIS));
        panelCombinado.add(panelAngulo);
        panelCombinado.add(panelVelocidad);

        JFrame ventanaGraficas = new JFrame("Gráficas de Prueba Activa");
        ventanaGraficas.setContentPane(panelCombinado);
        ventanaGraficas.pack();
        ventanaGraficas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaGraficas.setLocationRelativeTo(null);
        ventanaGraficas.setVisible(true);

        for (int i = 0; i < angulos.size(); i++) {
            double angulo = angulos.get(i);
            double velocidad = i < velocidades.size() ? velocidades.get(i) : 0.0;
            graficoAngulo.agregarDato(angulo);
            graficoVelocidad.agregarDato(velocidad);
        }

        try {
            BufferedImage imagen = new BufferedImage(panelCombinado.getWidth(), panelCombinado.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imagen.createGraphics();
            panelCombinado.paint(g2d);
            g2d.dispose();
             ImageIO.write(imagen, "png", new File(nombreArchivoPNG));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Error al guardar la imagen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazPruebas::new);
    }
}
