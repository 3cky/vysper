/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.handler;

import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.modules.core.base.handler.IQHandler;
import org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.AbstractPublishSubscribeTestCase;
import org.apache.vysper.xmpp.modules.extension.xep0060_pubsub.model.LeafNode;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.protocol.ResponseStanzaContainer;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

/**
 * @author The Apache MINA Project (http://mina.apache.org)
 */
public class PubSubRetrieveSubscriptionsTestCase extends AbstractPublishSubscribeTestCase {
    protected LeafNode n1 = null;

    protected LeafNode n2 = null;

    protected LeafNode n3 = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        n1 = new LeafNode(serviceConfiguration, "Node1", "Node 1 used for testing purposes", client);
        n2 = new LeafNode(serviceConfiguration, "Node2", "Node 2 used for testing purposes", client);
        n3 = new LeafNode(serviceConfiguration, "Node3", "Node 3 used for testing purposes", client);

        root.add(n1);
        root.add(n2);
        root.add(n3);
    }

    @Override
    protected IQHandler getHandler() {
        return new PubSubRetrieveSubscriptionsHandler(serviceConfiguration);
    }

    @Override
    protected AbstractStanzaGenerator getDefaultStanzaGenerator() {
        return new DefaultSubscribeStanzaGenerator();
    }

    public void testNoSubscriptions() {
        AbstractStanzaGenerator sg = getDefaultStanzaGenerator();
        Stanza stanza = sg.getStanza(client, pubsubService, "id123", null);
        ResponseStanzaContainer result = sendStanza(stanza, true);

        assertTrue(result.hasResponse());

        IQStanza response = new IQStanza(result.getResponseStanza());
        assertEquals(IQStanzaType.RESULT.value(), response.getType());
        XMLElement sub = response.getFirstInnerElement().getFirstInnerElement();
        assertEquals("subscriptions", sub.getName());
        assertEquals(0, sub.getInnerElements().size()); // there should be no subscriptions
    }

    public void testManySubscriptions() {
        AbstractStanzaGenerator sg = getDefaultStanzaGenerator();

        n1.subscribe("id123", client);
        n1.subscribe("id124", client);
        n2.subscribe("id125", client);
        n3.subscribe("id126", client);

        Stanza stanza = sg.getStanza(client, pubsubService, "4711", null);
        ResponseStanzaContainer result = sendStanza(stanza, true);

        assertTrue(result.hasResponse());

        IQStanza response = new IQStanza(result.getResponseStanza());
        assertEquals(IQStanzaType.RESULT.value(), response.getType());
        XMLElement sub = response.getFirstInnerElement().getFirstInnerElement();
        assertEquals("subscriptions", sub.getName());
        assertEquals(4, sub.getInnerElements().size());
    }

    public void testSubscriptionsForOneNode() {
        AbstractStanzaGenerator sg = getDefaultStanzaGenerator();

        n1.subscribe("id123", client);
        n1.subscribe("id124", client);
        n2.subscribe("id125", client);
        n3.subscribe("id126", client);

        Stanza stanza = sg.getStanza(client, pubsubService, "4711", "Node1");
        ResponseStanzaContainer result = sendStanza(stanza, true);

        assertTrue(result.hasResponse());

        IQStanza response = new IQStanza(result.getResponseStanza());
        assertEquals(IQStanzaType.RESULT.value(), response.getType());
        XMLElement sub = response.getFirstInnerElement().getFirstInnerElement();
        assertEquals("subscriptions", sub.getName());
        assertEquals(2, sub.getInnerElements().size());
    }

    class DefaultSubscribeStanzaGenerator extends AbstractStanzaGenerator {
        @Override
        protected StanzaBuilder buildInnerElement(Entity client, Entity pubsub, StanzaBuilder sb, String node) {
            sb.startInnerElement("subscriptions", NamespaceURIs.XEP0060_PUBSUB);
            if (node != null) {
                sb.addAttribute("node", node);
            }
            sb.endInnerElement();
            return sb;
        }

        @Override
        protected String getNamespace() {
            return NamespaceURIs.XEP0060_PUBSUB;
        }

        @Override
        protected IQStanzaType getStanzaType() {
            return IQStanzaType.GET;
        }
    }

}
