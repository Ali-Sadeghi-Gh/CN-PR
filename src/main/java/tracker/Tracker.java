package tracker;

import common.File;
import common.GetFileResult;

import java.util.*;

public class Tracker {
    private static final int PORT = 6771;
    private static Integer lastId = 0;
    private static final Map<String, Set<Integer>> peers = new HashMap<>();
    private static final Map<String, File> files = new HashMap<>();
    private static final Map<Integer, String> addresses = new HashMap<>();

    public static void main(String[] args) {
        new Thread(new CLI()).start();
        new Thread(new Manager()).start();
    }

    static void end() {
        System.exit(0);
    }

    static int getPORT() {
        return PORT;
    }

    static Integer getId() {
        return ++lastId;
    }

    static void addAddress(Integer id, String address) {
        addresses.putIfAbsent(id, address);
    }

    static void endPeer(Integer id) {
        addresses.remove(id);
        for (String fileName : peers.keySet()) {
            peers.get(fileName).remove(id);
            if (peers.get(fileName).isEmpty()) {
                peers.remove(fileName);
                files.remove(fileName);
            }
        }
    }

    static void addFile(Integer id, File file) {
        peers.putIfAbsent(file.name(), new HashSet<>());
        peers.get(file.name()).add(id);
        files.putIfAbsent(file.name(), file);
    }

    static GetFileResult getFile(String fileName) {
        Set<Integer> ids = peers.get(fileName);
        File file = files.get(fileName);
        if (ids == null || file == null)
            return GetFileResult.builder().build();

        return GetFileResult.builder()
                .found(true)
                .file(file)
                .addresses(idsToAddresses(ids))
                .build();
    }

    private static Set<String> idsToAddresses(Set<Integer> ids) {
        Set<String> result = new HashSet<>();
        for (Integer id : ids) {
            if (isValid(id))
                result.add(addresses.get(id));
        }
        return result;
    }

    static boolean isValid(Integer id) {
        return addresses.containsKey(id);
    }
}