/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dataaccess.messaging;

import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@EnableBinding(DataEnvelopeAcknowledgmentPublisherChannel.class)
public class DataEnvelopeAcknowledgementPublisher {}
