/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.solace.training;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.ConsumerFlowProperties;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.XMLMessageListener;

public class TakeAttendance {

    public static void main(String... args) throws JCSMPException, InterruptedException, FileNotFoundException, UnsupportedEncodingException {
        // Check command line arguments
        if (args.length != 1) {
            System.out.println("Usage: TakeAttendance <host:port>");
            System.out.println();
            System.exit(-1);
        }

        System.out.println("TakeAttendance initializing...");
        // Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, args[0]);     		 // host:port
        properties.setProperty(JCSMPProperties.USERNAME, "instructor");  // client-username 
        properties.setProperty(JCSMPProperties.VPN_NAME,  "attendance"); // message-vpn
        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
        session.connect();

        final String queueName = "attendance_q";
        // create the queue object locally
        final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        final CountDownLatch latch = new CountDownLatch(1); // used for synchronizing b/w threads

        System.out.printf("Attempting to bind to the queue '%s'.%n", queueName);

        // Create a Flow be able to bind to and consume messages from the Queue.
        final ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
        flow_prop.setEndpoint(queue);
        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

        EndpointProperties endpoint_props = new EndpointProperties();
        endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_EXCLUSIVE);

        PrintWriter writer = new PrintWriter("attendance.txt", "UTF-8");
        writer.println("Attendance:");
        
        final FlowReceiver cons = session.createFlow(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                System.out.printf("%s%n", msg.getDestination());
                writer.println(msg.getDestination());
                // When the ack mode is set to SUPPORTED_MESSAGE_ACK_CLIENT,
                // guaranteed delivery messages are acknowledged after
                // processing
                msg.ackMessage();
                latch.countDown(); // unblock main thread
            }

            @Override
            public void onException(JCSMPException e) {
                System.out.printf("Consumer received exception: %s%n", e);
                latch.countDown(); // unblock main thread
            }
        }, flow_prop, endpoint_props);

        // Start the consumer
        System.out.println("Connected. Awaiting message ...");
        cons.start();

        try {
            latch.await(); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
            System.out.println("I was awoken while waiting");
        }
        // Close consumer
        Thread.sleep(3000);
        writer.close();
        cons.close();
        System.out.println("Exiting.");
        session.closeSession();
    }
}
