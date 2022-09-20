#include <PubSubClient.h>
WiFiClient espClient; 
PubSubClient mqttClient(espClient);


//Funcion que realiza la conexion al broker sino muestra un mensaje de error de conexion y vuelve a intentar la conexion
void conectarMqtt(){
   while (!mqttClient.connected())
    {
      //Serial.println("Conexion  MQTT, Espere..");
      if (mqttClient.connect(MQTT_CLIENT_NAME))
      {
        Serial.println("Conectado al Broker");
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
    mqttClient.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);
   
  }
//Funcion que valida que el cliente mqtt este conectado al broker, sino se realiza nuevamente la conexion al broker  
void validarConexionMqtt(){
    if (!mqttClient.connected())
        {
          conectarMqtt();
        }
    mqttClient.loop();
  }
  
//Publicar un dato Gas a un topico del broker
void publicarMqtt(float gas)
{
    Serial.println("Enviando dato de Gas al broker");
    Serial.print("Valor a enviar:");
    Serial.println(gas);
     char gasstring[4];
    String vgas=dtostrf(gas,5,2,gasstring);
    mqttClient.publish("app_net_gas",(char*)vgas.c_str());
    Serial.println("Dato Enviado!!!"); 
    }

    void promedio(float a){
        for (int x=0; x< 100;x++)
        {
          gas_value = gas_value + a; 
        }
        gas_value = gas_value /100.0;
      }
      
  long MQGetPercentage(float rs_ro_ratio,float *pcurve)
  {
    return (pow(10,(((log(rs_ro_ratio)-pcurve[1])/pcurve[2]) + pcurve[0])));
    }      
//Envia el valor del gas MQ3  al broker  
void enviarGastMqtt(){
  long iPPM_CO =0; //VARIABLE PPM
  gas_analog = analogRead(gasPin); 
  while(isnan(gas_analog)){
    Serial.println("Lectura Fallida en el sensor ");
    delay(1000);
      gas_analog = analogRead(gasPin); 
    }
   promedio(gas_analog);
 //  gas_volt = (gas_value/1024)*5.0; 
  //  RS = ((5.0 - gas_volt)/gas_volt);
   // ratio= RS/10;
  // iPPM_CO = MQGetPercentage(ratio,COCurve);
  //Serial.print("GAS: "); 
 // Serial.println(gas_analog);
//  Serial.println(" PPM");
  publicarMqtt(gas_analog);
  delay(1000);
 
  }

  /*
 void publicarPrueba(String s){
    Serial.print("Valor a enviar:");
    Serial.println(s);
    mqttClient.publish("pi/esp32/gas",(char*)s.c_str());
    Serial.print("Enviando dato....");
    delay(2000);
    Serial.println("Envio exitoso"); 
  }*/


  
