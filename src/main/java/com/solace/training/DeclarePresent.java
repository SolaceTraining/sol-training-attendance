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
import java.io.UnsupportedEncodingException;

import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

public class DeclarePresent {

    public static void main(String... args) throws JCSMPException, InterruptedException, FileNotFoundException, UnsupportedEncodingException {
        // Check command line arguments
        if (args.length != 2) {
            System.out.println("Usage: TakeAttendance <host:port> <firstname/lastname>");
            System.out.println();
            System.exit(-1);
        }

        System.out.println("TakeAttendance initializing...");
        // Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, args[0]);     		 // host:port
        properties.setProperty(JCSMPProperties.USERNAME, "student");  // client-username 
        properties.setProperty(JCSMPProperties.VPN_NAME,  "attendance"); // message-vpn
        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
        session.connect();
        
        String name = args[1];
        Topic topic = JCSMPFactory.onlyInstance().createTopic(name);
        
        final XMLMessageProducer prod = session.getMessageProducer(
                new JCSMPStreamingPublishEventHandler() {
                    public void responseReceived(String messageID) {
                        System.out.printf("Producer received response for msg ID #%s%n",messageID);
                    }
                    public void handleError(String messageID, JCSMPException e, long timestamp) {
                        System.out.printf("Producer received error for msg ID %s @ %s - %s%n",
                                messageID,timestamp,e);
                    }
                });       

        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setDeliveryMode(DeliveryMode.PERSISTENT);
       
        // Send message
        prod.send(msg, topic);
        System.out.println("Message sent. Exiting.");

        Thread.sleep(3000);
        System.out.println("Exiting.");
        session.closeSession();
    }
}
