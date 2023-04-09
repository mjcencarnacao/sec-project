package com.sec.project.domain.models;

/**
 * Object used to perform network data exchanges.
 *
 * @param data      to be sent.
 * @param signature of the data contained in this packet.
 */
public record MessageTransferObject(byte[] data, byte[] signature) {
}
