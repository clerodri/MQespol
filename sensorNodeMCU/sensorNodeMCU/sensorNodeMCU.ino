
#include "conf.h"
#include "funcionesWifi.hpp"
#include "funcionesMqtt.hpp"

void setup() 
{   
    Serial.begin(115200);
    ConnectWiFi_SOFTAP();
    setupMqtt();
    
}
 
void loop() 
{      
  validarConexionMqtt();
  enviarTemperaturaMqtt();
  delay(1000);
}
