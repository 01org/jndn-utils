/*
 * jndn-utils
 * Copyright (c) 2016, Intel Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 */
package com.intel.jndn.utils.processing.impl;

import com.intel.jndn.utils.ProcessingStage;
import com.intel.jndn.utils.ProcessingStageException;
import net.named_data.jndn.Data;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;

/**
 * As a part of a server pipeline, this stage will sign a {@link Data} packet.
 *
 * @author Andrew Brown, andrew.brown@intel.com
 */
public class SigningStage implements ProcessingStage<Data, Data> {

  private final KeyChain keyChain;
  private final Name certificateName;

  /**
   * Default constructor.
   *
   * @param keyChain the {@link KeyChain} to use for signing
   * @param certificateName the certificate to sign with
   */
  public SigningStage(KeyChain keyChain, Name certificateName) {
    this.keyChain = keyChain;
    this.certificateName = certificateName;
  }

  /**
   * Build the stage using the default certificate name defined on the
   * {@link KeyChain}.
   *
   * @param keyChain the {@link KeyChain} to use for signing
   * @throws SecurityException if no default certificate is found
   */
  public SigningStage(KeyChain keyChain) throws SecurityException {
    this.keyChain = keyChain;
    this.certificateName = keyChain.getDefaultCertificateName();
  }

  /**
   * Sign a {@link Data} packet.
   *
   * @param context the data packet to sign
   * @return the signed data packet
   * @throws ProcessingStageException if signing fails
   */
  @Override
  public Data process(Data context) throws ProcessingStageException {
    try {
      keyChain.sign(context, certificateName);
    } catch (SecurityException e) {
      throw new ProcessingStageException(e);
    }
    return context;
  }
}
