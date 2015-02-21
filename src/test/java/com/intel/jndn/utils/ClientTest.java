/*
 * File name: ClientTest.java
 * 
 * Purpose: Test Client.java
 * 
 * © Copyright Intel Corporation. All rights reserved.
 * Intel Corporation, 2200 Mission College Boulevard,
 * Santa Clara, CA 95052-8119, USA
 */
package com.intel.jndn.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import com.intel.jndn.mock.MockTransport;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import org.junit.rules.ExpectedException;

/**
 * Test Client.java
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class ClientTest {

  /**
   * Setup logging
   */
  private static final Logger logger = Logger.getLogger(Client.class.getName());

  /**
   * Test retrieving data synchronously
   */
  @Test
  public void testGetSync() {
    // setup face
    MockTransport transport = new MockTransport();
    Face face = new Face(transport, null);

    // setup return data
    Data response = new Data(new Name("/test/sync"));
    response.setContent(new Blob("..."));
    transport.respondWith(response);

    // retrieve data
    logger.info("Client expressing interest synchronously: /test/sync");
    Client client = new Client();
    Data data = client.getSync(face, new Name("/test/sync"));
    assertEquals(new Blob("...").buf(), data.getContent().buf());
  }

  /**
   * Test retrieving data asynchronously
   *
   * @throws InterruptedException
   */
  @Test
  public void testGetAsync() throws InterruptedException, ExecutionException {
    // setup face
    MockTransport transport = new MockTransport();
    Face face = new Face(transport, null);

    // setup return data
    Data response = new Data(new Name("/test/async"));
    response.setContent(new Blob("..."));
    transport.respondWith(response);

    // retrieve data
    logger.info("Client expressing interest asynchronously: /test/async");
    Client client = new Client();
    FutureData futureData = client.getAsync(face, new Name("/test/async"));
    
    assertTrue(!futureData.isDone());
    futureData.get();
    assertTrue(futureData.isDone());
    assertEquals(new Blob("...").toString(), futureData.get().getContent().toString());
  }

  /**
   * Test that asynchronous client times out correctly
   * 
   * @throws InterruptedException 
   */
  @Test
  public void testTimeout() throws InterruptedException, ExecutionException, TimeoutException {
    // setup face
    MockTransport transport = new MockTransport();
    Face face = new Face(transport, null);

    // retrieve non-existent data, should timeout
    logger.info("Client expressing interest asynchronously: /test/timeout");
    FutureData futureData = Client.getDefault().getAsync(face, new Name("/test/timeout"));
    
    ExpectedException.none().expect(TimeoutException.class);
    futureData.get(50, TimeUnit.MILLISECONDS);
  }
}
