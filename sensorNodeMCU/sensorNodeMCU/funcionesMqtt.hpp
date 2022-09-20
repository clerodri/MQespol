#include <DHT.h>
#include <PubSubClient.h>

DHT dht(DHTPIN,DHTTYPE);
DHT dht2(DHTPIN,DHTTYPE);
WiFiClient espClient; 
PubSubClient mqttClient(espClient);

//Funcion que realiza la conexion al broker sino muestra un mensaje de error de conexion y vuelve a intentar la conexion
void conectarMqtt(){
   while (!mqttClient.connected())
    {
      //Serial.println("Conexion  MQTT, Espere..");
      if (mqttClient.connect(MQTT_CLIENT_NAME))
      {
        //SuscribeMqtt();
      }
      else
      {
        Serial.print("Fallo MQTT Conexion, rc=");
        Serial.print(mqttClient.state());
        Serial.println(" Re-Intento en 3 seconds");
        delay(3000);
      }
     }
  }
  
//Inicializar el sensor y el cliente mqtt
void setupMqtt(){
    dht.begin();
    dht2.begin();
    mqttClient.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);  
    //SuscribeMqtt();
    //mqttClient.setCallback(OnMqttReceived);
  }

//Funcion que valida que el cliente mqtt este conectado al broker, sino se realiza nuevamente la conexion al broker  
void validarConexionMqtt(){
    if (!mqttClient.connected())
        {
          conectarMqtt();
        }
    mqttClient.loop();
  }
  void float2byte(float f,byte* byte_array){
      union{
        float float_val;
        byte temp_array[4];
        } u;
        u.float_val = f;
        memcpy(byte_array,u.temp_array,4);
    }

  void publicarMqtt(float t,float h)
{
    char tempBuff[8];
    char humBuff[8];
    dtostrf(t,5,2,tempBuff);
    dtostrf(h,5,2,humBuff);
    if(tempBuff[4]=='0'){
        tempBuff[4]='1';
      }
    if(humBuff[4]=='0'){
        humBuff[4]='1';
      }
    Serial.print("Humedad: ");
    Serial.print(h);
    Serial.print("  Temperatura: ");
    Serial.println(t);
    mqttClient.publish("app_net_temperatura",tempBuff);
    delay(1000);
    mqttClient.publish("app_net_humedad",humBuff);
    Serial.println("Envio exitoso");
    }

 float promedio(float v1,float v2){
      return ((v1+v2)/2);
      }
      


//Envia el valor de la temperatura del sensor al envia al broker  
void enviarTemperaturaMqtt(){
  float h1 = dht.readHumidity();//Se lee la humedad y se asigna el valor a "h"
  float t1 = dht.readTemperature();//Se lee la temperatura y se asigna el valor a "t"
  delay(1000);
  float h2 = dht2.readHumidity(); //Se lee la humedad y se asigna el valor a "h"
  float t2 = dht2.readTemperature(); //Se lee la temperatura y se asigna el valor a "t"
  while(isnan(h1)|| isnan(t1)){
    Serial.println("Lectura Fallida en el sensor 1 ");
      h1 = dht.readHumidity(); 
       delay(1000);
     t1 = dht.readTemperature(); 
   }
   while(isnan(h2)|| isnan(t2)){
    Serial.println("Lectura Fallida en el sensor 2 ");
    delay(1000);
      h2 = dht2.readHumidity(); 
      t2 = dht2.readTemperature(); 
   }
  
  hump=promedio(h1,h2);
  delay(1000);
  temp=promedio(t1,t2);
  publicarMqtt(temp,hump);
 
 }



  
