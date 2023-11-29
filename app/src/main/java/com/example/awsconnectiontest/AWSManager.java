package com.example.awsconnectiontest;
import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class AWSManager {

    private static final String TAG = "AWSManager";
    private AWSIotMqttManager mqttManager;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private String clientId;
    private String topic = "iot/test"; // Replace with your desired topic

    public AWSManager(Context context) {

        // Replace these values with your actual Cognito Identity Pool ID and AWS region
        String cognitoIdentityPoolId = "us-east-1:1c352f41-a44b-4a8f-9594-c715b0f87257";
        Regions region = Regions.US_EAST_1;

        // Set up AWS credentials using Cognito Identity Pool
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                cognitoIdentityPoolId,
                region
        );

        clientId = UUID.randomUUID().toString();                //vvv iot end point
        mqttManager = new AWSIotMqttManager(clientId, "a3j5gn8c1p7i99-ats.iot.us-east-1.amazonaws.com");
        mqttManager.setCredentialsProvider(credentialsProvider);
    }

    public void connectToAwsIot(AWSIotMqttClientStatusCallback statusCallback) {
        try {
            mqttManager.connect(credentialsProvider, statusCallback);
        } catch (Exception e) {
            Log.e(TAG, "Exception during AWS IoT connection: " + e.getMessage());
        }
    }

    public void subscribeToTopic() {
        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0, (topic, data) -> {
                try {
                    String message = new String(data, "UTF-8");
                    Log.d(TAG, "Message received on topic " + topic + ": " + message);
                    // Handle the received message as needed
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error subscribing to topic: " + e.getMessage());
        }
    }

    public void publishMessage(String message) {
        try {
            mqttManager.publishString(message, topic, AWSIotMqttQos.QOS0);
            Log.d(TAG, "Message published: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Error publishing message: " + e.getMessage());
        }
    }

    public void disconnectFromAwsIot() {
        try {
            mqttManager.disconnect();
            Log.d(TAG, "Disconnected from AWS IoT");
        } catch (Exception e) {
            Log.e(TAG, "Error disconnecting from AWS IoT: " + e.getMessage());
        }
    }
}

