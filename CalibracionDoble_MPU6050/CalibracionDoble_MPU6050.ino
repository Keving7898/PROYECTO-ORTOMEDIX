#include "I2Cdev.h"
#include "MPU6050.h"
#include <Wire.h>

MPU6050 mpu1(0x68);  // Paleta
MPU6050 mpu2(0x69);  // Brazo

MPU6050* mpu;  // puntero dinámico al sensor actual

// Constantes del algoritmo
const int usDelay = 3150;
const int NFast = 1000;
const int NSlow = 10000;
const int LinesBetweenHeaders = 5;
const int iAx = 0, iAy = 1, iAz = 2, iGx = 3, iGy = 4, iGz = 5;

// Variables de calibración
int LowValue[6], HighValue[6], Smoothed[6];
int LowOffset[6], HighOffset[6], Target[6];
int LinesOut, N, i;

void setup() {
  Serial.begin(115200);
  Wire.begin();
  delay(500);

  Serial.println("=== CALIBRACIÓN IMU #1 (0x68) ===");
  calibrarSensor(&mpu1);

  Serial.println("\n=== CALIBRACIÓN IMU #2 (0x69) ===");
  calibrarSensor(&mpu2);

  Serial.println("\n✅ Ambos IMUs calibrados.");
}

void loop() {
  // No hace nada en loop
}

// ===============================
// FUNCIONES PRINCIPALES
// ===============================
void calibrarSensor(MPU6050* sensor) {
  mpu = sensor;
  mpu->initialize();
  if (!mpu->testConnection()) {
    Serial.println("❌ Error: no se pudo conectar con IMU.");
    return;
  }

  Serial.println("✔️ IMU conectada correctamente.");
  for (i = iAx; i <= iGz; i++) {
    Target[i] = 0;
    HighOffset[i] = 0;
    LowOffset[i] = 0;
  }
  Target[iAz] = 16384;

  SetAveraging(NFast);
  PullBracketsOut();
  PullBracketsIn();

  Serial.println("\n🚀 Calibración finalizada.");
  Serial.println("Offsets encontrados:");
  mpu->PrintActiveOffsets();
}

// ===============================
// FUNCIONES AUXILIARES
// ===============================
void SetAveraging(int NewN) {
  N = NewN;
  Serial.print("\nAveraging ");
  Serial.print(N);
  Serial.println(" readings each time");
}

void PullBracketsOut() {
  boolean Done = false;
  int NextLowOffset[6], NextHighOffset[6];

  Serial.println("Expandiendo:");
  ForceHeader();

  while (!Done) {
    Done = true;
    SetOffsets(LowOffset);
    GetSmoothed();

    for (i = 0; i <= 5; i++) {
      LowValue[i] = Smoothed[i];
      if (LowValue[i] >= Target[i]) {
        Done = false;
        NextLowOffset[i] = LowOffset[i] - 1000;
      } else {
        NextLowOffset[i] = LowOffset[i];
      }
    }

    SetOffsets(HighOffset);
    GetSmoothed();

    for (i = 0; i <= 5; i++) {
      HighValue[i] = Smoothed[i];
      if (HighValue[i] <= Target[i]) {
        Done = false;
        NextHighOffset[i] = HighOffset[i] + 1000;
      } else {
        NextHighOffset[i] = HighOffset[i];
      }
    }

    ShowProgress();

    for (i = 0; i <= 5; i++) {
      LowOffset[i] = NextLowOffset[i];
      HighOffset[i] = NextHighOffset[i];
    }
  }
}

void PullBracketsIn() {
  boolean AllBracketsNarrow = false;
  boolean StillWorking = true;
  int NewOffset[6];

  Serial.println("\nCerrando:");
  ForceHeader();

  while (StillWorking) {
    StillWorking = false;
    if (AllBracketsNarrow && (N == NFast)) {
      SetAveraging(NSlow);
    } else {
      AllBracketsNarrow = true;
    }

    for (int i = 0; i <= 5; i++) {
      if (HighOffset[i] <= (LowOffset[i] + 1)) {
        NewOffset[i] = LowOffset[i];
      } else {
        StillWorking = true;
        NewOffset[i] = (LowOffset[i] + HighOffset[i]) / 2;
        if (HighOffset[i] > (LowOffset[i] + 10)) {
          AllBracketsNarrow = false;
        }
      }
    }

    SetOffsets(NewOffset);
    GetSmoothed();

    for (i = 0; i <= 5; i++) {
      if (Smoothed[i] > Target[i]) {
        HighOffset[i] = NewOffset[i];
        HighValue[i] = Smoothed[i];
      } else {
        LowOffset[i] = NewOffset[i];
        LowValue[i] = Smoothed[i];
      }
    }

    ShowProgress();
  }
}

void ForceHeader() {
  LinesOut = 99;
}

void GetSmoothed() {
  int16_t RawValue[6];
  long Sums[6];
  for (i = 0; i <= 5; i++) Sums[i] = 0;

  for (i = 1; i <= N; i++) {
    mpu->getMotion6(&RawValue[iAx], &RawValue[iAy], &RawValue[iAz],
                    &RawValue[iGx], &RawValue[iGy], &RawValue[iGz]);
    delayMicroseconds(usDelay);
    for (int j = 0; j <= 5; j++) {
      Sums[j] += RawValue[j];
    }
  }

  for (i = 0; i <= 5; i++) {
    Smoothed[i] = (Sums[i] + N / 2) / N;
  }
}

void SetOffsets(int TheOffsets[6]) {
  mpu->setXAccelOffset(TheOffsets[iAx]);
  mpu->setYAccelOffset(TheOffsets[iAy]);
  mpu->setZAccelOffset(TheOffsets[iAz]);
  mpu->setXGyroOffset(TheOffsets[iGx]);
  mpu->setYGyroOffset(TheOffsets[iGy]);
  mpu->setZGyroOffset(TheOffsets[iGz]);
}

void ShowProgress() {
  if (LinesOut >= LinesBetweenHeaders) {
    Serial.println("\t\tXAccel\t\tYAccel\t\tZAccel\t\tXGyro\t\tYGyro\t\tZGyro");
    LinesOut = 0;
  }

  Serial.print(' ');
  for (i = 0; i <= 5; i++) {
    Serial.print('[');
    Serial.print(LowOffset[i]);
    Serial.print(',');
    Serial.print(HighOffset[i]);
    Serial.print("] --> [");
    Serial.print(LowValue[i]);
    Serial.print(',');
    Serial.print(HighValue[i]);
    if (i == 5) {
      Serial.println("]");
    } else {
      Serial.print("]\t");
    }
  }
  LinesOut++;
}
