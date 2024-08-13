/**
 * Created by K. Suwatchai (Mobizt)
 *
 * Email: k_suwatchai@hotmail.com
 *
 * Github: https://github.com/mobizt/Firebase-ESP-Client
 *
 * Copyright (c) 2023 mobizt
 *
 */

#include "FirebaseAccess.h"
#include <Arduino.h>
#include <ESP8266WiFi.h>

#include <Firebase_ESP_Client.h>

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID "Home"
#define WIFI_PASSWORD "adm1806*"

#define ROOT_PATH "/awning"
#define PROGRESS_PATH "/progress"
#define STATUS_PATH "/status"
#define REQUESTED_STATUS_PATH "/requested_status"
#define DURATION_PATH "/duration"
#define NETWORK_PATH "/network"

// Define Firebase Data object
FirebaseData stream;
FirebaseData fbdo;
FirebaseJsonData jsonData;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;

uint8_t status = -1;
uint8_t requestedStatus = -1;
uint32_t duration = -1;
uint32_t pinMs = 0;

#define CLOSE_PIN D1
#define OPEN_PIN D2

typedef enum
{
  STOP,
  CLOSE,
  OPEN
} Event;

void streamCallback(FirebaseStream data)
{
  printResult(data); // see addons/RTDBHelper.h

  if (strcasecmp(data.dataPath().c_str(), "/") == 0)
  {
    FirebaseJson *json = data.jsonObjectPtr();

    json->get(jsonData, "status");
    status = (uint8_t)jsonData.intValue;

    json->get(jsonData, "requested_status");
    requestedStatus = (uint8_t)jsonData.intValue;

    json->get(jsonData, "duration");
    duration = (uint32_t)jsonData.intValue;
  }

  else if (strcmp(data.dataPath().c_str(), REQUESTED_STATUS_PATH) == 0)
  {
    requestedStatus = (uint8_t)data.intData();
  }

  else if (strcmp(data.dataPath().c_str(), STATUS_PATH) == 0)
  {
    status = (uint8_t)data.intData();
  }

  else if (strcmp(data.dataPath().c_str(), DURATION_PATH) == 0)
  {
    duration = (uint32_t)data.intData();
  }

  else if (strcmp(data.dataPath().c_str(), PROGRESS_PATH) == 0)
  {
  }

  else
  {
    printResult(data);
  }
}

void streamTimeoutCallback(bool timeout)
{
  if (timeout)
    Serial.println("stream timed out, resuming...\n");

  if (!stream.httpConnected())
    Serial.printf("error code: %d, reason: %s\n\n", stream.httpCode(), stream.errorReason().c_str());
}

void setup()
{

  Serial.begin(115200);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  WiFi.setAutoConnect(true);
  WiFi.persistent(true);

  Serial.print("Connecting to Wi-Fi");

  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  /* Assign the api key (required) */
  config.api_key = FIREBASE_API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = FIREBASE_EMAIL;
  auth.user.password = FIREBASE_PASSWORD;

  /* Assign the RTDB URL (required) */
  config.database_url = FIREBASE_DATABASE_URL;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

  Firebase.begin(&config, &auth);

  Firebase.reconnectWiFi(true);

  // Recommend for ESP8266 stream, adjust the buffer size to match your stream data size
  stream.setBSSLBufferSize(2048 /* Rx in bytes, 512 - 16384 */, 512 /* Tx in bytes, 512 - 16384 */);

  if (!Firebase.RTDB.beginStream(&stream, "awning"))
    Serial.printf("sream begin error, %s\n\n", stream.errorReason().c_str());

  Firebase.RTDB.setStreamCallback(&stream, streamCallback, streamTimeoutCallback);

  pinMode(D1, OUTPUT);
  pinMode(D2, OUTPUT);
}

void loop()
{

  // Firebase.ready() should be called repeatedly to handle authentication tasks.
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 15 * 60 * 1000 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();
    Serial.printf("Set json... %s\n\n", Firebase.RTDB.setInt(&fbdo, "/awning/network", WiFi.RSSI()) ? "ok" : fbdo.errorReason().c_str());
  }

  if (requestedStatus != status)
  {
    switch (requestedStatus)
    {
    case STOP:
      pinMs -= 2 * duration;
      digitalWrite(CLOSE_PIN, LOW);
      digitalWrite(OPEN_PIN, LOW);
      break;

    case CLOSE:
      digitalWrite(CLOSE_PIN, HIGH);
      digitalWrite(OPEN_PIN, LOW);
      break;

    case OPEN:
      digitalWrite(CLOSE_PIN, LOW);
      digitalWrite(OPEN_PIN, HIGH);
      break;

    default:
      break;
    }

    if (pinMs == 0)
    {
      pinMs = millis();
    }

    Serial.printf("Set json... %s\n\n", Firebase.RTDB.setInt(&fbdo, "/awning/progress", (int)(((float)millis() - pinMs) / duration * 100)) ? "ok" : fbdo.errorReason().c_str());
    if (millis() - pinMs >= duration)
    {
      Serial.printf("Set json... %s\n\n", Firebase.RTDB.setInt(&fbdo, "/awning/status", requestedStatus) ? "ok" : fbdo.errorReason().c_str());
      pinMs = 0;
      digitalWrite(CLOSE_PIN, LOW);
      digitalWrite(OPEN_PIN, LOW);
    }
  }
  else
  {
    digitalWrite(CLOSE_PIN, LOW);
    digitalWrite(OPEN_PIN, LOW);
  }
}