package com.sec.project.models.records;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

public record Snapshot(HashMap<Integer, Integer> accounts, List<byte[]> signatures) {
}
