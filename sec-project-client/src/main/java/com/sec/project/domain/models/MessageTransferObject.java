package com.sec.project.domain.models;

public record MessageTransferObject(byte[] data, byte[] signature) {
}
