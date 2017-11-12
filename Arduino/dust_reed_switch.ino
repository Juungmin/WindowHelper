int Reed = 7;

/* dust sensor */
int measurePin = A0;
int ledPower = 12;
 
int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;
 
float voMeasured = 0;
float calcVoltage = 0;
float dustDensity = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);  
  pinMode(Reed, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
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

  if(digitalRead(Reed) == HIGH) {
    Serial.print("Reed: ");
    Serial.print(digitalRead(Reed));
    Serial.println(" opened");  
  }
  else {
    Serial.print("Reed: ");
    Serial.print(digitalRead(Reed));
    Serial.println(" closed");
  } 

 
  Serial.print("Dust_Density: ");
  Serial.print(dustDensity*100.0);
  Serial.println(" ug/m3");

  delay(5000);
}
