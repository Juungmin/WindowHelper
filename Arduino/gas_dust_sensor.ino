int gasPin_mq2 = A1;
int gasPin_mq7 = A2;

/*********** dust sensor*********/
int measurePin = A0;
int ledPower = 12;

int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;
 
float voMeasured = 0;
float calcVoltage = 0;
float dustDensity = 0;
/*******************************/

const int numReadings = 10; //size of array

/***********for average_ MQ-2*********/
float readings[numReadings];
int readIndex = 0;
float total = 0;
float average = 0;
/*******************************/

/***********for average_ MQ-7*********/
float readings_[numReadings];
int readIndex_ = 0;
float total_ = 0;
float average_ = 0;
/*******************************/

int count = 0; //just a loop count

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  for (int thisReading = 0; thisReading < numReadings; thisReading++) {
    readings[thisReading] = 0;  
  }
}

float getRValue(float sensor_volt, float RS_gas, float sensorValue) {
  float R;

  sensor_volt= sensorValue/ 1024 * 5.0;
  RS_gas = (5.0 - sensor_volt)/sensor_volt;
  R = RS_gas/10.0;

  return R;
}

void loop() {
  // put your main code here, to run repeatedly:
  float sensor_volt_mq2;
  float sensor_volt_mq7;
  float RS_gas_mq2; //Get value of RS(Resistance of the Sensor) in a GAS
  float RS_gas_mq7; //Get value of RS(Resistance of the Sensor) in a GAS
  float ratio_mq2;
  float ratio_mq7;

  float RS_air_mq2;
  float RS_air_mq7;

  float R0;
  float R1;
  
  float sensorValue_mq2 = analogRead(gasPin_mq2);
  float sensorValue_mq7 = analogRead(gasPin_mq7);

  /***************************** dust detecting ****************************/
  digitalWrite(ledPower,LOW); // power on the LED
  delayMicroseconds(samplingTime);
 
  voMeasured = analogRead(measurePin); // read the dust value
 
  delayMicroseconds(deltaTime);
  digitalWrite(ledPower,HIGH); // turn the LED off
  delayMicroseconds(sleepTime);
 
  // 0 - 5.0V mapped to 0 - 1023 integer values
  // recover voltage
  calcVoltage = voMeasured * (5.0 / 1024);
 
  // linear eqaution taken from http://www.howmuchsnow.com/arduino/airquality/
  // Chris Nafis (c) 2012
  dustDensity = 0.17 * calcVoltage - 0.1;

//  Serial.print(" - Dust Density: ");
//  Serial.println(dustDensity);     
  /***************************************************************************/

  /********************************* mq2 *************************************/
  sensor_volt_mq2 = (float)sensorValue_mq2 / 1024*5.0;    
  sensor_volt_mq7 = (float)sensorValue_mq7 / 1024*5.0;
  
  RS_gas_mq2 = (5.0-sensor_volt_mq2)/sensor_volt_mq2; //omit * RL
  RS_gas_mq7 = (5.0-sensor_volt_mq7)/sensor_volt_mq7; //omit * RL

  R0 = getRValue(sensor_volt_mq2, RS_gas_mq2, sensorValue_mq2);
  R1 = getRValue(sensor_volt_mq7, RS_gas_mq7, sensorValue_mq7);
  

  /*Serial.print("R0 : ");
  Serial.print(R0);
  Serial.print("R1 : ");
  Serial.println(R1);

  /*-Replace the name "R0" with the value of R0 in the demo of First Test -*/
  ratio_mq2 = RS_gas_mq2 /0.26;  // ratio = RS/R0 
  ratio_mq7 = RS_gas_mq7/0.23;  // ratio = RS/R0 

  /***************************************************************************/

//Serial.print("ratio_mq2: " + String(ratio_mq2) + ", ratio_mq7: " + String(ratio_mq7) + "\n");


  /***************************get average of MQ-2, MQ-7 gas sensor******************/
  // subtract the last reading
  total = total - readings[readIndex];
  total_ = total_ - readings_[readIndex_];
  
  //read from the sensor
  readings[readIndex] = ratio_mq2;
  readings_[readIndex_] = ratio_mq7;
  
  //add the reading to total
  total = total + readings[readIndex];
  total_ = total_ + readings_[readIndex_];
  
  //advance to the next position in the arr
  readIndex = readIndex + 1;
  readIndex_ = readIndex_ + 1;
  
  // if we're at the end of the array
  if (readIndex >= numReadings) {
    readIndex = 0; 
    readIndex_ = 0; 
  }


  
  if(count != 0 && count%10 == 0) {
    // calculate the average:    
    average = total / numReadings;  
    average_ = total_ / numReadings; 
    
    Serial.print("Dust_Density: ");
    Serial.print(dustDensity*100.);
    Serial.println(" ug/m3");
  
    Serial.print("MQ_2: ");
    Serial.print(average);
    Serial.print(" mg_2");

    Serial.print(" ");
    Serial.print(sensorValue_mq2);
    Serial.print(" ppm_2,");

  
    Serial.print(" MQ_7: ");
    Serial.print(average_);
    Serial.print(" mg_7 ");   
   
    Serial.print(sensorValue_mq7);
    Serial.println(" ppm_7");       
  }
/******************************************************************************/

  delay(1000);
  count++;
}
