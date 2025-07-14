package com.mycompany.interfazpruebas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class SerialIMUReader {
    private SerialPort serialPort;
    private BufferedReader reader;
    private final List<Vector3D> girosIMU1 = new ArrayList<>();
    private final List<Vector3D> girosIMU2 = new ArrayList<>();
    private final List<Vector3D> accelsIMU1 = new ArrayList<>();
    private final List<Vector3D> accelsIMU2 = new ArrayList<>();

    public boolean conectar(String puerto, int baudios) {
        serialPort = SerialPort.getCommPort(puerto);
        serialPort.setComPortParameters(baudios, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        boolean abierto = serialPort.openPort();
        if (abierto) {
            reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        }
        return abierto;
    }

    public void leerDurante(int milisegundos) {
        long tiempoInicio = System.currentTimeMillis();
        girosIMU1.clear();
        girosIMU2.clear();
        accelsIMU1.clear();
        accelsIMU2.clear();

        try {
            while (System.currentTimeMillis() - tiempoInicio < milisegundos) {
                String linea = reader.readLine();
                String[] tokens = linea.trim().split(",");
                if (tokens.length != 12) continue;

                double ax1 = Double.parseDouble(tokens[0]);
                double ay1 = Double.parseDouble(tokens[1]);
                double az1 = Double.parseDouble(tokens[2]);
                double gx1 = Double.parseDouble(tokens[3]);
                double gy1 = Double.parseDouble(tokens[4]);
                double gz1 = Double.parseDouble(tokens[5]);

                double ax2 = Double.parseDouble(tokens[6]);
                double ay2 = Double.parseDouble(tokens[7]);
                double az2 = Double.parseDouble(tokens[8]);
                double gx2 = Double.parseDouble(tokens[9]);
                double gy2 = Double.parseDouble(tokens[10]);
                double gz2 = Double.parseDouble(tokens[11]);

                accelsIMU1.add(new Vector3D(ax1, ay1, az1));
                accelsIMU2.add(new Vector3D(ax2, ay2, az2));

                girosIMU1.add(new Vector3D(gx1, gy1, gz1));
                girosIMU2.add(new Vector3D(gx2, gy2, gz2));
            }
        } catch (IOException | NumberFormatException e) {
        }
    }

    public List<Vector3D> getGirosIMU1() { return girosIMU1; }
    public List<Vector3D> getGirosIMU2() { return girosIMU2; }

    public List<Vector3D> getAccelsIMU1() { return accelsIMU1; }
    public List<Vector3D> getAccelsIMU2() { return accelsIMU2; }

    public double[] leerUltimaAceleracionIMU1() {
        if (!accelsIMU1.isEmpty()) {
            Vector3D a = accelsIMU1.get(accelsIMU1.size() - 1);
            return new double[]{a.getX(), a.getY(), a.getZ()};
        }
        return new double[]{0, 0, 0};
    }

    public double[] leerUltimaAceleracionIMU2() {
        if (!accelsIMU2.isEmpty()) {
            Vector3D a = accelsIMU2.get(accelsIMU2.size() - 1);
            return new double[]{a.getX(), a.getY(), a.getZ()};
        }
        return new double[]{0, 0, 0};
    }
}
