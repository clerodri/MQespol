  
#include "config.h"
#include "funcionesWifi.hpp"
#include "funcionesMqtt.hpp"
void setup()
{
  Serial.begin(115200);
  //ConnectWiFi_STA();
  ConnectWiFi_SOFTAP();
  setupMqtt();
 
}

void loop()
{
  
  validarConexionMqtt();
  enviarGastMqtt();
  delay(1000);
 }
