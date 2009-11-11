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
package org.apache.vysper.xmpp.xmlfragment;

import junit.framework.TestCase;

/**
 */
public class RendererTestCase extends TestCase {

	public void testRenderAttribute() {
		XMLElement elm = new XMLElement("foo", null, new Attribute[]{new Attribute("attr1", "value1")}, null);
		assertEquals("<foo attr1=\"value1\"></foo>", new Renderer(elm).getComplete());
	}

	// & must be escaped
	public void testRenderAttributeWithAmpersand() {
		XMLElement elm = new XMLElement("foo", null, new Attribute[]{new Attribute("attr1", "val&ue1")}, null);
		assertEquals("<foo attr1=\"val&amp;ue1\"></foo>", new Renderer(elm).getComplete());
	}

	public void testRenderAttributeWithQuot() {
		XMLElement elm = new XMLElement("foo", null, new Attribute[]{new Attribute("attr1", "val\"ue1")}, null);
		assertEquals("<foo attr1=\"val&quot;ue1\"></foo>", new Renderer(elm).getComplete());
	}

	public void testRenderAttributeWithApos() {
		XMLElement elm = new XMLElement("foo", null, new Attribute[]{new Attribute("attr1", "val'ue1")}, null);
		assertEquals("<foo attr1=\"val'ue1\"></foo>", new Renderer(elm).getComplete());
	}

	// > is not required to be escaped, but we do so to make sure
	public void testRenderAttributeWithGt() {
		XMLElement elm = new XMLElement("foo", null, new Attribute[]{new Attribute("attr1", "val>ue1")}, null);
		assertEquals("<foo attr1=\"val&gt;ue1\"></foo>", new Renderer(elm).getComplete());
	}

	// < must be escaped
	public void testRenderAttributeWithLt() {
		XMLElement elm = new XMLElement("foo", null, new Attribute[]{new Attribute("attr1", "val<ue1")}, null);
		assertEquals("<foo attr1=\"val&lt;ue1\"></foo>", new Renderer(elm).getComplete());
	}

}