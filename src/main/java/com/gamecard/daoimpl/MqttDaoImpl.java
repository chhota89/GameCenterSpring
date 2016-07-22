package com.gamecard.daoimpl;

import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Repository;

import com.gamecard.dao.MqttDao;

@Repository
public class MqttDaoImpl implements MqttDao {

	public void isSubcribe(String topic) {
		
		System.out.println("topic url match" + topic);
		String broker = "tcp://localhost:1883";
		String clientId = "mqttpublish";
		String content;
		// 0(only one time) 1(at least one time) 2(exactly one time)
		int qos = 2;
		// String topic="xyz";
		try {
			MqttClient client = new MqttClient(broker, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			System.out.println("Connecting to broker: " + broker);
			client.connect(connOpts);
			System.out.println("You can type message now");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			content = scanner.nextLine();
			while (true) {
				System.out.println("Publishing message: " + content);
				MqttMessage message = new MqttMessage(content.getBytes());
				message.setQos(qos);
				client.publish(topic, message);
				System.out.println("Press 1 to exit");
				System.out.println("Publishing message:");
				content = scanner.nextLine();
				if (content.equalsIgnoreCase("1"))
					break;

			} // end of while loop
			client.disconnect();
			System.out.println("connection is Disconnected to broker");
			System.exit(0);
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}

	
	}

}
