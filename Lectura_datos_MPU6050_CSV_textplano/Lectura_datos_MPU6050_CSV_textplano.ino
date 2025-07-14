#include <Wire.h>
#include <MPU6050.h>

MPU6050 mpu1(0x68);  // muñeca
MPU6050 mpu2(0x69);  // brazo

// Sensibilidad seleccionada
const float ACC_SCALE = 8192.0;   // ±4g
const float GYRO_SCALE = 65.5;    // ±500°/s

void setup() {
  Serial.begin(115200);
  Wire.begin();

  mpu1.initialize();
  mpu2.initialize();

  // ✔️ Offsets calibrados para mpu1 (0x68)
  mpu1.setXAccelOffset(1057);
  mpu1.setYAccelOffset(-5123);
  mpu1.setZAccelOffset(1389);
  mpu1.setXGyroOffset(23);
  mpu1.setYGyroOffset(4);
  mpu1.setZGyroOffset(-15);

  // ✔️ Offsets calibrados para mpu2 (0x69)
  mpu2.setXAccelOffset(-1047);
  mpu2.setYAccelOffset(-3051);
  mpu2.setZAccelOffset(1081);
  mpu2.setXGyroOffset(20);
  mpu2.setYGyroOffset(-9);
  mpu2.setZGyroOffset(-27);

  // Acelerómetro ±4g
  mpu1.setFullScaleAccelRange(MPU6050_ACCEL_FS_4);
  mpu2.setFullScaleAccelRange(MPU6050_ACCEL_FS_4);

  // Giroscopio ±500°/s
  mpu1.setFullScaleGyroRange(MPU6050_GYRO_FS_500);
  mpu2.setFullScaleGyroRange(MPU6050_GYRO_FS_500);

  // Filtro LPF a 44Hz
  mpu1.setDLPFMode(3);
  mpu2.setDLPFMode(3);

  mpu1.setSleepEnabled(false);
  mpu2.setSleepEnabled(false);
}

void loop() {
  int16_t ax1, ay1, az1, gx1, gy1, gz1;
  int16_t ax2, ay2, az2, gx2, gy2, gz2;

  mpu1.getMotion6(&ax1, &ay1, &az1, &gx1, &gy1, &gz1);
  mpu2.getMotion6(&ax2, &ay2, &az2, &gx2, &gy2, &gz2);

  // Aceleración en m/s²
  float a1x = ax1 / ACC_SCALE * 9.80665;
  float a1y = ay1 / ACC_SCALE * 9.80665;
  float a1z = az1 / ACC_SCALE * 9.80665;

  float a2x = ax2 / ACC_SCALE * 9.80665;
  float a2y = ay2 / ACC_SCALE * 9.80665;
  float a2z = az2 / ACC_SCALE * 9.80665;

  // Giroscopio en rad/s
  float g1x = gx1 / GYRO_SCALE * PI / 180.0;
  float g1y = gy1 / GYRO_SCALE * PI / 180.0;
  float g1z = gz1 / GYRO_SCALE * PI / 180.0;

  float g2x = gx2 / GYRO_SCALE * PI / 180.0;
  float g2y = gy2 / GYRO_SCALE * PI / 180.0;
  float g2z = gz2 / GYRO_SCALE * PI / 180.0;

  // Transmitir en texto plano CSV
  Serial.print(a1x); Serial.print(",");
  Serial.print(a1y); Serial.print(",");
  Serial.print(a1z); Serial.print(",");
  Serial.print(g1x); Serial.print(",");
  Serial.print(g1y); Serial.print(",");
  Serial.print(g1z); Serial.print(",");
  Serial.print(a2x); Serial.print(",");
  Serial.print(a2y); Serial.print(",");
  Serial.print(a2z); Serial.print(",");
  Serial.print(g2x); Serial.print(",");
  Serial.print(g2y); Serial.print(",");
  Serial.println(g2z);

  delay(10);  // 100 Hz
}
